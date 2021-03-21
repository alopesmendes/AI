package kafka

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.jena.rdf.model.Model
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

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
    /*
    val jsonFormatSchema = new String(Files.readAllBytes(Paths.get("src/main/resources/schema.avsc")))

    val schema: Schema = new Schema.Parser().parse(jsonFormatSchema)

    val recordInjection : Injection[GenericRecord, Array[Byte]] = GenericAvroCodecs.toBinary(schema)

    val avroRecord = new GenericData.Record(schema)

    */
    /*
    val streamConfig = new StreamsConfig(streamProducer)
    val serde = Serdes.ByteArray()

     */

    /***
     * Will parse and send the value.
     * @param mapper will parse to a json
     * @param model the Model
     * @param value the ProducerValue
     */
    def send(mapper: ObjectMapper, model: Model, topic : String, map: Map[String, String]): Unit = {
        mapper.writeValue(out, map)
        val json = out.toString()
        /*
        map.foreachEntry((k, v) => {
            avroRecord.put(k, v)
        })

        val byteArray = recordInjection.apply(avroRecord)
        val record = new ProducerRecord[String, Array[Byte]](topic, "key", byteArray)
        */

        val record = new ProducerRecord[String, String](topic, "key", json)
        producer.send( record )
        println("producing " + json)
    }
}
