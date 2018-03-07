package city.gotham.security.dags;

import city.gotham.security.ApplicationProperties;
import city.gotham.security.models.LoginFailureTopicOutput;
import city.gotham.security.models.LoginTopicInput;
import com.github.charithe.kafka.*;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ExtendWith(KafkaJunitExtension.class)
@KafkaJunitExtensionConfig(startupMode = StartupMode.WAIT_FOR_STARTUP)
public class LoginFailsDagTest {

    @Rule
    public KafkaJunitRule kafkaRule = new KafkaJunitRule(EphemeralKafkaBroker.create());

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginFailsDagTest.class);
    private static final ApplicationProperties APPLICATION_PROPERTIES = ApplicationProperties.getInstance();

    private Properties producerProperties = new Properties();
    private Properties consumerProperties = new Properties();

    @Before
    public void setUp() throws Exception {
        // Producer Properties
        producerProperties.put("bootstrap.servers", APPLICATION_PROPERTIES.getBootstrapServers());

        // Consumer Properties
        consumerProperties.put("bootstrap.servers", APPLICATION_PROPERTIES.getBootstrapServers());
    }

    @After
    public void tearDown() throws Exception { }

    /*@Test
    public void _0001_consecutiveLoginFails() {
        LOGGER.info("Running LocalMailServiceTest._0001_consecutiveLoginFails() ---> ");
        ApplicationProperties applicationProperties = ApplicationProperties.getInstance();

        Properties props = new Properties();
        props.put("bootstrap.servers", applicationProperties.getBootstrapServers());
        props.put("enable.auto.commit", "true");
        KafkaConsumer<String, LoginTopicInput> consumer = new KafkaConsumer<>(props, new StringDeserializer(), new JsonDeserializer<>(LoginTopicInput.class));
        consumer.subscribe(Arrays.asList(applicationProperties.getStreamLoginFailsOutputTopic()));

        while (true) {
            ConsumerRecords<String, LoginTopicInput> records = consumer.poll(100);
            for (ConsumerRecord<String, LoginTopicInput> record : records)
                System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
        }

        LOGGER.info("Passed");
    }*/
    @Test
    public void _0001_consecutiveLoginFails() throws ExecutionException, InterruptedException {
        LOGGER.info("Running LocalMailServiceTest._0001_consecutiveLoginFails() ---> ");
        Producer<String, LoginTopicInput> producer = kafkaRule.helper().createProducer(new StringSerializer(), new JsonSerializer<>(), producerProperties);

        LoginTopicInput loginTopicInput = new LoginTopicInput();
        loginTopicInput.setIp("aaa");
        loginTopicInput.setLogTime(GregorianCalendar.getInstance().toString());
        loginTopicInput.setStatus("fail");
        loginTopicInput.setUserId("30");

        for (int i = 0; i < 4; i++) {
            producer.send(new ProducerRecord<>(APPLICATION_PROPERTIES.getStreamLoginFailsSourceTopic(), loginTopicInput));
        }

        KafkaConsumer<String, LoginFailureTopicOutput> consumer = kafkaRule.helper().createConsumer(new StringDeserializer(), new JsonDeserializer<>(), consumerProperties);
        List<ConsumerRecord<String, LoginFailureTopicOutput>> result = kafkaRule.helper().consume(APPLICATION_PROPERTIES.getStreamLoginFailsOutputTopic(), consumer, 1).get();

        assertEquals(1, result.size());
        LOGGER.info("Passed");
    }

    @Test
    public void _0002_nonConsecutiveLoginFails() {
        LOGGER.info("Running LocalMailServiceTest._0002_nonConsecutiveLoginFails() ---> ");

        LOGGER.info("Passed");
    }

    @Test
    public void _0003_interruptedConsecutiveLoginFails() {
        LOGGER.info("Running LocalMailServiceTest._0003_interruptedConsecutiveLoginFails() ---> ");

        LOGGER.info("Passed");
    }

    @Test
    public void _0004_alternatedUsersConsecutiveLoginFails() {
        LOGGER.info("Running LocalMailServiceTest._0004_alternatedUsersConsecutiveLoginFails() ---> ");

        LOGGER.info("Passed");
    }

}
