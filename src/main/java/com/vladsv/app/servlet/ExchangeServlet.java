package com.vladsv.app.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.vladsv.app.exception.ExceptionHandler;
import com.vladsv.app.model.ExchangeRate;
import com.vladsv.app.repository.CurrencyRepository;
import com.vladsv.app.repository.ExchangeRateRepository;
import com.vladsv.app.service.ExchangeService;
import com.vladsv.app.util.Validator;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Optional;

@WebServlet(value = "/exchange")
public class ExchangeServlet extends HttpServlet {
    private final ExchangeRateRepository exchangeRateRepository = new ExchangeRateRepository();
    private final ExchangeService exchangeService = new ExchangeService(exchangeRateRepository);
    private final ExceptionHandler handler = new ExceptionHandler();
    private final Validator validator = new Validator();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            resp.setStatus(HttpServletResponse.SC_CREATED);

            String baseCurrencyCode = validator.getRequiredParameter(req.getParameter("from"));
            String targetCurrencyCode = validator.getRequiredParameter(req.getParameter("to"));
            String amount = validator.getRequiredParameter(req.getParameter("amount"));

            CurrencyRepository currencyRepository = new CurrencyRepository();
            BigDecimal convertedAmount = new BigDecimal(amount);

            Optional<ExchangeRate> exchangeRate = exchangeService.getExchangeRate(baseCurrencyCode, targetCurrencyCode);

            BigDecimal rate = exchangeRate.orElseThrow(
                    () -> new IllegalArgumentException("There is no such exchange rate")
            ).getRate();
            BigDecimal result = rate.multiply(convertedAmount);

            exchangeService.wrapResponse(resp, currencyRepository, baseCurrencyCode, targetCurrencyCode, rate, convertedAmount, result);
        } catch (IllegalArgumentException e) {
            handler.handle(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            handler.handleSQLException(resp, e);
        } catch (NoSuchElementException e) {
            handler.handle(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }
}
