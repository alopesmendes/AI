package kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

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
            case x if (x == outputCountTopic) => LongData
            case _ => StringData
        }
    }
}
