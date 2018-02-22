package city.gotham.security;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.Consumed;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Properties;

public class KafkaStreamBuilder {

    private static final Logger logger = LoggerFactory.getLogger(KafkaStreamBuilder.class);

    private Properties config;
    private String topic;

    private StreamsBuilder builder;

    KafkaStreamBuilder(final Properties config, String topic) {
        this.config = config;
        this.topic = "".equals(topic) ? "test" : topic;

        /*builder = new StreamsBuilder();
        KStream<String, String> textLines = builder.stream(topic);
        KTable<String, Long> wordCounts = textLines
                .flatMapValues(textLine -> Arrays.asList(textLine.toLowerCase().split("\\W+")))
                .groupBy((key, word) -> word)
                .count(*//*Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("counts-store")*//*);
        wordCounts.toStream().to(topic, Produced.with(Serdes.String(), Serdes.Long()));*/

        // Serializers/deserializers (serde) for String and Long types
        final Serde<String> stringSerde = Serdes.String();
        final Serde<Long> longSerde = Serdes.Long();

        // Construct a `KStream` from the input topic "streams-plaintext-input", where message values
        // represent lines of text (for the sake of this example, we ignore whatever may be stored
        // in the message keys).
        builder = new StreamsBuilder();
        KStream<String, String> textLines = builder.stream(topic,
                Consumed.with(stringSerde, stringSerde));

        KTable<String, Long> wordCounts = textLines
                // Split each text line, by whitespace, into words.
                .flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))

                // Group the text words as message keys
                .groupBy((key, value) -> value)

                // Count the occurrences of each word (message key).
                .count();

        // Store the running counts as a changelog stream to the output topic.
        wordCounts.toStream().to(topic, Produced.with(stringSerde, longSerde));
    }

    public KafkaStreams getKafkaStreams() {
        return new KafkaStreams(builder.build(), config);
    }

    public void setConfig(final Properties config) {
        this.config = config;
    }

    public Properties getConfig() {
        return config;
    }

}
