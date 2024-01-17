package avro.generic.serdes

import groovy.json.JsonSlurper
import groovy.transform.MapConstructor
import groovy.transform.NamedVariant
import org.apache.avro.Schema
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer

class GenericDeserializer implements Deserializer<Map> {
    String schema
    boolean serializedSchema = true

    @NamedVariant
    Map deserialize(String topic, byte[] data) {
        try {
            GenericRecord genericRecord = serializedSchema ? Generic.deserialize(data) : Generic.deserializeNoSchema(data, new Schema.Parser().parse(schema))
            return new JsonSlurper().parseText(genericRecord.toString()) as Map
        } catch (Exception e) {
            throw new SerializationException("Error when deserializing ${data}", e)
        }
    }
}
