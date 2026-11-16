package Services;

public final class ServiceException extends RuntimeException {
    public ServiceException(String userMessage) {
        super(userMessage);
    }

    public ServiceException(String userMessage, Throwable cause) {
        super(userMessage, cause);
    }

    public static ServiceException wrap(String userMessage, RuntimeException cause) {
        return cause instanceof ServiceException serviceException
                ? serviceException
                : new ServiceException(userMessage, cause);
    }
}
