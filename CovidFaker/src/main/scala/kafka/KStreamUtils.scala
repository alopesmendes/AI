package kafka

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.{KeyValue, StreamsConfig}
import org.apache.kafka.streams.kstream.{KStream, KeyValueMapper, Predicate, Produced}

import java.util.Properties

/**
 * The class that will deal with the kafka streams functions.
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


    def siderCodeStream(source: KStream[String, String], outputTopic: String, predicate: Predicate[String, String]): Unit = {

        val filterStream = source.filter(predicate)

        filterStream.to(outputTopic, Produced.`with`(Serdes.String, Serdes.String))

    }

    def transform(
                 source: KStream[String, String],
                 keyTransform: ((String, String) => String)): KStream[String, String] = {


        source.map((key, value) => KeyValue.pair(keyTransform(key, value), value))
    }

    def sideEffectsPerVaccine(
                             keyValueMapper: KeyValueMapper[String, String, KeyValue[String, String]],
                             kStream: KStream[String, String]): KStream[String, String] = {
        /*
        val joiner = new ValueJoiner[String, String, String] {
            override def apply(value1: String, value2: String): String = s"$value1:$value2"
        }
        val vaccine = source.map(keyValueMapper)
        .join(kStream.toTable(Named.as("count-effects")), joiner)
        .map((key, value) => KeyValue.pair(s"$key:allo", value))
        .groupByKey()
        .count()
        vaccine.toStream
        */
        kStream.map(keyValueMapper)
    }

    def countStream[K, V, VR](countStream: KStream[K, V], keyValueMapper: KeyValueMapper[K, V, VR]): KStream[VR, java.lang.Long] = {
        val counts = countStream.
        groupBy(keyValueMapper)
        .count()
        counts.toStream
    }
}
