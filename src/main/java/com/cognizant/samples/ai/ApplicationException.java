package com.cognizant.samples.ai;

public abstract class ApplicationException extends Exception {

    public ApplicationException() {
        super();
    }

    public ApplicationException(String s) {
        super(s);
    }

    public ApplicationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ApplicationException(Throwable throwable) {
        super(throwable);
    }

    protected ApplicationException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }


    public abstract String code();

    public String description() {
        return getMessage();
    }
}
