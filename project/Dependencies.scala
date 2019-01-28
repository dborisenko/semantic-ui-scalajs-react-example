import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import sbt.Def.setting
import sbt.librarymanagement.DependencyBuilders

object Versions {
  val scala = "2.12.8"

  // Scala: JVM libraries
  object ScalaJvm {
    val scalatags = "0.6.7"
    val http4s = "0.20.0-M5"
    val pureconfig = "0.10.1"
  }

  // Scala: JS libraries
  object ScalaJs {
    val `scalajs-react-components` = "0.3.1"
  }

  // JavaScript: Node.js libraries
  object NodeJs {
    val react = "16.7.0"
    val `semantic-ui-react` = "0.84.0"
  }

}

object Dependencies extends DependencyBuilders {
  type NpmDependency = (String, String)

  // Scala: JVM libraries
  object ScalaJvm {
    val scalatags = "com.lihaoyi" %% "scalatags" % Versions.ScalaJvm.scalatags
    val `http4s-dsl` = "org.http4s" %% "http4s-dsl" % Versions.ScalaJvm.http4s
    val `http4s-blaze-server` = "org.http4s" %% "http4s-blaze-server" % Versions.ScalaJvm.http4s
    val pureconfig = "com.github.pureconfig" %% "pureconfig" % Versions.ScalaJvm.pureconfig
  }

  // Scala: JS libraries
  object ScalaJs {
    val `semantic-ui-react` = setting(
      "com.dbrsn.scalajs.react.components" %%% "semantic-ui-react" % Versions.ScalaJs.`scalajs-react-components`
    )
  }

  // JavaScript: Node.js libraries
  object NodeJs {
    val react: NpmDependency = "react" -> Versions.NodeJs.react
    val `react-dom`: NpmDependency = "react-dom" -> Versions.NodeJs.react
    val `semantic-ui-react`: NpmDependency = "semantic-ui-react" -> Versions.NodeJs.`semantic-ui-react`
  }

}
