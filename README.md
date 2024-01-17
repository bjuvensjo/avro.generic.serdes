## Usage

Place

* target/serdes-1.0.0-SNAPSHOT.jar
* target/dependencies/*jar

in classpath.

Configure Kafka to use appropriate serializer and deserializer.


## Add support for new Avro schema 

Put the new Avro schema in 

    ./src/main/resources

### Avro avdl to avsc

This component does not support Avro schemas in avdl format, but in avsc format.
To generate avsc from avdl use avro-tools.

Example:

    java -jar ./lib/avro-tools-1.11.3.jar idl2schemata ./src/main/resources/SigningNotification.avdl ./src/main/resources

After generating, you can optionally delete some generated files, in this example:

* Body.avsc
* Header.avsc
* Status.avsc

### Serializer and Deserializer

Copy 

* [SigningNotificationDeserializer.groovy](src%2Fmain%2Fjava%2Favro%2Fgeneric%2Fserdes%2FSigningNotificationDeserializer.groovy)
* [SigningNotificationSerializer.groovy](src%2Fmain%2Fjava%2Favro%2Fgeneric%2Fserdes%2FSigningNotificationSerializer.groovy)

and update names to fit the name of your schema and also update them to reference your schema.