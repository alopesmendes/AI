name := "CovidFaker"

version := "0.1"

scalaVersion := "2.13.5"

// https://mvnrepository.com/artifact/org.apache.jena/jena-core
libraryDependencies += "org.apache.jena" % "jena-core" % "3.17.0"

// https://mvnrepository.com/artifact/com.github.javafaker/javafaker
libraryDependencies += "com.github.javafaker" % "javafaker" % "1.0.2"

libraryDependencies += "org.apache.kafka" % "kafka-clients" % "2.7.0"

libraryDependencies += "org.apache.kafka" % "kafka_2.13" % "2.7.0"

libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.10.0"

// https://mvnrepository.com/artifact/com.blazegraph/bigdata-core
libraryDependencies += "com.blazegraph" % "bigdata-core" % "2.1.4"

// https://mvnrepository.com/artifact/com.blazegraph/bigdata-client
libraryDependencies += "com.blazegraph" % "bigdata-client" % "2.1.4"

libraryDependencies += "org.apache.avro"  %  "avro"  %  "1.7.7"

// https://mvnrepository.com/artifact/com.twitter/bijection-core
libraryDependencies += "com.twitter" %% "bijection-core" % "0.9.7"

// https://mvnrepository.com/artifact/com.twitter/bijection-avro
libraryDependencies += "com.twitter" %% "bijection-avro" % "0.9.7"


