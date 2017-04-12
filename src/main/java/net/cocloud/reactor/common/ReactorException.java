package net.cocloud.reactor.common;

public class ReactorException extends RuntimeException {

    public ReactorException() {
        super();
    }

    public ReactorException(String s) {
        super(s);
    }

    public ReactorException(String message, Throwable cause) {
        super(message, cause);
    }


    public ReactorException(Throwable cause) {
        super(cause);
    }
}
