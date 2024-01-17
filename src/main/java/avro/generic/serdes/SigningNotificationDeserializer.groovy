package avro.generic.serdes

class SigningNotificationDeserializer extends GenericJsonStringDeserializer {
    SigningNotificationDeserializer() {
        this.schema = this.class.getResource('/SigningNotification.avsc').text
    }
}
