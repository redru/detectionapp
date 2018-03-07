package city.gotham.security.services;

import city.gotham.security.models.MailData;
import city.gotham.security.models.MailProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LocalMailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalMailService.class);
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(2);

    /**
     * This function send an asynchronous mail and returns a Future that can be consumed by the caller.
     * @param template
     * @param mailProperties
     * @param mailData
     * @return Future<Boolean>
     */
    public static Future<Boolean> asyncSendMail(String template, MailProperties mailProperties, MailData mailData) {
        return EXECUTOR_SERVICE.submit(() -> {
            try {
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", mailProperties.getSmtpStartTlsEnable());
                props.put("mail.smtp.host", mailProperties.getSmtpHost());
                props.put("mail.smtp.port", mailProperties.getSmtpPort());

                Session session = Session.getInstance(props, new javax.mail.Authenticator() {

                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(mailProperties.getSmtpUsername(), mailProperties.getSmtpPassword());
                    }

                });

                Message message = new MimeMessage(session);
                message.setFrom(mailData.getFromInternetAddress());
                message.setRecipients(Message.RecipientType.TO, mailData.getRecipients());
                message.setSubject("[Gotham Security] Alert Service");
                message.setContent(template, "text/html; charset=utf-8");

                Transport.send(message);
                LOGGER.info("An email has been correctly sent to " + String.join(" ", mailData.getTo()));
                return true;
            } catch (MessagingException e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    /**
     * This function returns synchronously after sending an email.
     * @param template
     * @param mailProperties
     * @param mailData
     * @return boolean
     */
    public static boolean sendMail(String template, MailProperties mailProperties, MailData mailData) {
        try {
            return asyncSendMail(template, mailProperties, mailData).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return false;
        }
    }

}
