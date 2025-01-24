package org.example.exceptions;

public class LocationNotFoundException extends RuntimeException {
    public LocationNotFoundException(String message) {
        super(message);
    }
}
