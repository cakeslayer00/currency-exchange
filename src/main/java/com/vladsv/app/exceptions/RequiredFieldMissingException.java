package com.vladsv.app.exceptions;

public class RequiredFieldMissingException extends Throwable {
    public RequiredFieldMissingException(String s) {
        super(s);
    }
}
