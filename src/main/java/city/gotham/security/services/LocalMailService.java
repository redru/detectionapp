package city.gotham.security.services;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class LocalMailService {

    private static LocalMailService instance;

    private Session session;

    private LocalMailService() {
        final String username = "<EMAIL_ADDRESS>";
        final String password = "<PASSWORD>";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "465");

        session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

    }

    public void sendMail() {
        new Thread(() -> {
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("<EMAIL_ADDRESS>"));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse("<EMAIL_ADDRESS>"));
                message.setSubject("Testing Subject");
                message.setText("Dear Mail Crawler,"
                        + "\n\n No spam to my email, please!");

                Transport.send(message);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public static LocalMailService getInstance() {
        if (instance == null)
            instance = new LocalMailService();

        return instance;
    }

}
