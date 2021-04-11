package person

import kafka.KStreamUtils
import kafka.Utils.mapper
import org.apache.kafka.streams.kstream.KStream
import person.URI.baseUri

import java.time.{LocalDate, Period, Year}
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * The attributes of a person.
 * @author Ailton LOPES MENDES
 * @author Jonathan CHU
 * @author Fabien LAMBERT--DELAVAQUERIE
 * @author Gérald LIN
 */
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

        val format : DateTimeFormatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)

        private def parse(birthdayDate: String) : LocalDate = {
            LocalDate.parse(birthdayDate, format)
        }

        private def isBetween(ld1: LocalDate, ld2: LocalDate, interval : Int): Boolean ={
            Period.between(ld1, ld2).getYears <= interval
        }

        /**
         * Checks if the difference of years between [[year]] and the date of [[valueJson]] is inferior to [[interval]].
         * @param year the [[Year]]
         * @param valueJson a json [[String]] with ''birthday'' in the json.
         * @return true if it's in the inferior to the [[interval]] otherwise false.
         */
        def isBetween(year : Year, valueJson : String, interval : Int): Boolean = {
            val jsonTree = mapper.readTree(valueJson)
            jsonTree.has(PersonAttribute.Birthday.name) &&
            PersonAttribute.Birthday.isBetween(
                LocalDate.parse(s"$year-01-01"),
                LocalDate.parse(jsonTree.get(PersonAttribute.Birthday.name).asText(), format),
                interval
            )
        }

        /**
         *
         * @param source
         * @param interval
         * @return
         */
        def everyIntervalOfYearsStream(source : KStream[String, String], years : List[Year], interval : Int) : Map[Year, KStream[String, String]] = {
            val map = collection.mutable.Map[Year, KStream[String, String]]()
            for (year <- years) {
                map.getOrElseUpdate(year, KStreamUtils.filter(source, (_, value) =>  PersonAttribute.Birthday.isBetween(year, value, interval)))
            }
            map.toMap
        }
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

    /***
     * Generates a map with PersonAttribute as key and the values the URI
     * @return Map[PersonAttribute, String]
     */
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
