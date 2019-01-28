package service

import cats.data.{NonEmptyList, ValidatedNel}
import cats.effect.{ContextShift, Sync}
import cats.implicits._
import org.http4s.dsl.Http4sDsl
import org.http4s.headers._
import org.http4s.{HttpRoutes, MediaType, Response, StaticFile}
import scalatags.Text
import scalatags.Text.all._
import shared.Shared

import scala.concurrent.ExecutionContext
import scala.language.higherKinds

final case class CannotFindJsError(projectName: String, ending: String)
    extends Exception(s"Cannot find .js file for project '$projectName' with ending '$ending'")

class BootstrapService[F[_]: Sync: ContextShift](
  blockingExecutionContext: ExecutionContext,
  projectName: String,
  renderContent: List[Modifier] => String
) extends Http4sDsl[F] {

  private type JsErrorOr[T] = ValidatedNel[CannotFindJsError, T]

  private def pathToBundleAsset(projectName: String, ending: String): JsErrorOr[String] = {
    val name = projectName.toLowerCase
    val fullEnding = (if (ending.length > 0) "-" + ending else "") + ".js"
    List(name + "-opt" + fullEnding, name + "-fastopt" + fullEnding)
      .filter(dn => getClass.getResource("/public/" + dn) != null)
      .map("/assets/" + _)
      .headOption
      .toValidNel(CannotFindJsError(projectName, ending))
  }

  private val contentTypeHtml = `Content-Type`(MediaType.text.html)

  private def scriptApplication: JsErrorOr[String] = pathToBundleAsset(projectName, "bundle")

  private def scriptLibraryOnly: JsErrorOr[List[String]] =
    (
      pathToBundleAsset(projectName, "library"),
      pathToBundleAsset(projectName, "loader"),
      pathToBundleAsset(projectName, "")
    ).mapN(List(_, _, _))

  private def scriptTags: JsErrorOr[List[Text.TypedTag[String]]] =
    scriptApplication.map(path => List(script(src := path))) orElse
      scriptLibraryOnly.map(_.map(path => script(src := path)))

  private def renderErrors(errors: NonEmptyList[CannotFindJsError]): String =
    renderContent(errors.map(error => p(error.getMessage)).toList)

  private lazy val bootStrap: F[Response[F]] = scriptTags.fold(
    errors => InternalServerError(renderErrors(errors), contentTypeHtml),
    tags => Ok(renderContent(div(id := Shared.BootstrapTagId) :: tags), contentTypeHtml)
  )

  val service: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root => bootStrap

    case req @ GET -> Root / "assets" / asset =>
      StaticFile
        .fromResource(
          name = s"/public/$asset",
          blockingExecutionContext = blockingExecutionContext,
          req = Some(req)
        )
        .getOrElse(Response(NotFound))
  }

}

object BootstrapService {
  def renderContent(pageTitle: String, styles: List[String])(content: List[Modifier]): String =
    html(
      head(title := pageTitle, styles.map { style =>
        link(rel := "stylesheet", href := style)
      }),
      body(content: _*)
    ).render
}
