package com.vladsv.app.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladsv.app.exceptions.CurrencyDoesNotExistsException;
import com.vladsv.app.exceptions.ExchangeRateDoesNotExistsException;
import com.vladsv.app.exceptions.RequiredFieldMissingException;
import com.vladsv.app.exceptions.handlers.ExceptionHandler;
import com.vladsv.app.mappers.ExchangeRateMapper;
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

@WebServlet(value = "/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private final ExchangeRateRepository exchangeRateRepository = new ExchangeRateRepository();
    private final ExceptionHandler handler = new ExceptionHandler();
    private final Validator validator = new Validator();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            resp.setStatus(HttpServletResponse.SC_OK);

            String base = validator.getValidParameter(
                    req.getPathInfo().substring(1, 4)
            );
            String target = validator.getValidParameter(
                    req.getPathInfo().substring(4)
            );

            validator.validateCurrencyCode(base);
            validator.validateCurrencyCode(target);
            CurrencyRepository currencyRepository = new CurrencyRepository();
            validator.getValidCurrency(currencyRepository, base);
            validator.getValidCurrency(currencyRepository, target);

            ExchangeRate exchangeRate = exchangeRateRepository.findByBaseAndTargetCurrencyCode(base, target).orElseThrow(
                    () -> new ExchangeRateDoesNotExistsException("Exchange rate with this code pair doesn't exists")
            );
            ExchangeRateMapper exchangeRateMapper = new ExchangeRateMapper();
            resp.getWriter().write(
                    new ObjectMapper().writeValueAsString(
                            exchangeRateMapper.map(exchangeRate)
                    )
            );
        } catch (RequiredFieldMissingException | IllegalArgumentException e) {
            handler.handle(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            handler.handle(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database is unavailable");
        } catch (CurrencyDoesNotExistsException | ExchangeRateDoesNotExistsException e) {
            handler.handle(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            resp.setStatus(HttpServletResponse.SC_OK);

            String base = validator.getValidParameter(
                    req.getPathInfo().substring(1, 4)
            );
            String target = validator.getValidParameter(
                    req.getPathInfo().substring(4)
            );
            String rate = validator.getValidParameter(req.getParameter("rate"));

            validator.validateCurrencyCode(base);
            validator.validateCurrencyCode(target);
            CurrencyRepository currencyRepository = new CurrencyRepository();
            validator.getValidCurrency(currencyRepository, base);
            validator.getValidCurrency(currencyRepository, target);

            ExchangeRate exchangeRate = exchangeRateRepository.findByBaseAndTargetCurrencyCode(base, target).orElseThrow(
                    () -> new ExchangeRateDoesNotExistsException("Exchange rate with this code pair doesn't exists")
            );
            exchangeRate.setRate(BigDecimal.valueOf(Double.parseDouble(rate)));
            exchangeRateRepository.update(exchangeRate);
            ExchangeRateMapper exchangeRateMapper = new ExchangeRateMapper();
            resp.getWriter().write(
                    new ObjectMapper().writeValueAsString(
                            exchangeRateMapper.map(exchangeRate)
                    )
            );
        } catch (RequiredFieldMissingException | IllegalArgumentException e) {
            handler.handle(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            handler.handle(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (CurrencyDoesNotExistsException | ExchangeRateDoesNotExistsException e) {
            handler.handle(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }
}
