package avro.generic.serdes

class SigningNotificationSerializer extends GenericJsonStringSerializer {
    SigningNotificationSerializer() {
        this.schema = this.class.getResource('/SigningNotification.avsc').text
    }
}
