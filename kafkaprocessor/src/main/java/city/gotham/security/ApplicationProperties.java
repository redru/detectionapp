package city.gotham.security;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApplicationProperties {

    private static ApplicationProperties instance;

    // Kafka configuration
    private String applicationId;
    private String bootstrapServers;

    // SMTP configuration
    private String smtpAuth;
    private String smtpStartTlsEnable;
    private String smtpHost;
    private String smtpPort;
    private String smtpUsername;
    private String smtpPassword;
    private String targetEmail;

    // Streams
    private String streamLoginFailsSourceTopic;
    private String streamLoginFailsOutputTopic;

    private ApplicationProperties() {
        try (InputStream is = ApplicationProperties.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            prop.load(is);

            applicationId = prop.getProperty("APPLICATION_ID");
            bootstrapServers = prop.getProperty("BOOTSTRAP_SERVERS");
            smtpAuth = prop.getProperty("SMTP_AUTH");
            smtpStartTlsEnable = prop.getProperty("SMTP_START_TLS_ENABLE");
            smtpHost = prop.getProperty("SMTP_HOST");
            smtpPort = prop.getProperty("SMTP_PORT");
            smtpUsername = prop.getProperty("SMTP_USERNAME");
            smtpPassword = prop.getProperty("SMTP_PASSWORD");
            targetEmail = prop.getProperty("TARGET_EMAIL");
            streamLoginFailsSourceTopic = prop.getProperty("STREAM_LOGIN_FAILS_SOURCE_TOPIC");
            streamLoginFailsOutputTopic = prop.getProperty("STREAM_LOGIN_FAILS_OUTPUT_TOPIC");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getSmtpAuth() {
        return smtpAuth;
    }

    public void setSmtpAuth(String smtpAuth) {
        this.smtpAuth = smtpAuth;
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

    public String getSmtpStartTlsEnable() {
        return smtpStartTlsEnable;
    }

    public void setSmtpStartTlsEnable(String smtpStartTlsEnable) {
        this.smtpStartTlsEnable = smtpStartTlsEnable;
    }

    public String getTargetEmail() {
        return targetEmail;
    }

    public void setTargetEmail(String targetEmail) {
        this.targetEmail = targetEmail;
    }

    public String getStreamLoginFailsSourceTopic() {
        return streamLoginFailsSourceTopic;
    }

    public void setStreamLoginFailsSourceTopic(String streamLoginFailsSourceTopic) {
        this.streamLoginFailsSourceTopic = streamLoginFailsSourceTopic;
    }

    public String getStreamLoginFailsOutputTopic() {
        return streamLoginFailsOutputTopic;
    }

    public void setStreamLoginFailsOutputTopic(String streamLoginFailsOutputTopic) {
        this.streamLoginFailsOutputTopic = streamLoginFailsOutputTopic;
    }

    public static ApplicationProperties getInstance() {
        if (instance == null) {
            instance = new ApplicationProperties();
        }

        return instance;
    }

}
