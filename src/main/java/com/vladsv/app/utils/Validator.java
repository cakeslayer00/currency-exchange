package com.vladsv.app.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladsv.app.dtos.AcknowledgmentDto;
import com.vladsv.app.exceptions.CurrencyCodeAlreadyExistsException;
import com.vladsv.app.exceptions.RequiredFieldMissingException;
import com.vladsv.app.repositories.impl.CurrencyRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

public class Validator {
    private final CurrencyRepository currencyRepository;

    public Validator(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    public void handle(HttpServletResponse resp, int httpStatusCode, String message) throws IOException {
        resp.setStatus(httpStatusCode);
        resp.getWriter().write(new ObjectMapper().writeValueAsString(
                AcknowledgmentDto.builder()
                        .httpResponseCode(httpStatusCode)
                        .httpResponseMessage(message)
                        .build()
        ));
    }

    public void checkWhetherCurrencyCodeAlreadyExists(String code) throws SQLException, CurrencyCodeAlreadyExistsException {
        if (currencyRepository.findByCode(code).isPresent())
            throw new CurrencyCodeAlreadyExistsException("Currency with this code already exists");
    }

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

}
