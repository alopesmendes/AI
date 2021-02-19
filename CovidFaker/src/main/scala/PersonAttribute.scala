import URI.baseUri

trait PersonAttribute {
    val name : String
}

object PersonAttribute {

    case object Id extends PersonAttribute {
        override val name: String = "id"
    }

    case object FirstName extends PersonAttribute {
        override val name: String = "firstname"
    }

    case object LastName extends PersonAttribute {
        override val name: String = "lastname"
    }

    case object Gender extends PersonAttribute {
        override val name: String = "gender"
    }

    case object Birthday extends PersonAttribute {
        override val name: String = "birthday"
    }

    case object Zipcode extends PersonAttribute {
        override val name: String = "zipcode"
    }

    case object Vaccin extends PersonAttribute {
        override val name: String = "vaccine"
    }

    case object VaccineDate extends PersonAttribute {
        override val name: String = "vaccinationDate"
    }

    def toUri() : Map[PersonAttribute, String] = {
        Map(
            Id -> s"$baseUri${Id.name}",
            FirstName -> s"$baseUri${FirstName.name}",
            LastName -> s"$baseUri${LastName.name}",
            Gender -> s"$baseUri${Gender.name}",
            Birthday -> s"$baseUri${Birthday.name}",
            Zipcode -> s"$baseUri${Zipcode.name}",
            Vaccin -> s"$baseUri${Vaccin.name}",
            VaccineDate -> s"$baseUri${VaccineDate.name}"
        )
    }
}
