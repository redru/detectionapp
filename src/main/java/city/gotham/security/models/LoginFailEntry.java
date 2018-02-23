package city.gotham.security.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginFailEntry {

    @JsonProperty("userID")
    private String userId;

    @JsonProperty("message")
    private String message;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
