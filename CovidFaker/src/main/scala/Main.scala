import com.github.javafaker.Faker
import org.apache.jena.rdf.model.{ModelFactory, Resource}

import java.text.SimpleDateFormat
import scala.jdk.CollectionConverters._

object Main extends App {
    val model = ModelFactory.createDefaultModel()
    model.read("file:lubm1.ttl", "TTL")
    //println(model.size())

    //    premiere utilisation de Faker

    val faker = new Faker()
    /*
    val name = faker.name().fullName()
    val fName = faker.name().firstName()
    val lName = faker.name().lastName()
    val streetAddress = faker.address().streetAddress()
    println(name)
    println(fName)
    println(lName)
    println(streetAddress)

     */

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
    val id = "http://extension.group1.fr/onto#id"
    val firstName = "http://extension.group1.fr/onto#firstName"
    val lastName = "http://extension.group1.fr/onto#lastName"
    val gender = "http://extension.group1.fr/onto#gender"
    val birthday = "http://extension.group1.fr/onto#birthday"
    val zipcode = "http://extension.group1.fr/onto#zipcode"

    // donnees pour les vaccins
    val vaccine = "http://swat.cse.lehigh.edu/onto/univ-bench.owl#vaccine"
    val baseUri = "http://extension.group1.fr/onto#"

    val anotherModel = ModelFactory.createDefaultModel()
    val map = PersonAttribute.toUri(baseUri)
    listOfPersons().foreach(person => {
        val s = PersonModel.resource(person.toString)
        val idAttributes = PersonModel.add(
            map.get(PersonAttribute.Id).get,
            m => m.createLiteral(faker.idNumber().valid())
        )

        val firstNameAttributes = PersonModel.add(
            map.get(PersonAttribute.FirstName).get,
            m => m.createLiteral(faker.name().firstName())
        )

        val lastNameAttributes = PersonModel.add(
            map.get(PersonAttribute.LastName).get,
            m => m.createLiteral(faker.name().lastName())
        )

        val genderAttributes = PersonModel.add(
            map.get(PersonAttribute.Gender).get,
            m => m.createResource(Gender.toUri(baseUri, Gender.randomGender()))
        )


        val birthdayAttributes = PersonModel.add(
            map.get(PersonAttribute.Birthday).get,
            m => {
                if (person.toString.contains("Student"))
                    anotherModel.createLiteral(faker.date().birthday(20, 30).toString)
                else
                    anotherModel.createLiteral(faker.date().birthday(30, 70).toString)
            }
        )

        val zipcodeAttributes = PersonModel.add(
            map.get(PersonAttribute.Zipcode).get,
            m => m.createLiteral(faker.address().zipCode())
        )

        val vaccineAttributes = PersonModel.add(
            map.get(PersonAttribute.Vaccin).get,
            m => {
                val randomVaccine = Vaccine.randomVaccine()
                m.createResource(Vaccine.toUri(baseUri, randomVaccine))
            }
        )

        val sdf = new SimpleDateFormat("dd/MM/yyyy")
        val january  = sdf.parse("01/01/2021")
        val february = sdf.parse("01/02/2021")
        val vaccineDateAttributes = PersonModel.add(
            map.get(PersonAttribute.VaccineDate).get,
            m => m.createLiteral(faker.date().between(january, february).toString)
        )

        PersonModel.addStatement(s, idAttributes._1, idAttributes._2)
        PersonModel.addStatement(s, firstNameAttributes._1, firstNameAttributes._2)
        PersonModel.addStatement(s, lastNameAttributes._1, lastNameAttributes._2)
        PersonModel.addStatement(s, genderAttributes._1, genderAttributes._2)
        PersonModel.addStatement(s, birthdayAttributes._1, birthdayAttributes._2)
        PersonModel.addStatement(s, zipcodeAttributes._1, zipcodeAttributes._2)

        if (vaccineAttributes._2.getLocalName != Vaccine.Nil.name) {
            PersonModel.addStatement(s, vaccineAttributes._1, vaccineAttributes._2)
            PersonModel.addStatement(s, vaccineDateAttributes._1, vaccineDateAttributes._2)
        }
    })
    println("rdf dataset size = " + PersonModel.model.size())
    ModelFileWriter.write("modelVaccine.rdf", PersonModel.model)
    //PersonModel.model.listStatements().forEach(println)

}
