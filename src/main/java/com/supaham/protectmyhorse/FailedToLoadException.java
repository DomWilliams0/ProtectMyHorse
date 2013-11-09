package com.supaham.protectmyhorse;

public class FailedToLoadException extends Exception {

    private static final long serialVersionUID = 1L;

    public FailedToLoadException() {

    }

    public FailedToLoadException(String message) {

        super(message);
    }

    public FailedToLoadException(String message, Throwable throwable) {

        super(message, throwable);

    }

    public FailedToLoadException(Throwable throwable) {

        super(throwable);
    }

}
