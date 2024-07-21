package com.fanap.fanrp.pat.patemailserver.exception;

public class InvalidConfigurationException extends Exception {
    private static final long serialVersionUID = 2457726114506620639L;

    public InvalidConfigurationException(String message) {
        super(message);
    }

    public InvalidConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
