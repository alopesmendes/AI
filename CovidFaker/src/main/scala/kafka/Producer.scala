package kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.twitter.bijection.avro.GenericAvroCodecs
import com.twitter.bijection.Injection
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericRecord
import org.apache.avro.Schema
import org.apache.jena.rdf.model.Model
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import java.io.StringWriter
import java.nio.file.{Files, Paths}
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
        properties.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer")
        properties
    }

    val producer = new KafkaProducer[String, Array[Byte]](streamProducer)

    val jsonFormatSchema = new String(Files.readAllBytes(Paths.get("src/main/resources/schema.avsc")))

    val schema: Schema = new Schema.Parser().parse(jsonFormatSchema)

    val recordInjection : Injection[GenericRecord, Array[Byte]] = GenericAvroCodecs.toBinary(schema)

    val avroRecord = new GenericData.Record(schema)

    /***
     * Will parse and send the value.
     * @param mapper will parse to a json
     * @param model the Model
     * @param value the ProducerValue
     */
    def send(mapper: ObjectMapper, model: Model, value : ProducerValue): Unit = {
        /*
        import org.apache.kafka.clients.producer.ProducerRecord
        mapper.writeValue(out, value)
        val json = out.toString()

         */
        avroRecord.put("id", value.id)
        avroRecord.put("fName", value.fName)
        avroRecord.put("lName", value.lName)
        avroRecord.put("vaccinationDate", value.vaccinationDate)
        avroRecord.put("vaccine", value.vaccine)
        avroRecord.put("sideEffectName", value.sideEffect.name)
        avroRecord.put("sideEffectCode", value.sideEffect.sideCode)

        val byteArray = recordInjection.apply(avroRecord)
        val record = new ProducerRecord[String, Array[Byte]]("test", "key", byteArray)
        producer.send( record )
        //println("producing " + json)
    }
}
