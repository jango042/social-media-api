package com.jango.socialmediaapi.exceptions;

public class ServiceException extends Exception {
    private String message;

    public ServiceException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
