package city.gotham.security.processors;

import city.gotham.security.models.LoginTopicInput;
import city.gotham.security.stores.CustomStoreWrapper;
import org.junit.*;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LoginFailsProcessorTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginFailsProcessorTest.class);

    private static final LoginTopicInput USER_30_SUCCESS = new LoginTopicInput();
    private static final LoginTopicInput USER_30_FAIL = new LoginTopicInput();
    private static final LoginTopicInput USER_75_SUCCESS = new LoginTopicInput();
    private static final LoginTopicInput USER_75_FAIL = new LoginTopicInput();

    private static LoginFailsProcessor loginFailsProcessor;
    private static CustomStoreWrapper<String, Integer> store;

    @BeforeClass
    public static void beforeAll() throws Exception {
        loginFailsProcessor = new LoginFailsProcessor();
        store = new CustomStoreWrapper<>();

        USER_30_SUCCESS.setUserId("30");
        USER_30_SUCCESS.setStatus("success");
        USER_30_SUCCESS.setIp("127.0.0.1");
        USER_30_SUCCESS.setLogTime("08/03/2018");

        USER_30_FAIL.setUserId("30");
        USER_30_FAIL.setStatus("fail");
        USER_30_FAIL.setIp("127.0.0.1");
        USER_30_FAIL.setLogTime("08/03/2018");

        USER_75_SUCCESS.setUserId("75");
        USER_75_SUCCESS.setStatus("success");
        USER_75_SUCCESS.setIp("127.0.0.1");
        USER_75_SUCCESS.setLogTime("02/03/2018");

        USER_75_FAIL.setUserId("75");
        USER_75_FAIL.setStatus("fail");
        USER_75_FAIL.setIp("127.0.0.1");
        USER_75_FAIL.setLogTime("02/03/2018");
    }

    @Before
    public void setUp() {
        store.clear();
        LOGGER.info("Store cleared");
    }

    @After
    public void tearDown() throws Exception { }

    @Test
    public void _0001_processLogin_chain_of_fail() {
        LOGGER.info("Testing 'n' consecutive failed login reaching the application limit.");
        LOGGER.info("Running LoginFailsProcessorTest._0001_processLogin_chain_of_fail() ---> Expect to be True");

        boolean mustForward = false;

        for (int i = 0; i < LoginFailsProcessor.LOGIN_FAILS_LIMIT; i++) {
            mustForward = loginFailsProcessor.processLogin(USER_30_FAIL, store); // !! user30 fails = LOGIN_FAILS_LIMIT
        }

        assertTrue(mustForward);
        LOGGER.info("Passed");
    }

    @Test
    public void _0002_processLogin_alternated_users_fail() {
        LOGGER.info("Testing 'n' consecutive failed logins alternating users. Limit is not reached.");
        LOGGER.info("Running LoginFailsProcessorTest._0002_processLogin_alternated_users_fail() ---> Expect to be always False");

        for (int i = 0; i < LoginFailsProcessor.LOGIN_FAILS_LIMIT; i++) {
            if ((i % 2) == 0) { // Alternate even / odd
                assertFalse(loginFailsProcessor.processLogin(USER_30_FAIL, store)); // user30 fails < LOGIN_FAILS_LIMIT
            } else {
                assertFalse(loginFailsProcessor.processLogin(USER_75_FAIL, store)); // user75 fails < LOGIN_FAILS_LIMIT
            }
        }

        LOGGER.info("Passed");
    }

    @Test
    public void _0003_processLogin_interrupted_chain_of_fails() {
        LOGGER.info("Testing 'n' failed logins interrupted by a success login.");
        LOGGER.info("Running LoginFailsProcessorTest._0003_processLogin_interrupted_chain_of_fails() ---> Expect to be False");

        for (int i = 1; i < LoginFailsProcessor.LOGIN_FAILS_LIMIT; i++) {
            loginFailsProcessor.processLogin(USER_30_FAIL, store); // user30 fails < LOGIN_FAILS_LIMIT
        }

        loginFailsProcessor.processLogin(USER_30_SUCCESS, store); // user30 fails = 0

        assertFalse(loginFailsProcessor.processLogin(USER_30_FAIL, store));
        LOGGER.info("Passed");
    }

    @Test
    public void _0004_processLogin_chain_of_users_and_interruptions() {
        LOGGER.info("Testing 'n' failed and success logins from different users.");
        LOGGER.info("Running LoginFailsProcessorTest._0004_processLogin_chain_of_users_and_interruptions() ---> Expect to be True if limit is reached, otherwise False");

        for (int i = 1; i < LoginFailsProcessor.LOGIN_FAILS_LIMIT; i++) {
            assertFalse(loginFailsProcessor.processLogin(USER_30_FAIL, store)); // user30 fails < LOGIN_FAILS_LIMIT
        }

        assertFalse(loginFailsProcessor.processLogin(USER_75_FAIL, store)); // user75 fails = 1
        assertFalse(loginFailsProcessor.processLogin(USER_30_SUCCESS, store)); // user30 fails = 0

        boolean mustForward = false;
        for (int i = 1; i < LoginFailsProcessor.LOGIN_FAILS_LIMIT; i++) {
            mustForward = loginFailsProcessor.processLogin(USER_75_FAIL, store); // !! user75 fails = LOGIN_FAILS_LIMIT
        }

        assertTrue(mustForward);
        LOGGER.info("Passed");
    }

}
