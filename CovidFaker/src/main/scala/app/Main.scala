package app

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import kafka.{Consumer, Producer, ProducerValue}
import load.Load
import org.apache.jena.rdf.model.{ModelFactory, Resource}
import person.URI.{persons, typeProperty}
import person.Vaccine.SideEffects
import person.{PersonAttribute, Vaccine}

import scala.jdk.CollectionConverters.CollectionHasAsScala


object Main extends App {
    val model = ModelFactory.createDefaultModel()
    model.read("file:lubm1.ttl", "TTL")

    //    recuperation de la liste des personnes

    def listOfPersons(): List[Resource] = {
        val rdfType = model.createProperty(typeProperty)

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

    // Creates JSON mapper will be used for the producer.
    val mapper = new ObjectMapper()
    mapper.registerModule(DefaultScalaModule)
    /*
    val jsonFormatSchema = new String(Files.readAllBytes(Paths.get("src/main/resources/schema.avsc")))

    val schema: Schema = new Schema.Parser().parse(jsonFormatSchema)

    val genericRecord: GenericRecord = new GenericData.Record(schema)
     */



    // Method will load the file using blaze then entering the query.
    // The last parameter will be a function that takes the results of the query and returns Unit
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
                    val producerValue = ProducerValue(id, fName, lName, vaccinationDate, vaccine.name, sideEffect)
                    Producer.send(mapper, modelRDF, producerValue)
                }
            }
            finally result.close()
        }
    )

    Consumer.consumeDisplay

    Producer.producer.close()

    Consumer.consumer.close()

}
