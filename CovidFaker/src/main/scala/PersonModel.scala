import com.github.javafaker.Faker
import org.apache.jena.rdf.model.{Model, Property, RDFNode, Resource}

import java.text.SimpleDateFormat

object PersonModel {
    val faker = new Faker()

    val resource : (String, Model) => Resource = (person, model) => model.createResource(person)

    def add[T](model: Model, property : String, fun :  Model => T): (Property, T) = (model.createProperty(property), fun(model))

    def addStatement(model: Model, resource: Resource, value: (Property, RDFNode)): Unit = {
        val statement = model.createStatement(resource, value._1, value._2)
        model.add(statement)
    }

    def addAll(model: Model, map : Map[PersonAttribute, String], person : String): Unit = {
        val s = PersonModel.resource(person.toString, model)
        val idAttributes = PersonModel.add(
            model,
            map.get(PersonAttribute.Id).get,
            m => m.createLiteral(faker.idNumber().valid())
        )

        val firstNameAttributes = PersonModel.add(
            model,
            map.get(PersonAttribute.FirstName).get,
            m => m.createLiteral(faker.name().firstName())
        )

        val lastNameAttributes = PersonModel.add(
            model,
            map.get(PersonAttribute.LastName).get,
            m => m.createLiteral(faker.name().lastName())
        )

        val genderAttributes = PersonModel.add(
            model,
            map.get(PersonAttribute.Gender).get,
            m => m.createResource(Gender.toUri(Gender.randomGender()))
        )


        val birthdayAttributes = PersonModel.add(
            model,
            map.get(PersonAttribute.Birthday).get,
            m => {
                if (person.toString.contains("Student"))
                    m.createLiteral(faker.date().birthday(20, 30).toString)
                else
                    m.createLiteral(faker.date().birthday(30, 70).toString)
            }
        )

        val zipcodeAttributes = PersonModel.add(
            model,
            map.get(PersonAttribute.Zipcode).get,
            m => m.createLiteral(faker.address().zipCode())
        )

        val vaccineAttributes = PersonModel.add(
            model,
            map.get(PersonAttribute.Vaccin).get,
            m => {
                val randomVaccine = Vaccine.randomVaccine()
                m.createResource(Vaccine.toUri(randomVaccine))
            }
        )

        val sdf = new SimpleDateFormat("dd/MM/yyyy")
        val january  = sdf.parse("01/01/2021")
        val february = sdf.parse("01/02/2021")
        val vaccineDateAttributes = PersonModel.add(
            model,
            map.get(PersonAttribute.VaccineDate).get,
            m => m.createLiteral(faker.date().between(january, february).toString)
        )

        PersonModel.addStatement(model, s, idAttributes)
        PersonModel.addStatement(model, s, firstNameAttributes)
        PersonModel.addStatement(model, s, lastNameAttributes)
        PersonModel.addStatement(model, s, genderAttributes)
        PersonModel.addStatement(model, s, birthdayAttributes)
        PersonModel.addStatement(model, s, zipcodeAttributes)

        if (vaccineAttributes._2.getLocalName != Vaccine.Nil.name) {
            PersonModel.addStatement(model, s, vaccineAttributes)
            PersonModel.addStatement(model, s, vaccineDateAttributes)
        }
    }
}
