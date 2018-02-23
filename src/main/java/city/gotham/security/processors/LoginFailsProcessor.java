package city.gotham.security.processors;

import city.gotham.security.models.LoginTopicInput;
import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginFailsProcessor extends AbstractProcessor<String, LoginTopicInput> {

    private static final Logger logger = LoggerFactory.getLogger(LoginFailsProcessor.class);

    private static final int LOGIN_FAILS_LIMIT = 4;

    public static final String PROCESSOR_NAME = "LOGIN_FAILS_PROCESSOR";
    public static final String LOGIN_FAILS_STORE_NAME = "LoginFailsStore";

    private ProcessorContext context;
    private KeyValueStore<String, Integer> loginFailsStore;

    @Override
    @SuppressWarnings("unchecked")
    public void init(ProcessorContext context) {
        this.context = context;
        loginFailsStore = (KeyValueStore<String, Integer>) this.context.getStateStore(LoginFailsProcessor.LOGIN_FAILS_STORE_NAME);
    }

    @Override
    public void process(String key, LoginTopicInput loginTopicInput) {
        // Reset user count if loginTopicInput has succeeded
        if ("success".equals(loginTopicInput.getStatus())) {
            logger.info("User " + loginTopicInput.getUserId() + " correctly logged in at " + loginTopicInput.getLogTime() + " so counter was reset from " + getUserFailsCountFromStore(loginTopicInput));
            loginFailsStore.delete(loginTopicInput.getUserId());
        } else {
            // Get current user failures count and add 1 because we know that it
            // is handling failed loginTopicInput.
            Integer userCount = getUserFailsCountFromStore(loginTopicInput) + 1;

            // If count has exceeded limit, reset and add to the queue a loginTopicInput fail message to commit
            // to the next topic. Else increment counter.
            if (userCount >= LoginFailsProcessor.LOGIN_FAILS_LIMIT) {
                logger.info("User " + loginTopicInput.getUserId() + " failed logging in for " + userCount + " times, last time at " + loginTopicInput.getLogTime() + ". A notification will be sent");
                loginFailsStore.put(loginTopicInput.getUserId(), 0);
                context.forward(key, loginTopicInput);
            } else {
                logger.info("User " + loginTopicInput.getUserId() + " failed logging in at " + loginTopicInput.getLogTime() + ". Current count: " + userCount);
                loginFailsStore.put(loginTopicInput.getUserId(), userCount);
            }
        }

        this.context.commit();
    }

    /**
     * This function returns the total user loginTopicInput fails count from the store.
     * @param loginTopicInput
     * @return Integer
     */
    private Integer getUserFailsCountFromStore(LoginTopicInput loginTopicInput) {
        Integer userCount = loginFailsStore.get(loginTopicInput.getUserId());

        return userCount != null ? userCount : 0;
    }

}
