enablePlugins( JavaServerAppPackaging )

lazy val commonSettings = Seq(
  organization := "at.linuxhacker",
  version := "0.1",
  scalaVersion := "2.11.6",
  name := "nxlog-to-es"
)
maintainer in Linux := "Herbert Straub <herbert@linuxhacker.at>"

packageSummary in Linux := "Custom application configuration"

packageDescription := "Custom application configuration"

rpmVendor := "Herbert Straub"

rpmLicense := Some( "GNU/GPLv3" )


mainClass in Compile := Some( "at.linuxhacker.nxlogToEs.bin.Server" )

lazy val root = (project in file(".")).
  settings(commonSettings: _*)
  

mergeStrategy in assembly := { 
  case PathList( "META-INF", "MANIFEST.MF" ) => MergeStrategy.discard
  case _ => MergeStrategy.first
}

mappings in Universal <+= (packageBin in Compile, sourceDirectory ) map { (_, src) =>
    val conf = src / "main" / "resources" / "application.conf"
    conf -> "conf/application.conf"
}

//javaOptions in Universal ++= Seq("-Dconfig.file=${{app_home}}/conf/application.conf")

resolvers ++= Seq("snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "releases" at "http://oss.sonatype.org/content/repositories/releases",
  "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases" )
  
val akkaVersion = "2.3.9" 

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-kernel" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" % "akka-slf4j_2.11" % akkaVersion,
  "com.typesafe.play" %% "play-json" % "2.4.0-M2",
  "org.scalaj" %% "scalaj-http" % "1.1.4",
  "joda-time" % "joda-time" % "2.8.1"
)