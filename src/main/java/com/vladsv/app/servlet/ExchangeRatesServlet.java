package com.vladsv.app.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladsv.app.dto.ExchangeRateDto;
import com.vladsv.app.exception.CurrencyDoesNotExistsException;
import com.vladsv.app.exception.RequiredParamMissingException;
import com.vladsv.app.exception.handlers.ExceptionHandler;
import com.vladsv.app.model.Currency;
import com.vladsv.app.model.ExchangeRate;
import com.vladsv.app.repository.impl.CurrencyRepository;
import com.vladsv.app.repository.impl.ExchangeRateRepository;
import com.vladsv.app.util.MapperConfig;
import com.vladsv.app.util.Validator;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.modelmapper.ModelMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@WebServlet(value = "/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private final ExchangeRateRepository exchangeRateRepository = new ExchangeRateRepository();
    private final ExceptionHandler handler = new ExceptionHandler();
    private final Validator validator = new Validator();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            List<ExchangeRate> exchangeRates = exchangeRateRepository.findAll();

            ModelMapper configuredMapper = MapperConfig.getConfiguredMapper();
            List<ExchangeRateDto> res = exchangeRates.stream()
                    .map(rate -> configuredMapper.map(rate, ExchangeRateDto.class))
                    .toList();
            resp.getWriter().write(new ObjectMapper().writeValueAsString(res));
        } catch (SQLException e) {
            handler.handleSQLException(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            resp.setStatus(HttpServletResponse.SC_CREATED);

            String baseCurrencyCode = validator.getRequiredParameter(req.getParameter("baseCurrencyCode"));
            String targetCurrencyCode = validator.getRequiredParameter(req.getParameter("targetCurrencyCode"));
            String rate = validator.getRequiredParameter(req.getParameter("rate"));
            validator.checkCurrencyCode(baseCurrencyCode);
            validator.checkCurrencyCode(targetCurrencyCode);

            CurrencyRepository currencyRepository = new CurrencyRepository();

            Currency baseCurrency = validator.getExistingCurrency(currencyRepository, baseCurrencyCode);
            Currency targetCurrency = validator.getExistingCurrency(currencyRepository, targetCurrencyCode);

            exchangeRateRepository.save(ExchangeRate.builder()
                    .baseCurrencyId(baseCurrency.getId())
                    .targetCurrencyId(targetCurrency.getId())
                    .rate(BigDecimal.valueOf(Double.parseDouble(rate)))
                    .build()
            );
        } catch (IllegalArgumentException e) {
            handler.handle(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            handler.handleSQLException(resp, e);
        } catch (CurrencyDoesNotExistsException e) {
            handler.handle(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }
}
