package com.vladsv.app.utils;

import com.vladsv.app.models.Currency;
import com.vladsv.app.repositories.impl.CurrencyRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Optional;

public class ValidationHelper {
    private final CurrencyRepository currencyRepository;

    public ValidationHelper(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    public void verifyCurrencyCode(HttpServletResponse resp, String code) throws SQLException {
        Optional<Currency> optional = currencyRepository.findByCode(code);
        optional.ifPresent(e -> {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            throw new IllegalArgumentException(String.format("currency code %s already exists", code));
        });
    }

    public void validateCurrencyCode(HttpServletResponse resp, String code) {
        if (!code.matches("\\p{Upper}{1,3}")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            throw new IllegalArgumentException(String.format("currency code \"%s\" is not a valid currency code", code));
        }
    }

    public String getField(HttpServletResponse resp,
                           String fieldValue) throws NoSuchElementException {
        if (fieldValue == null || fieldValue.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            throw new NoSuchElementException("required field is missing");
        }
        return fieldValue;
    }
}
