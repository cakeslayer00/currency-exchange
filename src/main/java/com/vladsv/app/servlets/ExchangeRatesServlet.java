package com.vladsv.app.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladsv.app.dtos.ExchangeRateDto;
import com.vladsv.app.exceptions.CurrencyDoesNotExistsException;
import com.vladsv.app.exceptions.RequiredParamMissingException;
import com.vladsv.app.exceptions.handlers.ExceptionHandler;
import com.vladsv.app.models.Currency;
import com.vladsv.app.models.ExchangeRate;
import com.vladsv.app.repositories.impl.CurrencyRepository;
import com.vladsv.app.repositories.impl.ExchangeRateRepository;
import com.vladsv.app.utils.MapperConfig;
import com.vladsv.app.utils.Validator;
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
            handler.handle(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            resp.setStatus(HttpServletResponse.SC_CREATED);

            String baseCurrencyCode = validator.getRequiredParameter(req.getParameter("baseCurrencyCode"));
            String targetCurrencyCode = validator.getRequiredParameter(req.getParameter("targetCurrencyCode"));
            String rate = validator.getRequiredParameter(req.getParameter("rate"));

            CurrencyRepository currencyRepository = new CurrencyRepository();

            Currency baseCurrency = validator.getExistingCurrency(currencyRepository, baseCurrencyCode);
            Currency targetCurrency = validator.getExistingCurrency(currencyRepository, targetCurrencyCode);

            exchangeRateRepository.save(ExchangeRate.builder()
                    .baseCurrencyId(baseCurrency.getId())
                    .targetCurrencyId(targetCurrency.getId())
                    .rate(BigDecimal.valueOf(Double.parseDouble(rate)))
                    .build()
            );
        } catch (IllegalArgumentException | RequiredParamMissingException e) {
            handler.handle(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            handler.handle(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (CurrencyDoesNotExistsException e) {
            handler.handle(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }
}
