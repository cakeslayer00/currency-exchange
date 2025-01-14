package com.vladsv.app.utils;

import com.vladsv.app.exceptions.CurrencyDoesNotExistsException;
import com.vladsv.app.exceptions.RequiredFieldMissingException;
import com.vladsv.app.models.Currency;
import com.vladsv.app.repositories.impl.CurrencyRepository;

import java.sql.SQLException;

public class Validator {
    public void validateCurrencyCode(String code) {
        if (!code.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException("Invalid currency code");
        }
    }

    public String getValidParameter(String param) throws RequiredFieldMissingException {
        if (param == null || param.isBlank()) {
            throw new RequiredFieldMissingException("Required field is empty");
        }
        return param;
    }

    public Currency getValidCurrency(CurrencyRepository currencyRepository, String code) throws CurrencyDoesNotExistsException, SQLException {
        return currencyRepository.findByCode(code).orElseThrow(
                () -> new CurrencyDoesNotExistsException("Currency with this code doesn't exist")
        );
    }
}
