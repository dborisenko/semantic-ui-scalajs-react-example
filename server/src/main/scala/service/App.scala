package service

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import pureconfig.ConfigReader
import pureconfig.error.ConfigReaderException
import pureconfig.generic.semiauto.deriveReader

import scala.concurrent.ExecutionContext.global

final case class HttpConfig(
  port: Int = HttpConfig.DefaultPort,
  host: String = HttpConfig.DefaultHost
)

object HttpConfig {
  final val DefaultPort: Int = 8080
  final val DefaultHost: String = "0.0.0.0"

  implicit lazy val configReaderHttpConfig: ConfigReader[HttpConfig] = deriveReader
}

object App extends IOApp {
  private val bootstrapService = new BootstrapService[IO](
    blockingExecutionContext = global,
    projectName = "explorer-client",
    renderContent = BootstrapService.renderContent(
      pageTitle = "Explorer",
      styles = List(
        "//cdn.jsdelivr.net/npm/semantic-ui@2.4.1/dist/semantic.min.css"
      )
    )
  ).service

  override def run(args: List[String]): IO[ExitCode] =
    for {
      config <- IO.fromEither(pureconfig.loadConfig[HttpConfig]("http").leftMap(ConfigReaderException(_)))
      exitCode <- BlazeServerBuilder[IO]
        .bindHttp(config.port, config.host)
        .withHttpApp(bootstrapService.orNotFound)
        .serve
        .compile
        .drain
        .as(ExitCode.Success)
    } yield exitCode
}
