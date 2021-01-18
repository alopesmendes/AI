import com.github.javafaker.Faker
import org.apache.jena.rdf.model.{ModelFactory, Resource}
import scala.jdk.CollectionConverters._

object Main extends App {

    val model = ModelFactory.createDefaultModel()
    model.read("file:lubm1.ttl", "TTL")
    println(model.size())

    //    premiere utilisation de Faker
    val faker = new Faker()
    val name = faker.name().fullName()
    val firstName = faker.name().firstName()
    val lastName = faker.name().lastName()
    val streetAddress = faker.address().streetAddress()
    println(name)
    println(firstName)
    println(lastName)
    println(streetAddress)

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
        aux(persons, List[Resource]())
    }

    println(listOfPersons().size)
}
