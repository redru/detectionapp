package city.gotham.security.processors;

import city.gotham.security.models.LoginTopicInput;
import city.gotham.security.stores.CustomStoreWrapper;
import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginFailsProcessor extends AbstractProcessor<String, LoginTopicInput> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginFailsProcessor.class);

    public static final int LOGIN_FAILS_LIMIT = 400;
    public static final String PROCESSOR_NAME = "LOGIN_FAILS_PROCESSOR";
    public static final String LOGIN_FAILS_STORE_NAME = "LoginFailsStore";

    private ProcessorContext context;
    private CustomStoreWrapper<String, Integer> loginFailsStore;

    @Override
    @SuppressWarnings("unchecked")
    public void init(ProcessorContext context) {
        this.context = context;
        this.loginFailsStore = new CustomStoreWrapper<>(
                (KeyValueStore<String, Integer>) this.context.getStateStore(LoginFailsProcessor.LOGIN_FAILS_STORE_NAME)
        );
    }

    @Override
    public void process(String key, LoginTopicInput loginTopicInput) {
        boolean mustForward = this.processLogin(loginTopicInput, this.loginFailsStore);

        if (mustForward) {
            context.forward(key, loginTopicInput);
        }

        this.context.commit();
    }

    /**
     *
     * @param loginTopicInput
     * @param store
     */
    public boolean processLogin(LoginTopicInput loginTopicInput, KeyValueStore<String, Integer> store) {
        boolean mustForward = false;

        // Reset user count if loginTopicInput has succeeded
        if ("success".equals(loginTopicInput.getStatus())) {
            LOGGER.info("User " + loginTopicInput.getUserId() + " correctly logged in at " + loginTopicInput.getLogTime() + " so counter was reset from " + getUserFailsCountFromStore(loginTopicInput, store));
            store.delete(loginTopicInput.getUserId());
        } else {
            // Get current user failures count and add 1 because we know that it
            // is handling failed loginTopicInput.
            Integer userCount = getUserFailsCountFromStore(loginTopicInput, store) + 1;

            // If count has exceeded limit, reset and add to the queue a loginTopicInput fail message to commit
            // to the next topic. Else increment counter.
            if (userCount >= LoginFailsProcessor.LOGIN_FAILS_LIMIT) {
                LOGGER.info("User " + loginTopicInput.getUserId() + " failed logging in for " + userCount + " times, last time at " + loginTopicInput.getLogTime() + ". A notification will be sent");
                store.put(loginTopicInput.getUserId(), 0);
                mustForward = true;
            } else {
                LOGGER.info("User " + loginTopicInput.getUserId() + " failed logging in at " + loginTopicInput.getLogTime() + ". Current count: " + userCount);
                store.put(loginTopicInput.getUserId(), userCount);
            }
        }

        return mustForward;
    }

    /**
     * This function returns the total user loginTopicInput fails count from the store.
     * @param loginTopicInput
     * @param store
     * @return Integer
     */
    private Integer getUserFailsCountFromStore(LoginTopicInput loginTopicInput, KeyValueStore<String, Integer> store) {
        Integer userCount = store.get(loginTopicInput.getUserId());

        return userCount != null ? userCount : 0;
    }

}
