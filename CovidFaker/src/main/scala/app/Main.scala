package app

import kafka.KStreamUtils.props
import kafka.Utils._
import kafka.{Consumer, KStreamUtils, Producer}
import load.Load
import org.apache.jena.rdf.model.{ModelFactory, Resource}
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.{Consumed, KStream, Produced}
import org.apache.kafka.streams.{KafkaStreams, StreamsBuilder}
import person.URI.{persons, typeProperty}
import person.Vaccine.SideEffects
import person.{PersonAttribute, Vaccine}

import java.util
import java.util.concurrent.CountDownLatch
import scala.annotation.tailrec
import scala.jdk.CollectionConverters.CollectionHasAsScala


object Main extends App {
    val model = ModelFactory.createDefaultModel()
    model.read("file:lubm1.ttl", "TTL")

    //    recuperation de la liste des personnes

    def listOfPersons(): List[Resource] = {
        val rdfType = model.createProperty(typeProperty)

        @tailrec
        def aux(lst: List[String], acc: List[Resource]): List[Resource] = {
            lst match {
                case h :: t => val obj = model.createResource(h)
                    val iterator = model.listSubjectsWithProperty(rdfType, obj)
                    aux(t, iterator.toList.asScala.toList ::: acc)
                case Nil => acc
            }
        }

        aux(persons, List.empty[Resource])
    }

    println(listOfPersons().size)

    // donnees pour les vaccins

    val map = PersonAttribute.toUri()

    // Write file in resources
    /*
    listOfPersons().foreach(person => {

        PersonModel.addAll(model, map, person.toString)
    })
    println("rdf dataset size = " + model.size())
    ModelFileWriter.write("/modelVaccine.rdf", model, "RDF/XML-ABBREV")

    */

    // Read rdf file in resources
    val modelRDF = ModelFactory.createDefaultModel()
    modelRDF.read("file:src/main/resources/modelVaccine.rdf", "RDF/XML-ABBREV")


    /*
    val jsonFormatSchema = new String(Files.readAllBytes(Paths.get("src/main/resources/schema.avsc")))

    val schema: Schema = new Schema.Parser().parse(jsonFormatSchema)

    val genericRecord: GenericRecord = new GenericData.Record(schema)
     */

    Load.load("/modelVaccine.rdf", "select ?id ?fName ?lName ?vaccinationDate ?o where {" +
        "?s <http://extension.group1.fr/onto#vaccine> ?o."+
        s"?s <http://extension.group1.fr/onto#id> ?id."+
        s"?s <http://extension.group1.fr/onto#${PersonAttribute.FirstName.name}> ?fName." +
        s"?s <http://extension.group1.fr/onto#${PersonAttribute.LastName.name}> ?lName." +
        s"?s <http://extension.group1.fr/onto#${PersonAttribute.VaccineDate.name}> ?vaccinationDate."+
        "}",
        result => {
            try while ( {
                result.hasNext
            }) {
                // will send a ProducerValue if there's a sideEffect see Vaccine for more details.
                val bs = result.next
                val id = bs.getValue("id").stringValue()
                val fName = bs.getValue("fName").stringValue()
                val lName = bs.getValue("lName").stringValue()
                val vaccinationDate = bs.getValue("vaccinationDate").stringValue()
                val obj = bs.getValue("o").stringValue()
                val vaccine = Vaccine.findVaccine(obj)
                val sideEffect = Vaccine.SideEffects.randomSideEffect(vaccine)
                if (sideEffect != SideEffects.Nil && vaccine != Vaccine.Nil) {

                    //val bi = Bijection[GenericRecord, String](genericRecord)
                    Producer.send(mapper, inputTopic,
                        Map(
                            "id" -> id,
                            "fName" -> fName,
                            "lName" -> lName,
                            "vaccinationDate" -> vaccinationDate,
                            "vaccine" -> vaccine.name,
                            "sideEffectName" -> sideEffect.name,
                            "sideEffectCode" -> sideEffect.sideCode
                        )
                    )
                }
            }
            finally result.close()
        }
    )

    Load.load("/modelVaccine.rdf", "select ?id ?birthday ?o where {" +
    "?s <http://extension.group1.fr/onto#vaccine> ?o." +
    s"?s <http://extension.group1.fr/onto#id> ?id." +
    s"?s <http://extension.group1.fr/onto#${PersonAttribute.Birthday.name}> ?birthday." +
    "}",
        result => {
            try while ( {
                result.hasNext
            }) {
                // will send a ProducerValue if there's a sideEffect see Vaccine for more details.
                val bs = result.next
                val id = bs.getValue("id").stringValue()
                val birthday = bs.getValue("birthday").stringValue()
                val obj = bs.getValue("o").stringValue()
                val vaccine = Vaccine.findVaccine(obj)
                val sideEffect = Vaccine.SideEffects.randomSideEffect(vaccine)
                if (sideEffect != SideEffects.Nil && vaccine != Vaccine.Nil) {

                    //val bi = Bijection[GenericRecord, String](genericRecord)
                    Producer.send(mapper, inputTopic,
                        Map(
                            "id" -> id,
                            "birthday" -> birthday,
                            "vaccine" -> vaccine.name,
                            "sideEffectName" -> sideEffect.name,
                            "sideEffectCode" -> sideEffect.sideCode
                        )
                    )
                }
            }
            finally result.close()
        }
    )


    Consumer.consumeDisplay(util.Arrays.asList(outputFilterTopic, outputCountTopic))

    val latch: CountDownLatch = new CountDownLatch(1)

    val builder: StreamsBuilder = new StreamsBuilder
    val topicStream1: KStream[String, String] = builder.stream(inputTopic, Consumed.`with`(Serdes.String, Serdes.String))
    val topicStream2: KStream[String, String] = builder.stream(inputTopic2, Consumed.`with`(Serdes.String, Serdes.String))
    val source = topicStream1.merge(topicStream2)


    KStreamUtils.siderCodeStream(source, outputFilterTopic,
        (key, value) => mapper.readTree(value).get("sideEffectCode").toString.equals("\"C0027497\"")
    )



    val sideEffectsStream = KStreamUtils.transform(source,
        (_, value) => s"${mapper.readTree(value).get("sideEffectCode")}"
    )

    val perVaccine = KStreamUtils.transform(
        sideEffectsStream,
        (key, value) => s"$key/${mapper.readTree(value).get("vaccine")}")


    val counts = KStreamUtils.countStream(perVaccine,
        (key: String, v: String) => {
            val array = key.split("/").toList
            array(1)
        })
    counts.to(outputCountTopic, Produced.`with`(Serdes.String, Serdes.Long))

    val streams = new KafkaStreams(builder.build, props)

    // attach shutdown handler to catch control-c
    Runtime.getRuntime.addShutdownHook(new Thread("streams-shutdown-hook") {
        override def run(): Unit = {
            streams.close()
            latch.countDown()
        }
    })


    try {
        streams.start()
        latch.await()
    } catch {
        case _: Throwable =>
            System.exit(1)
    }

    Producer.producer.close()

    Consumer.consumer.close()

}
