import scala.util.Random

trait Gender {
    val sex : String
}

object Gender {
    val random = new Random()

    case object Male extends Gender {
        override val sex: String = "male"
    }

    case object Female extends Gender {
        override val sex: String = "female"
    }

    def randomGender(): Gender = {
        val value = random.nextBoolean()
        value match {
            case true => Male
            case false => Female
        }
    }
}
