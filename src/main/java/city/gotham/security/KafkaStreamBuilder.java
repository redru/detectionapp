package city.gotham.security;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.Consumed;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

public class KafkaStreamBuilder {

    private static final Logger logger = LoggerFactory.getLogger(KafkaStreamBuilder.class);

    private Properties config;
    private String inputTopic;
    private String outputTopic;

    private StreamsBuilder builder;

    KafkaStreamBuilder(final Properties config, final String inputTopic, final String outputTopic) throws NullPointerException {
        this.config = config;
        this.inputTopic = "".equals(inputTopic) ? "test" : inputTopic;
        this.outputTopic = "".equals(outputTopic) ? "test-output" : outputTopic;

        if (this.config == null) {
            throw new NullPointerException("config object cannot be null");
        }

        builder = new StreamsBuilder();

        KStream<String, String> source = builder.stream(this.inputTopic);
        source.flatMapValues(new ValueMapper<String, Iterable<String>>() {

            @Override
            public Iterable<String> apply(String value) {
                List<String> result = new ArrayList<>();
                Iterable<String> words = Arrays.asList(value.split("\\W+"));

                words.forEach(new Consumer<String>() {

                    @Override
                    public void accept(String o) {
                        if ("abc".equals(o)) {
                            result.add(o);
                        }
                    }

                });

                return result;
            }

        }).to(this.outputTopic);
    }

    public KafkaStreams getKafkaStreams() {
        Topology topology = builder.build();
        logger.info(topology.describe().toString());

        return new KafkaStreams(topology, config);
    }

    public Properties getConfig() {
        return config;
    }

    public String getInputTopic() {
        return inputTopic;
    }

    public String getOutputTopic() {
        return outputTopic;
    }

}
