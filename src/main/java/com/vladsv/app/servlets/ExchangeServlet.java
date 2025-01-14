package com.vladsv.app.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladsv.app.exceptions.RequiredFieldMissingException;
import com.vladsv.app.exceptions.handlers.ExceptionHandler;
import com.vladsv.app.mappers.ExchangeMapper;
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
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Optional;

@WebServlet(value = "/exchange")
public class ExchangeServlet extends HttpServlet {
    private final ExchangeRateRepository exchangeRateRepository = new ExchangeRateRepository();
    private final ExceptionHandler handler = new ExceptionHandler();
    private final Validator validator = new Validator();
    private final ExchangeMapper mapper = new ExchangeMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            resp.setStatus(HttpServletResponse.SC_CREATED);

            String baseCurrencyCode = validator.getValidParameter(req.getParameter("from"));
            String targetCurrencyCode = validator.getValidParameter(req.getParameter("to"));
            String amount = validator.getValidParameter(req.getParameter("amount"));

            validator.validateCurrencyCode(baseCurrencyCode);
            validator.validateCurrencyCode(targetCurrencyCode);
            CurrencyRepository currencyRepository = new CurrencyRepository();
            Currency baseCurrency = validator.getValidCurrency(currencyRepository, baseCurrencyCode);
            Currency targetCurrency = validator.getValidCurrency(currencyRepository, targetCurrencyCode);

            assembleResponse(
                    resp,
                    baseCurrencyCode,
                    targetCurrencyCode,
                    BigDecimal.valueOf(Double.parseDouble(amount)),
                    baseCurrency,
                    targetCurrency
            );
        } catch (IllegalArgumentException | RequiredFieldMissingException e) {
            handler.handle(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            handler.handle(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (NoSuchElementException e) {
            handler.handle(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }

    private void assembleResponse(
            HttpServletResponse resp,
            String baseCurrencyCode,
            String targetCurrencyCode,
            BigDecimal amount,
            Currency baseCurrency,
            Currency targetCurrency
    ) throws SQLException, IOException {
        Optional<ExchangeRate> byBaseAndTargetCurrencyCode =
                exchangeRateRepository.findByBaseAndTargetCurrencyCode(baseCurrencyCode, targetCurrencyCode);

        Optional<ExchangeRate> byTargetAndBaseCurrencyCode =
                exchangeRateRepository.findByBaseAndTargetCurrencyCode(targetCurrencyCode, baseCurrencyCode);

        Optional<ExchangeRate> byUSDAndBaseCurrencyCode =
                exchangeRateRepository.findByBaseAndTargetCurrencyCode("USD", baseCurrencyCode);

        Optional<ExchangeRate> byUSDAndTargetCurrencyCode =
                exchangeRateRepository.findByBaseAndTargetCurrencyCode("USD", targetCurrencyCode);

        if (byBaseAndTargetCurrencyCode.isPresent()) {
            BigDecimal rate = byBaseAndTargetCurrencyCode.get().getRate();
            createResponse(resp, rate, amount, baseCurrency, targetCurrency);
        } else if (byTargetAndBaseCurrencyCode.isPresent()) {
            BigDecimal rate = BigDecimal.valueOf(1d)
                    .divide(
                            byTargetAndBaseCurrencyCode.get().getRate(),
                            RoundingMode.UNNECESSARY
                    );
            createResponse(resp, rate, amount, baseCurrency, targetCurrency);
        } else if (byUSDAndBaseCurrencyCode.isPresent() && byUSDAndTargetCurrencyCode.isPresent()) {
            BigDecimal rate = byUSDAndTargetCurrencyCode.get().getRate()
                    .divide(
                            byUSDAndBaseCurrencyCode.get().getRate(),
                            RoundingMode.UNNECESSARY
                    );
            createResponse(resp, rate, amount, baseCurrency, targetCurrency);
        } else {
            throw new NoSuchElementException("No such exchange rate");
        }
    }

    private void createResponse(
            HttpServletResponse resp,
            BigDecimal rate,
            BigDecimal amount,
            Currency baseCurrency,
            Currency targetCurrency
    ) throws IOException {
        BigDecimal res = rate.multiply(amount);
        resp.getWriter().write(new ObjectMapper().writeValueAsString(mapper.map(
                baseCurrency,
                targetCurrency,
                String.format("%.2f", rate),
                String.format("%.2f", amount),
                String.format("%.2f", res)
        )));
    }
}
