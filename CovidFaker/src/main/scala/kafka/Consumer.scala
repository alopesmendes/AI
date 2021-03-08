package kafka

import org.apache.kafka.clients.consumer.KafkaConsumer

import java.util
import java.util.Properties

object Consumer {
    /***
     * Return the properties of a consumer.
     * Only done with StringDeserializer for the moment.
     * @return Properties.
     */
    def streamConsumer: Properties = {
        val properties = new Properties
        properties.put("bootstrap.servers", "localhost:9092")
        properties.put("group.id", "mygroup")
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer")
        properties
    }

    val consumer = new KafkaConsumer[String, Array[Byte]](streamConsumer)

    /***
     * Display's all the consumers.
     */
    def consumeDisplay : Unit = {
        consumer.subscribe(util.Arrays.asList("test"))
        while (true) {
            val consumerRecord = consumer.poll(100)
            if (consumerRecord.isEmpty) {
                return
            }
            consumerRecord.forEach((record) => {

                System.out.println("Record Key " + record.key)
                System.out.println("Record value " + new String(record.value))
                System.out.println("Record partition " + record.partition)
                System.out.println("Record offset " + record.offset)

            })
            consumer.commitAsync()
        }
    }
}
