package city.gotham.security.processors;

import city.gotham.security.models.LoginEntry;
import city.gotham.security.models.LoginFailEntry;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LoginProcessor implements Processor<String, String> {

    private static final Logger logger = LoggerFactory.getLogger(LoginProcessor.class);

    private ProcessorContext context;
    private KeyValueStore<String, Integer> loginFailsCountStore;
    private KeyValueStore<String, String> loginFailsStore;

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    @SuppressWarnings("unchecked")
    public void init(ProcessorContext context) {
        // keep the processor context locally because we need it in punctuate() and commit()
        this.context = context;

        // retrieve the key-value store named "LoginFails"
        loginFailsCountStore = (KeyValueStore) context.getStateStore("LoginFailsCount");
        loginFailsStore = (KeyValueStore) context.getStateStore("LoginFails");

        // schedule a punctuate() method every 1000 milliseconds based on stream-time
        this.context.schedule(1000, PunctuationType.STREAM_TIME, (timestamp) -> {
            KeyValueIterator<String, String> loginFailsIterator = this.loginFailsStore.all();

            while (loginFailsIterator.hasNext()) {
                KeyValue<String, String> storeEntry = loginFailsIterator.next();

                try {
                    LoginEntry loginEntry = mapper.readValue(storeEntry.value, LoginEntry.class);

                    LoginFailEntry loginFailEntry = new LoginFailEntry();
                    loginFailEntry.setUserId(loginEntry.getUserId());
                    loginFailEntry.setMessage("User failed logging in 4 times");

                    context.forward(storeEntry.key, mapper.writeValueAsString(loginFailEntry));
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    loginFailsStore.delete(storeEntry.key);
                }
            }

            loginFailsIterator.close();

            // commit the current processing progress
            context.commit();
        });
    }

    @Override
    public void process(String key, String value) {
        try {
            LoginEntry loginEntry = mapper.readValue(value, LoginEntry.class);

            // Reset user count and return if login has succeeded
            if ("success".equals(loginEntry.getStatus())) {
                int oldCount = loginFailsCountStore.get(loginEntry.getUserId());
                loginFailsCountStore.put(loginEntry.getUserId(), 0);
                logger.info("User " + loginEntry.getUserId() + " correctly logged in at " + loginEntry.getLogTime() + " so counter was reset from " + oldCount);
                return;
            }

            // Get current user login fails count and check if is null
            // to prevent NullPointerException
            Integer userCount = loginFailsCountStore.get(loginEntry.getUserId());

            if (userCount == null) {
                userCount = 0;
            }

            if (userCount + 1 >= 4) {
                loginFailsCountStore.put(loginEntry.getUserId(), 0);
                loginFailsStore.put(loginEntry.getUserId(), value);
                logger.info("User " + loginEntry.getUserId() + " failed logging in at " + loginEntry.getLogTime() + ". A notification will be sent");
            } else {
                loginFailsCountStore.put(loginEntry.getUserId(), userCount + 1);
                logger.info("User " + loginEntry.getUserId() + " failed logging in at " + loginEntry.getLogTime() + ". Current count: " + (userCount + 1));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void punctuate(long l) {
        // DEPRECATED
    }

    @Override
    public void close() {
        // close the key-value store
        loginFailsStore.close();
        loginFailsCountStore.close();
    }

}
