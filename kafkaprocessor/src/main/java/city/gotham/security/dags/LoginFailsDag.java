package city.gotham.security.dags;

import city.gotham.security.ApplicationProperties;
import city.gotham.security.models.LoginTopicInput;
import city.gotham.security.processors.LoginFailsEmailSenderProcessor;
import city.gotham.security.processors.LoginFailsOutputProcessor;
import city.gotham.security.processors.LoginFailsProcessor;
import org.apache.kafka.common.serialization.*;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Properties;

public class LoginFailsDag {

    private static final Logger logger = LoggerFactory.getLogger(LoginFailsDag.class);

    private KafkaStreams streams;

    public LoginFailsDag(Properties config) {
        logger.info("Creating LoginFailsDag...");

        ApplicationProperties applicationProperties = ApplicationProperties.getInstance();

        Topology topology = new Topology();
        topology.addSource("SOURCE", new StringDeserializer(), new JsonDeserializer<>(LoginTopicInput.class), applicationProperties.getStreamLoginFailsSourceTopic())

                // State stores creation
                .addStateStore(Stores.keyValueStoreBuilder(
                        Stores.persistentKeyValueStore(LoginFailsProcessor.LOGIN_FAILS_STORE_NAME),
                        Serdes.String(),
                        Serdes.Integer()))

                // Processors
                // LoginFailsProcessor -> EmailSenderProcessor -> OutputProcessor
                .addProcessor(LoginFailsProcessor.PROCESSOR_NAME, LoginFailsProcessor::new, "SOURCE")
                .addProcessor(LoginFailsEmailSenderProcessor.PROCESSOR_NAME, LoginFailsEmailSenderProcessor::new, LoginFailsProcessor.PROCESSOR_NAME)
                .addProcessor(LoginFailsOutputProcessor.PROCESSOR_NAME, LoginFailsOutputProcessor::new, LoginFailsEmailSenderProcessor.PROCESSOR_NAME)

                // Sinks
                .addSink("SINK", applicationProperties.getStreamLoginFailsOutputTopic(), new StringSerializer(), new JsonSerializer(), LoginFailsOutputProcessor.PROCESSOR_NAME)

                // State stores + processor connection
                .connectProcessorAndStateStores(LoginFailsProcessor.PROCESSOR_NAME, LoginFailsProcessor.LOGIN_FAILS_STORE_NAME);

        streams = new KafkaStreams(topology, config);

        streams.setUncaughtExceptionHandler((Thread thread, Throwable throwable) -> {
            throwable.printStackTrace();
        });

        logger.info("Created LoginFailsDag...");
    }

    public KafkaStreams getStreams() {
        return streams;
    }

}
