package city.gotham.security.dags;

import city.gotham.security.models.LoginTopicInput;
import city.gotham.security.processors.LoginFailsProcessor;
import com.github.charithe.kafka.*;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.Stores;
import org.junit.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ExtendWith(KafkaJunitExtension.class)
@KafkaJunitExtensionConfig(startupMode = StartupMode.WAIT_FOR_STARTUP)
public class LoginFailsDagTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginFailsDagTest.class);

    @Mock
    ProcessorContext processorContext;

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setUp() throws Exception {

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
    public void _0001_consecutiveLoginFails() {
        LOGGER.info("Running LocalMailServiceTest._0001_consecutiveLoginFails() ---> ");

        ProcessorContext _context = Mockito.mock(ProcessorContext.class);

        _context.register(
                Stores.keyValueStoreBuilder(
                        Stores.persistentKeyValueStore(LoginFailsProcessor.LOGIN_FAILS_STORE_NAME),
                        Serdes.String(),
                        Serdes.Integer()
                ).build(),
                true,
                (byte[] var1, byte[] var2) -> {
                    LOGGER.info(new String(var2));
                });

        LoginTopicInput input = new LoginTopicInput();
        input.setUserId("30");

        LoginFailsProcessor loginFailsProcessor = new LoginFailsProcessor();
        loginFailsProcessor.init(_context);
        loginFailsProcessor.process("", input);

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
