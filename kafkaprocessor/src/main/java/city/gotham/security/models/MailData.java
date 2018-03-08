package city.gotham.security.models;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class MailData {

    private String from;
    private String to;
    private String subject;

    public MailData() {
        this("", "", "");
    }

    public MailData(String from, String to, String subject) {
        this.from = from;
        this.to = to;
        this.subject = subject;
    }

    public InternetAddress getFromInternetAddress() {
        try {
            return new InternetAddress(from);
        } catch (AddressException e) {
            e.printStackTrace();
            return null;
        }
    }

    public InternetAddress[] getRecipients() {
        try {
            return InternetAddress.parse(to);
        } catch (AddressException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

}
