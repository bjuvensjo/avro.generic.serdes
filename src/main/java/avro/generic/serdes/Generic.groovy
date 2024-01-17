package avro.generic.serdes

import org.apache.avro.Schema
import org.apache.avro.file.DataFileReader
import org.apache.avro.file.DataFileWriter
import org.apache.avro.file.SeekableByteArrayInput
import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericDatumReader
import org.apache.avro.generic.GenericDatumWriter
import org.apache.avro.generic.GenericRecord
import org.apache.avro.io.*

/**
 * This class supports what currently is required by the Avro schemas used.
 * Extend if new requirements arises :).
 */
class Generic {

    static GenericRecord toGenericRecord(Map<String, Object> value, Schema schema) {
        return value.inject(new GenericData.Record(schema)) { record, k, v ->
            record.put(k, toFieldValue(v, record.getSchema().getField(k).schema()))
            return record
        }
    }

    static GenericRecord deserialize(byte[] bytes) {
        DatumReader<GenericRecord> datumReader = new GenericDatumReader<GenericRecord>()
        DataFileReader<GenericRecord> dataFileReader = new DataFileReader<GenericRecord>(new SeekableByteArrayInput(bytes), datumReader)
        // Change below if bytes can contain multiple records
        if (dataFileReader.hasNext()) {
            return dataFileReader.next()
        }
        return null
    }

    // Only used in version 1 of mhub services
    static byte[] serialize(GenericRecord genericRecord) {
        Schema schema = genericRecord.schema
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<GenericRecord>(schema)
        DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<GenericRecord>(datumWriter)
        dataFileWriter.create(schema, outputStream)
        dataFileWriter.append(genericRecord)
        dataFileWriter.close()
        return outputStream.toByteArray()
    }

    // Only used in version 1 of mhub services
    static GenericRecord deserializeNoSchema(byte[] bytes, Schema schema) {
        return new GenericDatumReader<GenericRecord>(schema: schema)
                .read(
                        null,
                        DecoderFactory.get().binaryDecoder(bytes, null)
                )
    }

    static byte[] serializeNoSchema(GenericRecord genericRecord) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null)
        new GenericDatumWriter<GenericRecord>(genericRecord.schema).write(
                genericRecord,
                encoder)
        ((Flushable) encoder).flush()
        outputStream.close()
        return outputStream.toByteArray()
    }

    private static Object toFieldValue(Object o, Schema schema) {
        switch (schema.type) {
            case Schema.Type.ARRAY:
                return (o as List).collect() { toFieldValue(it, schema.elementType) }
            case Schema.Type.ENUM:
                return new GenericData.EnumSymbol(schema, o)
            case Schema.Type.MAP:
                return (o as Map).collectEntries { k, v ->
                    [(k): toFieldValue(v, schema.valueType)]
                }
            case Schema.Type.RECORD:
                return toGenericRecord(o as Map, schema)
            case Schema.Type.UNION:
                List<Schema> types = schema.types
                Schema elementSchema = types.find() { it.type != Schema.Type.NULL }
                return toFieldValue(o, elementSchema)
        }
        return o
    }
}
