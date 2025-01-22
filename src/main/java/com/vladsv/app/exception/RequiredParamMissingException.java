package com.vladsv.app.exception;

public class RequiredParamMissingException extends Throwable {
    public RequiredParamMissingException(String s) {
        super(s);
    }
}
