package city.gotham.security.models;

public class MailProperties {

    public MailProperties() {
        this("", "", "", "", "", "");
    }

    public MailProperties(
            String smtpAuth,
            String smtpStartTlsEnable,
            String smtpHost,
            String smtpPort,
            String smtpUsername,
            String smtpPassword
    ) {
        this.smtpAuth = smtpAuth;
        this.smtpStartTlsEnable = smtpStartTlsEnable;
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.smtpUsername = smtpUsername;
        this.smtpPassword = smtpPassword;
    }

    private String smtpAuth;
    private String smtpStartTlsEnable;
    private String smtpHost;
    private String smtpPort;
    private String smtpUsername;
    private String smtpPassword;

    public String getSmtpAuth() {
        return smtpAuth;
    }

    public void setSmtpAuth(String smtpAuth) {
        this.smtpAuth = smtpAuth;
    }

    public String getSmtpStartTlsEnable() {
        return smtpStartTlsEnable;
    }

    public void setSmtpStartTlsEnable(String smtpStartTlsEnable) {
        this.smtpStartTlsEnable = smtpStartTlsEnable;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    public String getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(String smtpPort) {
        this.smtpPort = smtpPort;
    }

    public String getSmtpUsername() {
        return smtpUsername;
    }

    public void setSmtpUsername(String smtpUsername) {
        this.smtpUsername = smtpUsername;
    }

    public String getSmtpPassword() {
        return smtpPassword;
    }

    public void setSmtpPassword(String smtpPassword) {
        this.smtpPassword = smtpPassword;
    }

}
