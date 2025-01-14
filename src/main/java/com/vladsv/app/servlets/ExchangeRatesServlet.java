package com.vladsv.app.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladsv.app.dtos.ExchangeRateDto;
import com.vladsv.app.exceptions.CurrencyDoesNotExistsException;
import com.vladsv.app.exceptions.RequiredFieldMissingException;
import com.vladsv.app.exceptions.handlers.ExceptionHandler;
import com.vladsv.app.mappers.ExchangeRateMapper;
import com.vladsv.app.models.Currency;
import com.vladsv.app.models.ExchangeRate;
import com.vladsv.app.repositories.impl.CurrencyRepository;
import com.vladsv.app.repositories.impl.ExchangeRateRepository;
import com.vladsv.app.utils.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(value = "/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private final ExchangeRateRepository exchangeRateRepository = new ExchangeRateRepository();
    private final ExceptionHandler handler = new ExceptionHandler();
    private final Validator validator = new Validator();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<ExchangeRate> exchangeRates = exchangeRateRepository.findAll();

            ExchangeRateMapper exchangeRateMapper = new ExchangeRateMapper();
            List<ExchangeRateDto> res = new ArrayList<>();
            for (ExchangeRate exchangeRate : exchangeRates) {
                ExchangeRateDto map = exchangeRateMapper.map(exchangeRate);
                res.add(map);
            }
            resp.getWriter().write(new ObjectMapper().writeValueAsString(res));
        } catch (SQLException e) {
            handler.handle(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database is unavailable");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            resp.setStatus(HttpServletResponse.SC_CREATED);

            String baseCurrencyCode = validator.getValidParameter(req.getParameter("baseCurrencyCode"));
            String targetCurrencyCode = validator.getValidParameter(req.getParameter("targetCurrencyCode"));
            String rate = validator.getValidParameter(req.getParameter("rate"));

            validator.validateCurrencyCode(baseCurrencyCode);
            validator.validateCurrencyCode(targetCurrencyCode);

            CurrencyRepository currencyRepository = new CurrencyRepository();

            Currency baseCurrency = validator.getValidCurrency(currencyRepository, baseCurrencyCode);
            Currency targetCurrency = validator.getValidCurrency(currencyRepository, targetCurrencyCode);

            exchangeRateRepository.save(ExchangeRate.builder()
                    .baseCurrencyId(baseCurrency.getId())
                    .targetCurrencyId(targetCurrency.getId())
                    .rate(BigDecimal.valueOf(Double.parseDouble(rate)))
                    .build());

        } catch (IllegalArgumentException | RequiredFieldMissingException e) {
            handler.handle(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            handler.handle(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (CurrencyDoesNotExistsException e) {
            handler.handle(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }
}
