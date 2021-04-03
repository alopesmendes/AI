package kafka

import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer}
import org.apache.kafka.common.serialization.{Serdes, StringDeserializer}
import org.apache.kafka.streams.StreamsConfig

import java.util
import java.util.Properties

/**
 * The Consumer the data and display's it.
 * @author Ailton LOPES MENDES
 * @author Jonathan CHU
 * @author Fabien LAMBERT--DELAVAQUERIE
 * @author Gérald LIN
 */
object Consumer {

    /***
     * Return the properties of a consumer.
     * The value.value.deserializer is a [[CustomDeserializer]].
     * @return Properties.
     */
    def streamConsumer: Properties = {
        val properties = new Properties
        properties.putIfAbsent(StreamsConfig.APPLICATION_ID_CONFIG, "kafka_id")
        properties.putIfAbsent(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
        properties.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0)
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
        properties.putIfAbsent("group.id", "mygroup")
        properties.put("key.deserializer", classOf[StringDeserializer])
        properties.put("value.deserializer", classOf[CustomDeserializer])
        properties
    }

    val consumer = new KafkaConsumer[String, String](streamConsumer)



    /***
     * Display's all the consumers.
     * @param list The [[List]] of topics.
     */
    def consumeDisplay(list: util.List[String]) : Unit = {
        consumer.subscribe(list)
        while (true) {
            val consumerRecord = consumer.poll(100)
            if (consumerRecord.isEmpty) {
                return
            }
            consumerRecord.forEach(record => {
                println(s"topic:${record.topic()} {")
                println(s"\tkey:${record.key()}, value:${record.value()}")
                println(s"\tpartition:${record.partition()}, offset:${record.offset()}")
                println("}")

            })
            consumer.commitAsync()
        }

    }
}
