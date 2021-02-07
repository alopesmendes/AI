import org.apache.jena.rdf.model.{Model, ModelFactory, Property, RDFNode, Resource}

object PersonModel {
    val model = ModelFactory.createDefaultModel()

    val resource : String => Resource = (person) => model.createResource(person)

    def add[T](property : String, fun :  Model => T): (Property, T) = (model.createProperty(property), fun(model))

    def addStatement(resource: Resource, property: Property, value : RDFNode): Unit = {
        val statement = model.createStatement(resource, property, value)
        model.add(statement)
    }
}
