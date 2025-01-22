package com.vladsv.app.exception;

public class RequiredParamMissingException extends IllegalArgumentException {
    public RequiredParamMissingException(String s) {
        super(s);
    }
}
