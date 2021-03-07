package person

import com.github.javafaker.Faker
import org.apache.jena.rdf.model.{Model, Property, RDFNode, Resource}

import java.text.SimpleDateFormat

object PersonModel {
    val faker = new Faker()
    /***
     * Creates a resource by taking a String and the Model
     */
    val resource: (String, Model) => Resource = (person, model) => model.createResource(person)

    /***
     * Returns a tuple of a property and a T.
     * Will create a property and T should be an literal or a resource.
     * @param model the Model
     * @param property the string to create a Property
     * @param fun a function should return a resource or literal.
     * @tparam T a resource or literal.
     * @return a tuple
     */
    def add[T](model: Model, property: String, fun: Model => T): (Property, T) = (model.createProperty(property), fun(model))

    /***
     * Adds a statement
     * @param model the Model
     * @param resource the Resource
     * @param value a tuple of property and RDFNode
     */
    def addStatement(model: Model, resource: Resource, value: (Property, RDFNode)): Unit = {
        val statement = model.createStatement(resource, value._1, value._2)
        model.add(statement)
    }

    /**
     * Adds all attributes of a person.
     * @param model the Model
     * @param map the Map of PersonAttribute and strings of a URI
     * @param person a type of person (student, professor ...)
     */
    def addAll(model: Model, map: Map[PersonAttribute, String], person: String): Unit = {
        val s = PersonModel.resource(person, model)
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
        val january = sdf.parse("01/01/2021")
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
