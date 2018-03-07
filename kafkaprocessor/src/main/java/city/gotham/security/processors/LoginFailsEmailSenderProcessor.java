package city.gotham.security.processors;

import city.gotham.security.ApplicationProperties;
import city.gotham.security.models.LoginTopicInput;
import city.gotham.security.models.MailData;
import city.gotham.security.models.MailProperties;
import city.gotham.security.services.LocalMailService;
import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

public class LoginFailsEmailSenderProcessor extends AbstractProcessor<String, LoginTopicInput> {

    public static final String PROCESSOR_NAME = "LOGIN_FAILS_EMAIL_SENDER_PROCESSOR_NAME";
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginFailsEmailSenderProcessor.class);
    private static final ApplicationProperties APPLICATION_PROPERTIES = ApplicationProperties.getInstance();

    private ProcessorContext context;

    @Override
    @SuppressWarnings("unchecked")
    public void init(ProcessorContext context) {
        this.context = context;
    }

    @Override
    public void process(String key, LoginTopicInput loginTopicInput) {
        LOGGER.info("Sending email...");

        // Send mail with the notification
        LocalMailService.asyncSendMail(
                EMAIL_TEMPLATE
                        .replace("${userId}", loginTopicInput.getUserId())
                        .replace("${logTime}", loginTopicInput.getLogTime())
                        .replace("${ip}", loginTopicInput.getIp()),
                new MailProperties(
                        APPLICATION_PROPERTIES.getSmtpAuth(),
                        APPLICATION_PROPERTIES.getSmtpStartTlsEnable(),
                        APPLICATION_PROPERTIES.getSmtpHost(),
                        APPLICATION_PROPERTIES.getSmtpPort(),
                        APPLICATION_PROPERTIES.getSmtpUsername(),
                        APPLICATION_PROPERTIES.getSmtpPassword()
                ),
                new MailData(APPLICATION_PROPERTIES.getSmtpUsername(), APPLICATION_PROPERTIES.getTargetEmail())
        );

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
