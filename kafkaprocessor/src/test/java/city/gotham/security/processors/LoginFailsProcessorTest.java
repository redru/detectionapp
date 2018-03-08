package city.gotham.security.processors;

import city.gotham.security.models.LoginTopicInput;
import city.gotham.security.stores.CustomStoreWrapper;
import org.apache.kafka.streams.state.KeyValueStore;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LoginFailsProcessorTest {

    private LoginFailsProcessor loginFailsProcessor;
    private KeyValueStore<String, Integer> store;

    @Before
    public void setUp() throws Exception {
        loginFailsProcessor = new LoginFailsProcessor();
        store = new CustomStoreWrapper<>();
    }

    @After
    public void tearDown() throws Exception { }

    @Test
    public void _0001_processLogin() {
        LoginTopicInput loginTopicInput = new LoginTopicInput();
        loginTopicInput.setUserId("30");
        loginTopicInput.setStatus("fail");
        loginTopicInput.setIp("127.0.0.1");
        loginTopicInput.setLogTime("08/03/2018");

        loginFailsProcessor.processLogin(loginTopicInput, store);
        loginFailsProcessor.processLogin(loginTopicInput, store);
        loginFailsProcessor.processLogin(loginTopicInput, store);
        boolean mustForward = loginFailsProcessor.processLogin(loginTopicInput, store);

        assertEquals(true, mustForward);
    }

}
