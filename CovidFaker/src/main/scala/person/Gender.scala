package person

import person.URI.baseUri

import scala.util.Random

/**
 * Will define the gender.
 * @author Ailton LOPES MENDES
 * @author Jonathan CHU
 * @author Fabien LAMBERT--DELAVAQUERIE
 * @author Gérald LIN
 */
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

    /***
     * Returns a random gender between Male and Female.
     * @return Gender
     */
    def randomGender(): Gender = {
        val value = random.nextInt(100) <= Male.percentage
        value match {
            case true => Male
            case false => Female
        }
    }

    /**
     * Returns the URI for gender.
     * @param gender a Gender
     * @return String
     */
    def toUri(gender: Gender): String = {
        return s"$baseUri${gender.sex}"
    }
}
