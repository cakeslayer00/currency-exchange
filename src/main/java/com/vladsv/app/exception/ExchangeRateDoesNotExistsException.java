package com.vladsv.app.exception;

import java.util.NoSuchElementException;

public class ExchangeRateDoesNotExistsException extends NoSuchElementException {
    public ExchangeRateDoesNotExistsException(String s) {
        super(s);
    }
}
