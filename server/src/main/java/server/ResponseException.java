package server;
public class ResponseException extends Exception {
    public ResponseException(String message) {
        super(message);
    }
    public ResponseException(Throwable cause, String message) {
        super(message, cause);
    }
}

