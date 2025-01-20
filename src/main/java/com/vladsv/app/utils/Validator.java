package com.vladsv.app.utils;

import com.vladsv.app.exceptions.CurrencyDoesNotExistsException;
import com.vladsv.app.exceptions.RequiredParamMissingException;
import com.vladsv.app.models.Currency;
import com.vladsv.app.repositories.impl.CurrencyRepository;

import java.sql.SQLException;

public class Validator {
    public String getRequiredParameter(String param) throws RequiredParamMissingException {
        if (param == null || param.isBlank()) {
            throw new RequiredParamMissingException("Required field is empty");
        }
        return param;
    }

    public Currency getExistingCurrency(CurrencyRepository currencyRepository, String code) throws CurrencyDoesNotExistsException, SQLException {
        return currencyRepository.findByCode(code).orElseThrow(
                () -> new CurrencyDoesNotExistsException("Currency with this code doesn't exist")
        );
    }
}
