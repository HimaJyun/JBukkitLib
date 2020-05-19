package jp.jyn.jbukkitlib.util.updater;

public class HttpNotOKException extends RuntimeException {
    private final int responseCode;
    private final String responseMessage;

    public HttpNotOKException(int responseCode, String responseMessage) {
        super(String.format("HTTP %d %s", responseCode, responseMessage));
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }
}
