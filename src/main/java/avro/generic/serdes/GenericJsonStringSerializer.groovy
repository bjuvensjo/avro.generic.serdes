package avro.generic.serdes

import groovy.json.JsonSlurper
import groovy.transform.NamedVariant
import org.apache.avro.Schema
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Serializer

class GenericJsonStringSerializer implements Serializer<String> {
    String schema
    boolean serializeSchema = true

    @NamedVariant
    byte[] serialize(String topic, String jsonString) {
        try {
            Map data = new JsonSlurper().parseText(jsonString) as Map
            GenericRecord genericRecord = Generic.toGenericRecord(data, new Schema.Parser().parse(schema))
            return serializeSchema ? Generic.serialize(genericRecord) : Generic.serializeNoSchema(genericRecord)
        } catch (Exception e) {
            throw new SerializationException("Error when serializing ${jsonString}", e)
        }
    }
}
