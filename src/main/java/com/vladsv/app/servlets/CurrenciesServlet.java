package com.vladsv.app.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladsv.app.exceptions.RequiredParamMissingException;
import com.vladsv.app.exceptions.handlers.ExceptionHandler;
import com.vladsv.app.models.Currency;
import com.vladsv.app.repositories.impl.CurrencyRepository;
import com.vladsv.app.utils.Validator;
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
    private final ExceptionHandler handler = new ExceptionHandler();
    private final Validator validator = new Validator();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            List<Currency> currencies = currencyRepository.findAll();
            resp.getWriter().write(new ObjectMapper().writeValueAsString(currencies));
        } catch (SQLException e) {
            handler.handle(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database is unavailable");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            resp.setStatus(HttpServletResponse.SC_CREATED);

            String name = validator.getRequiredParameter(req.getParameter("name"));
            String code = validator.getRequiredParameter(req.getParameter("code"));
            String sign = validator.getRequiredParameter(req.getParameter("sign"));

            currencyRepository.save(Currency.builder()
                    .name(name)
                    .code(code)
                    .sign(sign)
                    .build()
            );
        } catch (IllegalArgumentException | RequiredParamMissingException e) {
            handler.handle(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            handler.handle(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
