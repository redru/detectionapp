package city.gotham.security;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Properties;

public class KafkaStreamsBuilder {

    private static final Logger logger = LoggerFactory.getLogger(KafkaStreamsBuilder.class);

    private Properties config;
    private String topic;

    private StreamsBuilder builder;

    KafkaStreamsBuilder(final Properties config, String topic) {
        this.config = config;
        this.topic = "".equals(topic) ? "test" : topic;

        builder = new StreamsBuilder();
        KStream<String, String> textLines = builder.stream(topic);
        KTable<String, Long> wordCounts = textLines
                .flatMapValues(textLine -> Arrays.asList(textLine.toLowerCase().split("\\W+")))
                .groupBy((key, word) -> word)
                .count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("counts-store"));
        wordCounts.toStream().to(topic, Produced.with(Serdes.String(), Serdes.Long()));
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
