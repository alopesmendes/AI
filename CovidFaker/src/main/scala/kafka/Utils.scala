package kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import java.time.Year
import java.util
import scala.jdk.CollectionConverters.{CollectionHasAsScala, SeqHasAsJava}

/**
 * The Utilities object. Will stock the topics, mapper, etc.
 * @author Ailton LOPES MENDES
 * @author Jonathan CHU
 * @author Fabien LAMBERT--DELAVAQUERIE
 * @author Gérald LIN
 */
object Utils {
    val inputTopic = "stream-info-person-input"
    val inputTopic2 = "stream-sideEffect-input"
    val outputFilterTopic = "stream-info-person-filter-output"
    val outputCountTopic = "stream-sideEffect-count-output"
    val startYear = 1950
    val endYear = 2000

    /**
     *
     * @param interval of years
     * @return a [[List]] of all years within a interval.
     */
    def yearsInterval(interval : Int) : List[Year] = {
        val list = new util.ArrayList[Year]()
        for (i <- startYear to endYear by interval) {
            list.add(Year.of(i))
        }
        list.asScala.toList
    }

    /**
     *
     * @param years [[List]] of years
     * @return a [[Map]] with the yars as key and the output topic as value.
     */
    def selectCountMap(years : List[Year]) : Map[Year, String] = {
        val mutableMap = collection.mutable.Map[Year, String]()
        for (year <- years) {
            mutableMap.getOrElseUpdate(year, s"$outputCountTopic-$year")
        }
        mutableMap.toMap
    }

    /**
     * @return a [[util.List]] of all output topics.
     */
    def listOfOutputTopics(years : Map[Year, String]) : util.List[String] = {
        val list = years.values.toList
        list.::(outputFilterTopic).asJava
    }

    val mapper = new ObjectMapper()
    mapper.registerModule(DefaultScalaModule)

    trait TypeData

    case object LongData extends TypeData

    case object StringData extends TypeData

    /**
     * Will return the [[TypeData]] of the topic.
     * @param topic name of the Topic.
     * @return the [[TypeData]].
     */
    def typeOf(topic : String) : TypeData = {
        topic match {
            case x if (x == outputFilterTopic) => StringData
            case _ => LongData
        }
    }
}
