package com.vladsv.app.util;

import com.vladsv.app.exception.CurrencyDoesNotExistsException;
import com.vladsv.app.exception.RequiredParamMissingException;
import com.vladsv.app.model.Currency;
import com.vladsv.app.repository.impl.CurrencyRepository;

import java.sql.SQLException;

public class Validator {
    public String getRequiredParameter(String param) throws RequiredParamMissingException {
        if (param == null || param.isBlank()) {
            throw new RequiredParamMissingException("Required field is empty");
        }
        return param;
    }

    public Currency getExistingCurrency(CurrencyRepository currencyRepository, String code) throws CurrencyDoesNotExistsException, SQLException {
        return currencyRepository.findByCode(code).orElseThrow(() -> new CurrencyDoesNotExistsException("Currency with this code doesn't exist"));
    }

    public void checkCurrencyCode(String code) {
        if (!code.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException(String.format("Currency code %s is invalid", code));
        }
    }

    public void validateCurrencyPair(String baseCurrencyCode, String targetCurrencyCode) {
        if (baseCurrencyCode.equals(targetCurrencyCode)) {
            throw new IllegalArgumentException("The base currency code must not be the same as the target currency code");
        }
    }

    public void checkNameParameterLength(String name) {
        checkForValidLength(20, name, String.format("The name %s is too long", name));
    }

    public void checkSignParameterLength(String sign) {
        checkForValidLength(3, sign, String.format("The sign %s is too long", sign));
    }

    private void checkForValidLength(int length, String param, String exceptionMessage) {
        if (param.length() > length) {
            throw new IllegalArgumentException(exceptionMessage);
        }
    }
}
