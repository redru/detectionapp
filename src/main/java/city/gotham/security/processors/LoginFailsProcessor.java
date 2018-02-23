package city.gotham.security.processors;

import city.gotham.security.models.Login;
import city.gotham.security.models.LoginFail;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginFailsProcessor extends AbstractProcessor<String, Login> {

    private static final Logger logger = LoggerFactory.getLogger(LoginFailsProcessor.class);

    private ProcessorContext context;
    private ObjectMapper mapper;
    private KeyValueStore<String, Integer> loginFailsStore;

    @Override
    @SuppressWarnings("unchecked")
    public void init(ProcessorContext context) {
        this.context = context;
        mapper = new ObjectMapper();
        loginFailsStore = (KeyValueStore<String, Integer>) this.context.getStateStore("LoginFailsStore");
    }

    @Override
    public void process(String key, Login login) {
        // Get current user login fails count
        Integer userCount = getUserFailsCount(login);

        // Reset user count and return if login has succeeded
        if ("success".equals(login.getStatus())) {
            loginFailsStore.put(login.getUserId(), 0);
            logger.info("User " + login.getUserId() + " correctly logged in at " + login.getLogTime() + " so counter was reset from " + userCount);
            return;
        }

        if (userCount + 1 >= 4) {
            try {
                logger.info("User " + login.getUserId() + " failed logging in at " + login.getLogTime() + ". A notification will be sent");

                LoginFail loginFail = new LoginFail();
                loginFail.setUserId(login.getUserId());
                loginFail.setMessage("User failed logging in 4 times");

                this.context.forward(key, mapper.writeValueAsString(loginFail));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } finally {
                loginFailsStore.put(login.getUserId(), 0);
            }
        } else {
            loginFailsStore.put(login.getUserId(), userCount + 1);
            logger.info("User " + login.getUserId() + " failed logging in at " + login.getLogTime() + ". Current count: " + (userCount + 1));
        }

        this.context.commit();
    }

    /**
     * This function returns the total user login fails count from the store.
     * @param login
     * @return Integer
     */
    private Integer getUserFailsCount(Login login) {
        Integer userCount = loginFailsStore.get(login.getUserId());

        if (userCount == null) {
            userCount = 0;
        }

        return userCount;
    }

}
