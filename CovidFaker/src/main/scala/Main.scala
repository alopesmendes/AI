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
    val fName = faker.name().firstName()
    val lName = faker.name().lastName()
    val streetAddress = faker.address().streetAddress()
    println(name)
    println(fName)
    println(lName)
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

    //    ajout de donnees pour les personnes
    val id = "http://swat.cse.lehigh.edu/onto/univ-bench.owl#id"
    val firstName = "http://swat.cse.lehigh.edu/onto/univ-bench.owl#firstName"
    val lastName = "http://swat.cse.lehigh.edu/onto/univ-bench.owl#lastName"
    val gender = "http://swat.cse.lehigh.edu/onto/univ-bench.owl#gender"
    val birthday = "http://swat.cse.lehigh.edu/onto/univ-bench.owl#birthday"
    val zipcode = "http://swat.cse.lehigh.edu/onto/univ-bench.owl#zipcode"

    val anotherModel = ModelFactory.createDefaultModel()
    listOfPersons().foreach(person => {
        val s = anotherModel.createResource(person.toString)

        val p = anotherModel.createProperty(id)
        val o = anotherModel.createResource(faker.idNumber().valid())

        val p1 = anotherModel.createProperty(firstName)
        val o1 = anotherModel.createResource(faker.name().firstName())

        val p2 = anotherModel.createProperty(lastName)
        val o2 = anotherModel.createResource(faker.name().lastName())

        val p3 = anotherModel.createProperty(birthday)
        val o3 =
            if (person.toString.contains("Student"))
                anotherModel.createResource(faker.date().birthday(20, 30).toString)
            else
                anotherModel.createResource(faker.date().birthday(30, 70).toString)

        val p4 = anotherModel.createProperty(zipcode)
        val o4 = anotherModel.createResource(faker.address().zipCode())

        val statement = anotherModel.createStatement(s, p, o)
        val statement1 = anotherModel.createStatement(s, p1, o1)
        val statement2 = anotherModel.createStatement(s, p2, o2)
        val statement3 = anotherModel.createStatement(s, p3, o3)
        val statement4 = anotherModel.createStatement(s, p4, o4)

        anotherModel.add(statement)
        anotherModel.add(statement1)
        anotherModel.add(statement2)
        anotherModel.add(statement3)
        anotherModel.add(statement4)
    })
    println("rdf dataset size = " + anotherModel.size())
    //    anotherModel.listStatements().forEach(println)

}
