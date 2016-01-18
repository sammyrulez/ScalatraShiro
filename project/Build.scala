import sbt.Keys._
import sbt._


object Build extends sbt.Build {
  val buildOrganisation = "com.github.sammyrulez"
  val buildVersion = "1.1.0"
  val buildScalaVersion = "2.11.6"
  val ScalatraVersion = "2.3.0"
  val buildScalaOptions = Seq(
    "-unchecked", "-deprecation"
    , "-encoding", "utf8"
    , "-Xelide-below", annotation.elidable.ALL.toString
  )


  lazy val main = Project(id = "scalatrashiro", base = file("."))
    .settings(
      libraryDependencies ++= Seq(
        "org.scalatra" %% "scalatra" % ScalatraVersion,
        "org.scalatra" %% "scalatra-scalate" % ScalatraVersion,
        "org.scalatra" %% "scalatra-auth" % ScalatraVersion,
        "org.scalatra" %% "scalatra-scalatest" % ScalatraVersion % "test",
        "junit" % "junit" % "4.12" % "test",
        "ch.qos.logback" % "logback-classic" % "1.0.6" % "runtime",
        "org.eclipse.jetty" % "jetty-webapp" % "8.1.8.v20121106" % "test",
        "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" % "provided;test" artifacts Artifact("javax.servlet", "jar", "jar"),
        "org.apache.shiro" % "shiro-core" % "1.2.4",
        "org.apache.shiro" % "shiro-web" % "1.2.4"
      )
      , organization := buildOrganisation
      , version := buildVersion
      , scalaVersion := buildScalaVersion
      , scalacOptions := buildScalaOptions
      , publishMavenStyle := true
      , publishArtifact in Test := false
      , publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
  }
      , pomExtra := (<url>https://github.com/sammyrulez/ScalatraShiro</url>
    <developers>
      <developer>
        <name>Sam Reghenzi</name>
        <email>sammyrulez@gmail.dot.com</email>
        <organization>sammyrulez</organization>
        <organizationUrl>https://github.com/sammyrulez/</organizationUrl>
      </developer>
    </developers>
    <licenses>
      <license>
        <name>Apache License, Version 2.0</name>
        <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <connection>scm:git:git@github.com:sammyrulez/ScalatraShiro.git</connection>
      <developerConnection>scm:git:git@github.com:sammyrulez/ScalatraShiro.git</developerConnection>
      <url>https://github.com/sammyrulez/ScalatraShiro</url>
    </scm>
    <ciManagement>
      <system>Travis-ci</system>
      <url>
        https://travis-ci.org/sammyrulez/ScalatraShiro
      </url>
    </ciManagement>)
      , credentials += Credentials("Sonatype Nexus Repository Manager",
    "oss.sonatype.org",
    System.getenv("SONATYPE_USER"),
    System.getenv("SONATYPE_PASS"))
    )
}