package city.gotham.security.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginTopicInput {

    @JsonProperty("logTime")
    private String logTime;

    @JsonProperty("userID")
    private String userId;

    @JsonProperty("IP")
    private String ip;

    @JsonProperty("status")
    private String status;

    public String getLogTime() {
        return logTime;
    }

    public void setLogTime(String logTime) {
        this.logTime = logTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "\nUser:\nlogTime: " + logTime +
                "\nuserId: " + userId +
                "\nip: " + ip +
                "\nstatus: " + status;
    }

}
