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
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
        properties
    }

    val consumer = new KafkaConsumer[String, String](streamConsumer)

    /***
     * Display's all the consumers.
     */
    def consumeDisplay(list: util.List[String]) : Unit = {
        consumer.subscribe(list)
        while (true) {
            val consumerRecord = consumer.poll(200)
            if (consumerRecord.isEmpty) {
                return
            }
            consumerRecord.forEach((record) => {

                System.out.println("Record Key " + record.key)
                System.out.println("Record value " + record.value)
                System.out.println("Record partition " + record.partition)
                System.out.println("Record offset " + record.offset)

            })
            consumer.commitAsync()
        }
    }
}
