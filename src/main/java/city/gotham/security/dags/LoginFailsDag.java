package city.gotham.security.dags;

import city.gotham.security.models.Login;
import city.gotham.security.processors.LoginFailsProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.daniel.shuy.kafka.jackson.serializer.KafkaJacksonDeserializer;
import com.github.daniel.shuy.kafka.jackson.serializer.KafkaJacksonSerializer;
import org.apache.kafka.common.serialization.*;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class LoginFailsDag {

    private static final Logger logger = LoggerFactory.getLogger(LoginFailsDag.class);

    private static final String SOURCE_TOPIC = "login-topic";
    private static final String OUTPUT_TOPIC = "login-failure-topic";

    private Properties config;
    private KafkaStreams streams;

    public LoginFailsDag(Properties config) {
        logger.info("Creating LoginFailsDag...");
        this.config = config;

        ObjectMapper mapper = new ObjectMapper();

        Topology topology = new Topology();
        topology.addSource("SOURCE", new StringDeserializer(), new KafkaJacksonDeserializer<>(mapper, Login.class), LoginFailsDag.SOURCE_TOPIC)

                // State stores creation
                .addStateStore(Stores.keyValueStoreBuilder(
                        Stores.persistentKeyValueStore(LoginFailsProcessor.LOGIN_FAILS_STORE_NAME),
                        Serdes.String(),
                        Serdes.Integer()))

                // Processors
                .addProcessor(LoginFailsProcessor.LOGIN_FAILS_PROCESSOR_NAME, LoginFailsProcessor::new, "SOURCE")

                // Sinks
                .addSink("SINK", LoginFailsDag.OUTPUT_TOPIC, new StringSerializer(), new KafkaJacksonSerializer(mapper), LoginFailsProcessor.LOGIN_FAILS_PROCESSOR_NAME)

                // State stores + processor connection
                .connectProcessorAndStateStores(LoginFailsProcessor.LOGIN_FAILS_PROCESSOR_NAME, LoginFailsProcessor.LOGIN_FAILS_STORE_NAME);

        streams = new KafkaStreams(topology, config);

        streams.setUncaughtExceptionHandler((Thread thread, Throwable throwable) -> {
            throwable.printStackTrace();
        });

        logger.info("Created LoginFailsDag...");
    }

    public KafkaStreams getStreams() {
        return streams;
    }

    public Properties getConfig() {
        return config;
    }

}
