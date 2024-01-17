import avro.generic.serdes.Generic
import avro.generic.serdes.GenericDeserializer
import avro.generic.serdes.GenericJsonStringDeserializer
import avro.generic.serdes.GenericJsonStringSerializer
import avro.generic.serdes.GenericSerializer
import groovy.json.JsonOutput
import org.apache.avro.Schema
import org.apache.avro.generic.GenericRecord
import spock.lang.Specification

class GenericJsonStringTest extends Specification {
    String schema
    String jsonString
    Map signingNotification

    def setup() {
        signingNotification = [
                header: [
                        id         : 'headerId',
                ],
                body  : [
                        signingId : 'signingId',
                        status: 'SIGNED',
                ]
        ]
        jsonString = JsonOutput.toJson(signingNotification)
        schema = this.class.getResource('/SigningNotification.avsc').text
    }

    def "serialize String and deserialize to String"() {
        when:
        byte[] serialized = new GenericJsonStringSerializer(schema: schema).serialize(topic: "topic", jsonString: jsonString)
        String deserialized = new GenericJsonStringDeserializer(schema: schema).deserialize("topic", null, serialized)
        then:
        deserialized == jsonString
    }

    def "serialize String and deserialize to Map"() {
        when:
        byte[] serialized = new GenericJsonStringSerializer(schema: schema).serialize(topic: "topic", jsonString: jsonString)
        Map deserialized = new GenericDeserializer(schema: schema).deserialize("topic", null, serialized)
        then:
        deserialized == signingNotification
    }

    def "serialize Map and deserialize to String"() {
        when:
        byte[] serialized = new GenericSerializer(schema: schema).serialize(topic: "topic", data:  signingNotification)
        String deserialized = new GenericJsonStringDeserializer(schema: schema).deserialize("topic", null, serialized)
        then:
        deserialized == jsonString
    }
}
