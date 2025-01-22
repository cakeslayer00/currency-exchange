package com.vladsv.app.exception;

import java.util.NoSuchElementException;

public class CurrencyDoesNotExistsException extends NoSuchElementException {
    public CurrencyDoesNotExistsException(String s) {
        super(s);
    }
}
