package city.gotham.security.processors;

import city.gotham.security.models.LoginTopicInput;
import city.gotham.security.models.LoginFailureTopicOutput;
import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginFailsOutputProcessor extends AbstractProcessor<String, LoginTopicInput> {

    private static final Logger logger = LoggerFactory.getLogger(LoginFailsOutputProcessor.class);

    public static final String PROCESSOR_NAME = "LOGIN_FAILS_OUTPUT_PROCESSOR_NAME";

    private ProcessorContext context;

    @Override
    @SuppressWarnings("unchecked")
    public void init(ProcessorContext context) {
        this.context = context;
    }

    @Override
    public void process(String key, LoginTopicInput loginTopicInput) {
        logger.info("Writing output...");

        LoginFailureTopicOutput loginFailureTopicOutput = new LoginFailureTopicOutput();
        loginFailureTopicOutput.setUserId(loginTopicInput.getUserId());
        loginFailureTopicOutput.setMessage("User failed logging in too many times");

        this.context.forward(key, loginFailureTopicOutput);
        this.context.commit();
    }

}
