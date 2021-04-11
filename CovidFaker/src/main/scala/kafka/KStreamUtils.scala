package kafka

import kafka.Utils.mapper
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream._
import org.apache.kafka.streams.{KeyValue, StreamsConfig}

import java.util.Properties

/**
 * The class that will deal with the kafka streams functions.
 *
 * @author Ailton LOPES MENDES
 * @author Jonathan CHU
 * @author Fabien LAMBERT--DELAVAQUERIE
 * @author Gérald LIN
 */
object KStreamUtils {

    val props: Properties = new Properties()
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka_id")
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0)
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

    /**
     * Will filter with [[predicate]] and return a stream with id as key.
     * @param stream the source [[KStream]]
     * @param predicate the [[Predicate]] that will filter our stream.
     * @return a [[KStream]]
     */
    def filter(stream : KStream[String, String], predicate: Predicate[String, String]) : KStream[String, String] = {
        stream
        .filter(predicate)
        .map((_, value) => KeyValue.pair(mapper.readTree(value).get("id").asText(), value))
    }

    /**
     * Will transform the value of the [[KStream]] with [[valueTransform]].
     * The key of the stream will always be the id.
     * @param source the source [[KStream]]
     * @param valueTransform a function that receives two [[String]] and returns a [[String]]
     * @return a [[KStream]]
     */
    def transform(
                 source: KStream[String, String],
                 valueTransform: (String, String) => String): KStream[String, String] = {

        source.map((key, value) => KeyValue.pair(mapper.readTree(value).get("id").asText(), valueTransform(key, value)))

    }

    /**
     * Will join the two stream.
     * The values of both streams will be separated by '/'.
     * @param kStream1 a [[KStream]]
     * @param kStream2 a [[KStream]]
     * @return the joined [[KStream]]
     */
    def joinStream(
                  kStream1: KStream[String, String],
                  kStream2: KStream[String, String],
                  ): KStream[String, String] = {

        val kTable1 = kStream1.toTable
        val kTable2 = kStream2.toTable
        val joiner = new ValueJoiner[String, String, String] {
            override def apply(value1: String, value2: String): String = s"$value1/$value2"
        }
        kTable1.join(kTable2, joiner).toStream
    }

    /**
     * Will group the stream with [[keyValueMapper]] and count the number of occurrences.
     * @param countStream the [[KStream]] that we will count the occurrences.
     * @param keyValueMapper the [[KeyValueMapper]] that we will group by our stream.
     * @return a [[KStream]]
     */
    def countStream(countStream: KStream[String, String], keyValueMapper: KeyValueMapper[String, String, String]): KStream[String, java.lang.Long] = {
        val counts = countStream
        .groupBy(keyValueMapper)
        .count()
        counts.toStream
    }
}
