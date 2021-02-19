import org.apache.jena.rdf.model.{ModelFactory, Resource}

import scala.jdk.CollectionConverters._

object Main extends App {
    val model = ModelFactory.createDefaultModel()
    model.read("file:lubm1.ttl", "TTL")

    //    recuperation de la liste des personnes
    val typeProperty = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type"
    val persons = List("http://swat.cse.lehigh.edu/onto/univ-bench.owl#FullProfessor"
        ,
        "http://swat.cse.lehigh.edu/onto/univ-bench.owl#AssociateProfessor"
        ,
        "http://swat.cse.lehigh.edu/onto/univ-bench.owl#AssistantProfessor"
        ,
        "http://swat.cse.lehigh.edu/onto/univ-bench.owl#Lecturer"
        ,
        "http://swat.cse.lehigh.edu/onto/univ-bench.owl#UndergraduateStudent"
        ,
        "http://swat.cse.lehigh.edu/onto/univ-bench.owl#GraduateStudent")

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
    val vaccine = "http://swat.cse.lehigh.edu/onto/univ-bench.owl#vaccine"
    val baseUri = "http://extension.group1.fr/onto#"

    val map = PersonAttribute.toUri()
    listOfPersons().foreach(person => {

        PersonModel.addAll(model, map, person.toString)
    })
    println("rdf dataset size = " + model.size())
    ModelFileWriter.write("modelVaccine.rdf", model)
    //PersonModel.model.listStatements().forEach(println)

}
