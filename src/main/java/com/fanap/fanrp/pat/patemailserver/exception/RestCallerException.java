package com.fanap.fanrp.pat.patemailserver.exception;

/**
 * @author Mehrdad Dehnamaki
 */
public class RestCallerException extends RuntimeException {

    public RestCallerException() {
    }

    public RestCallerException(String message) {
        super(message);
    }

    public RestCallerException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestCallerException(Throwable cause) {
        super(cause);
    }

    public RestCallerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
