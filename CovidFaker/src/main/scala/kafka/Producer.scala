package kafka

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.jena.rdf.model.Model
import org.apache.kafka.clients.producer.KafkaProducer

import java.io.StringWriter
import java.util.Properties

object Producer {
    val out: StringWriter = new StringWriter()

    /***
     * Return the properties of a consumer.
     * Only done with StringSerializer for the moment.
     * @return Properties
     */
    def streamProducer: Properties = {
        val properties = new Properties
        properties.put("bootstrap.servers", "localhost:9092")
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
        properties
    }

    val producer = new KafkaProducer[String, String](streamProducer)

    /***
     * Will parse and send the value.
     * @param mapper will parse to a json
     * @param model the Model
     * @param value the ProducerValue
     */
    def send(mapper: ObjectMapper, model: Model, value : ProducerValue): Unit = {

        import org.apache.kafka.clients.producer.ProducerRecord
        mapper.writeValue(out, value)
        val json = out.toString()
        val record = new ProducerRecord[String, String]("test", "key", json)
        producer.send( record )
        println("producing " + json)
    }
}
