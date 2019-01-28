import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject, _}
import scalajsbundler.sbtplugin.WebScalaJSBundlerPlugin
import scalajscrossproject.ScalaJSCrossPlugin.autoImport._
import wartremover.Wart

inThisBuild(
  List(
    scalaVersion := Versions.scala,
    organization := "com.dbrsn.scalajs.react.semanticui.example",
    scalacOptions := Seq(
      "-deprecation", // warning and location for usages of deprecated APIs
      "-encoding",
      "UTF-8",
      "-feature", // warning and location for usages of features that should be imported explicitly
      "-unchecked", // additional warnings where generated code depends on assumptions
      "-Xcheckinit", // Wrap field accessors to throw an exception on uninitialized access.
      "-Xlint", // recommended additional warnings
      "-Xfatal-warnings", // Fail the compilation if there are any warnings.
      "-Xfuture", // Turn on future language features.
      "-Xlint:adapted-args", // Warn if an argument list is modified to match the receiver.
      "-Xlint:by-name-right-associative", // By-name parameter of right associative operator.
      "-Xlint:delayedinit-select", // Selecting member of DelayedInit.
      "-Xlint:doc-detached", // A Scaladoc comment appears to be detached from its element.
      "-Xlint:inaccessible", // Warn about inaccessible types in method signatures.
      "-Xlint:infer-any", // Warn when a type argument is inferred to be `Any`.
      "-Xlint:missing-interpolator", // A string literal appears to be missing an interpolator id.
      "-Xlint:nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
      "-Xlint:nullary-unit", // Warn when nullary methods return Unit.
      "-Xlint:option-implicit", // Option.apply used implicit view.
      "-Xlint:package-object-classes", // Class or object defined in package object.
      "-Xlint:poly-implicit-overload", // Parameterized overloaded implicit methods are not visible as view bounds.
      "-Xlint:private-shadow", // A private field (or class parameter) shadows a superclass field.
      "-Xlint:stars-align", // Pattern sequence wildcard must align with sequence component.
      "-Xlint:type-parameter-shadow", // A local type parameter shadows a type already in scope.
      "-Xlint:unsound-match", // Pattern match may not be typesafe.
      "-Yno-adapted-args", // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
      "-Ypartial-unification", // Enable partial unification in type constructor inference
      "-Ywarn-dead-code", // Warn when dead code is identified.
      "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
      "-Ywarn-infer-any", // Warn when a type argument is inferred to be `Any`.
      "-Ywarn-nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
      "-Ywarn-nullary-unit", // Warn when nullary methods return Unit.
      "-Ywarn-numeric-widen", // Warn when numerics are widened.
      "-Ywarn-value-discard", // Warn when non-Unit expression results are unused.
      "-Xlint:constant", // Evaluation of a constant arithmetic expression results in an error.
      "-Ywarn-extra-implicit", // Warn when more than one implicit parameter section is defined.
      "-Ywarn-unused:implicits", // Warn if an implicit parameter is unused.
      "-Ywarn-unused:imports", // Warn if an import selector is not referenced.
      "-Ywarn-unused:locals", // Warn if a local definition is unused.
      "-Ywarn-unused:params", // Warn if a value parameter is unused.
      "-Ywarn-unused:patvars", // Warn if a variable bound in a pattern is unused.
      "-Ywarn-unused:privates", // Warn if a private member is unused.
      "-Ywarn-adapted-args" // Warn if an argument list is modified to match the receiver
    ),
    scalafmtOnCompile := true,
    scalafmtTestOnCompile := true
  )
)

def lintingSettings(compileWarts: Seq[Wart] = Warts.all, testWarts: Seq[Wart] = Warts.all): List[Setting[_]] = List(
  scalastyleFailOnError := true,
  scalastyleFailOnWarning := true,
  wartremoverErrors in (Compile, compile) := compileWarts,
  wartremoverErrors in (Test, compile) := testWarts
)

lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(crossType = CrossType.Pure)
  .in(file("shared"))
  .settings(lintingSettings())

lazy val client = project
  .in(file("client"))
  .enablePlugins(ScalaJSPlugin)
  .enablePlugins(ScalaJSWeb)
  .enablePlugins(ScalaJSBundlerPlugin)
  .settings(lintingSettings())
  .settings(
    libraryDependencies ++= Seq(
      Dependencies.ScalaJs.`semantic-ui-react`.value,
      Dependencies.ScalaJvmJs.`cats-core`.value
    ),
    npmDependencies in Compile ++= Seq(
      Dependencies.NodeJs.react,
      Dependencies.NodeJs.`react-dom`,
      Dependencies.NodeJs.`semantic-ui-react`
    ),
    webpackBundlingMode in Compile := BundlingMode.LibraryOnly(),
    emitSourceMaps := false,
    requiresDOM := true // Execute the tests in browser-like environment
  )
  .settings(
    scalacOptions ++= Seq(
      "-P:scalajs:sjsDefinedByDefault" // Declare a non-native JS type withou @ScalaJSDefined annotation
    )
  )
  .dependsOn(shared.js)

lazy val server = project
  .in(file("server"))
  .enablePlugins(DockerComposePlugin)
  .settings(lintingSettings(compileWarts = Warts.allBut(Wart.Any, Wart.DefaultArguments, Wart.Nothing, Wart.Equals)))
  .settings(
    libraryDependencies ++= Seq(
      Dependencies.ScalaJvm.`http4s-dsl`,
      Dependencies.ScalaJvm.`http4s-blaze-server`,
      Dependencies.ScalaJvm.pureconfig,
      Dependencies.ScalaJvm.scalatags
    )
  )
  .enablePlugins(WebScalaJSBundlerPlugin)
  .settings(
    // triggers scalaJSPipeline when using compile or continuous compilation
    compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,
    // connect to the client project
    scalaJSProjects := Seq(client),
    pipelineStages in Assets := Seq(scalaJSPipeline),
    pipelineStages := Seq(digest, gzip),
    WebKeys.packagePrefix in Assets := "public/",
    managedClasspath in Runtime += (packageBin in Assets).value
  )
  .dependsOn(shared.jvm)

lazy val root = project
  .in(file("."))
  .aggregate(shared.jvm)
  .aggregate(shared.js)
  .aggregate(client)
  .aggregate(server)
