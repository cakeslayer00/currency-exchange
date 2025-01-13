package com.vladsv.app.exceptions;

public class CurrencyDoesNotExistException extends Throwable {
    public CurrencyDoesNotExistException(String s) {
        super(s);
    }
}
