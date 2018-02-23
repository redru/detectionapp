package city.gotham.security;

import city.gotham.security.processors.LoginProcessor;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class KafkaStreamBuilder {

    private static final Logger logger = LoggerFactory.getLogger(KafkaStreamBuilder.class);

    private Properties config;
    private String inputTopic;
    private String outputTopic;

    private Topology topology;

    KafkaStreamBuilder(Properties config, String inputTopic, String outputTopic) throws NullPointerException {
        this.config = config;
        this.inputTopic = "".equals(inputTopic) ? "test" : inputTopic;
        this.outputTopic = "".equals(outputTopic) ? "test-output" : outputTopic;

        if (this.config == null) {
            throw new NullPointerException("config object cannot be null");
        }

        topology = new Topology();

        // add the source processor node that takes Kafka topic "source-topic" as input
        topology.addSource("Source", inputTopic)

                // add the WordCountProcessor node which takes the source processor as its upstream processor
                .addProcessor("LoginProcess", LoginProcessor::new, "Source")

                // add the sink processor node that takes Kafka topic "sink-topic" as output
                // and the WordCountProcessor node as its upstream processor
                .addSink("Sink", outputTopic, "LoginProcess");

        topology.addStateStore(Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore("LoginFails"),
                Serdes.String(),
                Serdes.String()));

        topology.addStateStore(Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore("LoginFailsCount"),
                Serdes.String(),
                Serdes.Integer()));

        topology.connectProcessorAndStateStores("LoginProcess", "LoginFails");
        topology.connectProcessorAndStateStores("LoginProcess", "LoginFailsCount");
    }

    public KafkaStreams getKafkaStreams() {
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
