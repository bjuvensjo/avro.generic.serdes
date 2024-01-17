import avro.generic.serdes.Generic
import org.apache.avro.Schema
import org.apache.avro.generic.GenericRecord
import spock.lang.Specification

class GenericTest extends Specification {
    Schema schema
    GenericRecord genericRecord

    def setup() {
        Map signingNotification = [
                header: [
                        id         : 'headerId',
                ],
                body  : [
                        signingId : 'signingId',
                        status: 'SIGNED',
                ]
        ]
        String schemaContent = this.class.getResource('/SigningNotification.avsc').text
        schema = new Schema.Parser().parse(schemaContent)
        genericRecord = Generic.toGenericRecord(signingNotification, schema)
    }

    def "serialize and deserialize"() {
        when:
        byte[] serialized = Generic.serialize(genericRecord)
        GenericRecord deserialized = Generic.deserialize(serialized)
        then:
        genericRecord.toString() == deserialized.toString()
    }

    def "serialize and deserialize no schema"() {
        when:
        byte[] serialized = Generic.serializeNoSchema(genericRecord)
        GenericRecord deserialized = Generic.deserializeNoSchema(serialized, schema)
        then:
        genericRecord.toString() == deserialized.toString()
    }
}
