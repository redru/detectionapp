package city.gotham.security.processors;

import city.gotham.security.models.Login;
import city.gotham.security.models.LoginFail;
import city.gotham.security.services.LocalMailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginFailsProcessor extends AbstractProcessor<String, Login> {

    private static final Logger logger = LoggerFactory.getLogger(LoginFailsProcessor.class);

    private static final int LOGIN_FAILS_LIMIT = 4;

    public static final String LOGIN_FAILS_PROCESSOR_NAME = "LOGIN_FAILS_PROCESSOR";
    public static final String LOGIN_FAILS_STORE_NAME = "LoginFailsStore";

    private ProcessorContext context;
    private ObjectMapper mapper;
    private KeyValueStore<String, Integer> loginFailsStore;

    @Override
    @SuppressWarnings("unchecked")
    public void init(ProcessorContext context) {
        this.context = context;
        mapper = new ObjectMapper();
        loginFailsStore = (KeyValueStore<String, Integer>) this.context.getStateStore(LoginFailsProcessor.LOGIN_FAILS_STORE_NAME);
    }

    @Override
    public void process(String key, Login login) {
        // Reset user count if login has succeeded
        if ("success".equals(login.getStatus())) {
            processSuccess(login);
        } else {
            processFailure(key, login);
        }

        this.context.commit();
    }

    /**
     * This function returns the total user login fails count from the store.
     * @param login
     * @return Integer
     */
    private Integer getUserFailsCountFromStore(Login login) {
        Integer userCount = loginFailsStore.get(login.getUserId());

        if (userCount == null) {
            userCount = 0;
        }

        return userCount;
    }

    /**
     * This function processes successful logins. It resets counter and logs to stdout the success.
     * @param login
     */
    private void processSuccess(Login login) {
        logger.info("User " + login.getUserId() + " correctly logged in at " + login.getLogTime() + " so counter was reset from " + getUserFailsCountFromStore(login));
        loginFailsStore.put(login.getUserId(), 0);
    }

    /**
     * This function processes failed logins. It increments the counter of the user and if
     * the counter is higher than limit, sends a notification and then resets the counter.
     * @param key
     * @param login
     */
    private void processFailure(String key, Login login) {
        // Get current user failures count and add 1 because we know
        // that this function is handling failed login
        Integer userCount = getUserFailsCountFromStore(login) + 1;

        // If count has exceeded limit, reset and add to the queue a login fail message to commit
        // to the next topic. Else increment counter.
        if (userCount >= LoginFailsProcessor.LOGIN_FAILS_LIMIT) {
            try {
                logger.info("User " + login.getUserId() + " failed logging in for " + userCount + " times, last time at " + login.getLogTime() + ". A notification will be sent");

                LoginFail loginFail = new LoginFail();
                loginFail.setUserId(login.getUserId());
                loginFail.setMessage("User failed logging in " + userCount + " times");

                this.context.forward(key, mapper.writeValueAsString(loginFail));

                // Send mail with the notification
                LocalMailService.getInstance().sendMail();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } finally {
                loginFailsStore.put(login.getUserId(), 0);
            }
        } else {
            loginFailsStore.put(login.getUserId(), userCount);
            logger.info("User " + login.getUserId() + " failed logging in at " + login.getLogTime() + ". Current count: " + (userCount));
        }
    }

}
