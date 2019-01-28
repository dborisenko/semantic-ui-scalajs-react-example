// Scala.js, the Scala to JavaScript compiler
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.26")

// SBT plugin that can check Maven and Ivy repositories for dependency updates
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.3.4")

// SBT Native Packager
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.15")

// SBT plugin to use Scala.js along with any sbt-web server
addSbtPlugin("com.vmunier" % "sbt-web-scalajs" % "1.0.8-0.6")

// Module bundler for Scala.js projects that use NPM packages
addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.13.1")

// Cross-platform compilation support for sbt
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "0.6.0")

// Integrates Docker Compose functionality into sbt
addSbtPlugin("com.tapad" % "sbt-docker-compose" % "1.0.34")

// Module bundler for Scala.js projects that use NPM packages.
addSbtPlugin("ch.epfl.scala" % "sbt-web-scalajs-bundler" % "0.13.1")

// sbt-web plugin for adding checksum files for web assets
addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.4")

// sbt-web plugin for gzip compressing web assets
addSbtPlugin("com.typesafe.sbt" % "sbt-gzip" % "1.0.2")

// Scalafmt SBT plugin
addSbtPlugin("com.lucidchart" % "sbt-scalafmt" % "1.15")

// Scalastyle examines your Scala code and indicates potential problems with it
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")

// Flexible Scala code linting tool
addSbtPlugin("org.wartremover" % "sbt-wartremover" % "2.3.7")

// An SBT plugin for dangerously fast development turnaround in Scala
addSbtPlugin("io.spray" % "sbt-revolver" % "0.9.1")

// Generates Scala source from your build definitions
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.9.0")

// An sbt plugin for deploying Heroku Scala applications
addSbtPlugin("com.heroku" % "sbt-heroku" % "2.1.2")
