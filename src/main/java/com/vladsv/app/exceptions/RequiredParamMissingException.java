package com.vladsv.app.exceptions;

public class RequiredParamMissingException extends Throwable {
    public RequiredParamMissingException(String s) {
        super(s);
    }
}
