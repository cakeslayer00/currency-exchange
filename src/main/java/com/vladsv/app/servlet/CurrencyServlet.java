package com.vladsv.app.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladsv.app.exception.RequiredParamMissingException;
import com.vladsv.app.exception.handlers.ExceptionHandler;
import com.vladsv.app.model.Currency;
import com.vladsv.app.repository.impl.CurrencyRepository;
import com.vladsv.app.util.Validator;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.NoSuchElementException;

@WebServlet(value = "/currency/*")
public class CurrencyServlet extends HttpServlet {
    private final CurrencyRepository currencyRepository = new CurrencyRepository();
    private final ExceptionHandler handler = new ExceptionHandler();
    private final Validator validator = new Validator();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            resp.setStatus(HttpServletResponse.SC_OK);

            String code = validator.getRequiredParameter(
                    req.getPathInfo().substring(1)
            );
            Currency currency = validator.getExistingCurrency(currencyRepository, code);

            resp.getWriter().write(new ObjectMapper().writeValueAsString(currency));
        } catch (IllegalArgumentException e) {
            handler.handle(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            handler.handleSQLException(resp, e);
        } catch (NoSuchElementException e) {
            handler.handle(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }
}
