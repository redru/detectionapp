package city.gotham.security.services;

import city.gotham.security.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class LocalMailService {

    private static final Logger logger = LoggerFactory.getLogger(LocalMailService.class);

    // Singleton instance
    private static LocalMailService instance;

    private ApplicationProperties applicationProperties;
    private Session session;

    private LocalMailService() {
        applicationProperties = ApplicationProperties.getInstance();

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", applicationProperties.getSmtpStartTlsEnable());
        props.put("mail.smtp.host", applicationProperties.getSmtpHost());
        props.put("mail.smtp.port", applicationProperties.getSmtpPort());

        session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(applicationProperties.getSmtpUsername(), applicationProperties.getSmtpPassword());
                    }
                });

    }

    public void sendMail(String template) {
        new Thread(() -> {
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(applicationProperties.getSmtpUsername()));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(applicationProperties.getTargetEmail()));
                message.setSubject("[Gotham Security] Alert Service");
                message.setContent(template, "text/html; charset=utf-8");

                Transport.send(message);
                logger.info("An email has been correctly sent to " + applicationProperties.getTargetEmail());
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public static LocalMailService getInstance() {
        if (instance == null) {
            instance = new LocalMailService();
        }

        return instance;
    }

}
