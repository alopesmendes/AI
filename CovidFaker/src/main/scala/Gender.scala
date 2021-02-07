import scala.util.Random

trait Gender {
    val sex : String
}

object Gender {
    val random = new Random()

    case object Male extends Gender {
        override val sex: String = "male"
        val percentage = 48
    }

    case object Female extends Gender {
        override val sex: String = "female"
    }

    def randomGender(): Gender = {
        val value = random.nextInt(100) <= Male.percentage
        value match {
            case true => Male
            case false => Female
        }
    }

    def toUri(baseUri : String, gender: Gender): String = {
        return s"$baseUri${gender.sex}"
    }
}
