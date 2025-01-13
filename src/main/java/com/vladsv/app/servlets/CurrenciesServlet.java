package com.vladsv.app.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladsv.app.exceptions.CurrencyCodeAlreadyExistsException;
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
import java.util.List;

@WebServlet(value = "/currencies")
public class CurrenciesServlet extends HttpServlet {
    private final CurrencyRepository currencyRepository = new CurrencyRepository();
    private final Validator validator = new Validator(currencyRepository);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            List<Currency> currencies = currencyRepository.findAll();

            resp.getWriter().write(new ObjectMapper().writeValueAsString(currencies));
        } catch (SQLException e) {
            validator.handle(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database is unavailable");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_CREATED);

            String name = validator.getValidParameter(req.getParameter("name"));
            String code = validator.getValidParameter(req.getParameter("code"));
            String sign = validator.getValidParameter(req.getParameter("sign"));
            validator.validateCurrencyCode(code);
            validator.checkWhetherCurrencyCodeAlreadyExists(code);

            currencyRepository.save(Currency.builder()
                    .name(name)
                    .code(code)
                    .sign(sign)
                    .build());

        } catch (IllegalArgumentException | RequiredFieldMissingException e) {
            validator.handle(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            validator.handle(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database is unavailable");
        } catch (CurrencyCodeAlreadyExistsException e) {
            validator.handle(resp, HttpServletResponse.SC_CONFLICT, e.getMessage());
        }
    }
}
