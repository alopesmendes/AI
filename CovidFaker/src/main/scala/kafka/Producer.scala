package kafka

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.{Serdes, StringSerializer}
import org.apache.kafka.streams.StreamsConfig

import java.io.StringWriter
import java.util.Properties

/**
 * The Producer send will the messages.
 * @author Ailton LOPES MENDES
 * @author Jonathan CHU
 * @author Fabien LAMBERT--DELAVAQUERIE
 * @author Gérald LIN
 */
object Producer {

    /***
     * Return the properties of a consumer.
     * Only done with StringSerializer for the moment.
     * @return Properties
     */
    def streamProducer: Properties = {
        val properties = new Properties
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka_id")
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
        properties.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0)
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
        properties.put("key.serializer", classOf[StringSerializer])
        properties.put("value.serializer", classOf[StringSerializer])

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
     */
    def send(mapper: ObjectMapper, topic : String, map: Map[String, String]): Unit = {
        val out: StringWriter = new StringWriter()
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
