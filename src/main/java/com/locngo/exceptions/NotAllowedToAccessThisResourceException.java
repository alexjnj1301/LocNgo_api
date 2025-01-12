package com.locngo.exceptions;

public class NotAllowedToAccessThisResourceException extends RuntimeException {
    public NotAllowedToAccessThisResourceException(String message) {
        super(message);
    }
}
