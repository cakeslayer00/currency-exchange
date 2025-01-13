package com.vladsv.app.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladsv.app.exceptions.CurrencyDoesNotExistException;
import com.vladsv.app.exceptions.RequiredFieldMissingException;
import com.vladsv.app.models.Currency;
import com.vladsv.app.repositories.impl.CurrencyRepository;
import com.vladsv.app.utils.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet(value = "/currency/*")
public class CurrencyServlet extends HttpServlet {
    private final CurrencyRepository currencyRepository = new CurrencyRepository();
    private final Validator validator = new Validator(currencyRepository);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_OK);

            String code = validator.getValidParameter(
                    req.getPathInfo().substring(1)
            );
            validator.validateCurrencyCode(code);
            Currency currency = currencyRepository.findByCode(code).orElseThrow(
                    () -> new CurrencyDoesNotExistException("Currency with this code doesn't exist")
            );

            resp.getWriter().write(new ObjectMapper().writeValueAsString(currency));
        } catch (RequiredFieldMissingException | IllegalArgumentException e) {
            validator.handle(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            validator.handle(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database is unavailable");
        } catch (CurrencyDoesNotExistException e) {
            validator.handle(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }

    }
}
