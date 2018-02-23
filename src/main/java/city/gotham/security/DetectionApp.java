package city.gotham.security;

import city.gotham.security.dags.LoginFailsDag;
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

        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "detection-app");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "DESKTOP-4GK2USK:9092");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        LoginFailsDag loginFailsDag = new LoginFailsDag(config);
        KafkaStreams streams = loginFailsDag.getStreams();
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

}
