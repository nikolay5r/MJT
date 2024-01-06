package bg.sofia.uni.fmi.mjt.simcity.exception;

public class InsufficientPlotAreaException extends RuntimeException {
    public InsufficientPlotAreaException() {
        super();
    }

    public InsufficientPlotAreaException(String message) {
        super(message);
    }

    public InsufficientPlotAreaException(String message, Throwable cause) {
        super(message, cause);
    }
}
