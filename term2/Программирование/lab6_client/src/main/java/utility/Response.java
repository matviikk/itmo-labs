package utility;

import java.io.Serializable;

public class Response implements Serializable {
    private String message;
    private String errorMessage;
    public Response(String message) {
        this.message = message;
    }

    public Response(String message, String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "Response{" +
                "message='" + message + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }

    public String getMessage() {
        return message;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
}
