name := "harvard_E63_final_project"

version := "1.0"

//scalaVersion := "2.12.4"

scalaVersion := "2.11.8"

assemblyOption in assembly ~= { _.copy(includeScala = false) }


libraryDependencies ++= Seq(
  "org.apache.bahir" %% "spark-streaming-twitter" % "2.0.1"
)

libraryDependencies += "org.apache.spark" % "spark-core_2.11" % "2.0.2"
libraryDependencies += "org.apache.spark" % "spark-mllib_2.11" % "2.0.2"
libraryDependencies += "org.apache.spark" % "spark-sql_2.11" % "2.0.2"
libraryDependencies += "org.apache.bahir" % "spark-streaming-twitter_2.11" % "2.0.1"
libraryDependencies += "org.apache.kafka" % "kafka-clients" % "0.9.0.0"
libraryDependencies += "edu.stanford.nlp" % "stanford-corenlp" % "3.8.0"
libraryDependencies += "edu.stanford.nlp" % "stanford-corenlp" % "3.8.0" classifier "models"
libraryDependencies += "org.elasticsearch" % "elasticsearch-spark-20_2.11" % "6.0.0-rc2"

val json4sNative = "org.json4s" %% "json4s-native" % "{latestVersion}"

assemblyJarName in assembly := "twitterStream.jar"


assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

resolvers += Resolver.sonatypeRepo("releases")