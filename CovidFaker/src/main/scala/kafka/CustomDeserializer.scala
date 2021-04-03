package kafka

import kafka.Utils._
import org.apache.kafka.common.serialization.{Deserializer, LongDeserializer, StringDeserializer}

/**
 * Will deserializer according to the topic to a [[LongDeserializer]] and [[StringDeserializer]].
 * @author Ailton LOPES MENDES
 * @author Jonathan CHU
 * @author Fabien LAMBERT--DELAVAQUERIE
 * @author Gérald LIN
 */
class CustomDeserializer extends Deserializer[Any]{
    /**
     * Deserialize to [[StringDeserializer]] or [[LongDeserializer]] according to [[topic]].
     * @param topic the name of the Topic.
     * @param data the date of the Topic.
     * @return the data deserialize.
     */
    override def deserialize(topic: String, data: Array[Byte]): Any = {
        typeOf(topic) match {
            case StringData => new StringDeserializer().deserialize(topic, data)
            case LongData => new LongDeserializer().deserialize(topic, data)
        }

    }
}
