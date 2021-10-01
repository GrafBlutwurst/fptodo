inThisBuild(
  List(
    scalaVersion := "2.13.6",
    semanticdbEnabled := true, // enable SemanticDB
    semanticdbVersion := scalafixSemanticdb.revision // use Scalafix compatible version
  )
)

scalaVersion := "2.13.6"

val catsEffectVersion = "3.0.1"
val catsVersion = "2.5.0"
val circeVersion = "0.14.0-M4"
val http4sVersion = "0.23.1"

val defaultScalaCOptions = Seq(
  "-Yrangepos", // Required for scalafix
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-encoding",
  "utf-8", // Specify character encoding used by source files.
  "-explaintypes", // Explain type errors in more detail.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-language:existentials", // Existential types (besides wildcard types) can be written and inferred
  "-language:implicitConversions", // Allow definition of implicit functions called views
  "-language:experimental.macros", // Allow macro definition (besides implementation and application)
  "-language:higherKinds", // Allow higher-kinded types
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xcheckinit", // Wrap field accessors to throw an exception on uninitialized access.
  //Conflicts with scalafix
  //"-Xfatal-warnings",                // Fail the compilation if there are any warnings.
  //"-Xfuture",                         // Turn on future language features.
  "-Xlint:adapted-args", // Warn if an argument list is modified to match the receiver.
  //"-Xlint:by-name-right-associative", // By-name parameter of right associative operator.
  "-Xlint:constant", // Evaluation of a constant arithmetic expression results in an error.
  "-Xlint:delayedinit-select", // Selecting member of DelayedInit.
  "-Xlint:doc-detached", // A Scaladoc comment appears to be detached from its element.
  "-Xlint:inaccessible", // Warn about inaccessible types in method signatures.
  "-Xlint:infer-any", // Warn when a type argument is inferred to be `Any`.
  "-Xlint:missing-interpolator", // A string literal appears to be missing an interpolator id.
  "-Xlint:nullary-unit", // Warn when nullary methods return Unit.
  "-Xlint:option-implicit", // Option.apply used implicit view.
  "-Xlint:package-object-classes", // Class or object defined in package object.
  "-Xlint:poly-implicit-overload", // Parameterized overloaded implicit methods are not visible as view bounds.
  "-Xlint:private-shadow", // A private field (or class parameter) shadows a superclass field.
  "-Xlint:stars-align", // Pattern sequence wildcard must align with sequence component.
  "-Xlint:type-parameter-shadow", // A local type parameter shadows a type already in scope.
  //"-Xlint:unsound-match",             // Pattern match may not be typesafe.
  //"-Yno-adapted-args",                // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
  //"-Ypartial-unification",            // Enable partial unification in type constructor inference
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-extra-implicit", // Warn when more than one implicit parameter section is defined.
  //"-Ywarn-inaccessible",              // Warn about inaccessible types in method signatures.
  //-Ywarn-infer-any",                 // Warn when a type argument is inferred to be `Any`.
  //"-Ywarn-nullary-unit",              // Warn when nullary methods return Unit.
  "-Ywarn-numeric-widen", // Warn when numerics are widened.
  "-Ywarn-unused:implicits", // Warn if an implicit parameter is unused.
  "-Ywarn-unused:imports", // Warn if an import selector is not referenced.
  "-Ywarn-unused:locals", // Warn if a local definition is unused.
  "-Ywarn-unused:params", // Warn if a value parameter is unused.
  "-Ywarn-unused:patvars", // Warn if a variable bound in a pattern is unused.
  "-Ywarn-unused:privates", // Warn if a private member is unused.
  "-Ywarn-value-discard", // Warn when non-Unit expression results are unused.
  "-Yrangepos", // Required for ScalaFix
  "-Vimplicits",
  "-Vtype-diffs",
  //"-Ystatistics:typer"                // Bloop Compile Time Statistics Typer Times
  "-Ymacro-annotations",
  "-Ywarn-macros:after"
)

lazy val root = project
  .in(file("."))
  .settings(
    name := "fp-todo",
    version := "0.1.0",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % catsEffectVersion,
      "org.typelevel" %% "cats-core" % catsVersion,
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "io.circe" %% "circe-literal" % circeVersion,
      "org.http4s" %% "http4s-blaze-client" % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion,
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-blaze-server" % http4sVersion,
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "org.endpoints4s" %% "algebra" % "1.5.0",
      "org.endpoints4s" %% "http4s-server" % "7.0.0",
      "org.endpoints4s" %% "http4s-client" % "5.0.0",
      "org.endpoints4s" %% "algebra-json-schema" % "1.5.0",
      "org.endpoints4s" %% "json-schema-generic" % "1.5.0",
      "org.endpoints4s" %% "json-schema-circe" % "1.5.0",
      "org.endpoints4s" %% "openapi" % "3.1.0",
      "com.disneystreaming" %% "weaver-cats" % "0.7.6" % Test
    ),
    testFrameworks += new TestFramework("weaver.framework.CatsEffect"),
    scalacOptions ++= defaultScalaCOptions,
    (assembly / test) := {},
    (assembly / assemblyOutputPath) := new java.io.File("target/app.jar"),
    (assembly / assemblyMergeStrategy) := {
      case "module-info.class" => MergeStrategy.discard
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case x =>
        val oldStrategy = (assembly / assemblyMergeStrategy).value
        oldStrategy(x)
    }
  )

addCommandAlias(
  "lint",
  List(
    "scalafix",
    "scalafmtAll",
    "scalafmtSbt"
  ).mkString("; ")
)

addCommandAlias(
  "lintCheck",
  List(
    "scalafix --check",
    "scalafmtCheckAll",
    "scalafmtSbtCheck"
  ).mkString("; ")
)
