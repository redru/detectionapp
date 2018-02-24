package city.gotham.security.processors;

import city.gotham.security.models.LoginTopicInput;
import city.gotham.security.services.LocalMailService;
import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginFailsEmailSenderProcessor extends AbstractProcessor<String, LoginTopicInput> {

    private static final Logger logger = LoggerFactory.getLogger(LoginFailsEmailSenderProcessor.class);

    public static final String PROCESSOR_NAME = "LOGIN_FAILS_EMAIL_SENDER_PROCESSOR_NAME";

    private ProcessorContext context;
    private LocalMailService localMailService;

    @Override
    @SuppressWarnings("unchecked")
    public void init(ProcessorContext context) {
        this.context = context;
        localMailService = LocalMailService.getInstance();
    }

    @Override
    public void process(String key, LoginTopicInput loginTopicInput) {
        logger.info("Sending email...");

        // Send mail with the notification
        localMailService.sendMail(EMAIL_TEMPLATE
                .replace("${userId}", loginTopicInput.getUserId())
                .replace("${logTime}", loginTopicInput.getLogTime())
                .replace("${ip}", loginTopicInput.getIp()));

        this.context.forward(key, loginTopicInput);
        this.context.commit();
    }

    private static final String EMAIL_TEMPLATE = "<h3>Joker is trying to hack the Gotham Security System!</h3>" +
            "<div>" +
            "   <table border=\"1\">" +
            "       <thead>" +
            "           <tr>" +
            "               <th>User</th>" +
            "               <th>Time</th>" +
            "               <th>IP</th>" +
            "           </tr>" +
            "       </thead>" +
            "       <tbody>" +
            "           <tr>" +
            "               <td>${userId}</td>" +
            "               <td>${logTime}</td>" +
            "               <td>${ip}</td>" +
            "           </tr>" +
            "       </tbody>" +
            "   </table>" +
            "</div>";

}
