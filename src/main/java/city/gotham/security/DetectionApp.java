package city.gotham.security;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class DetectionApp {

    private static final Logger logger = LoggerFactory.getLogger(DetectionApp.class);

    public static void main(String[] args) {
        logger.info("Starting service...");

        final Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-application");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "DESKTOP-4GK2USK:9092");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        logger.info("Properties created...");

        KafkaStreamBuilder kafkaStreamBuilder = new KafkaStreamBuilder(config, "test");
        KafkaStreams streams = kafkaStreamBuilder.getKafkaStreams();
        streams.start();
    }

}
