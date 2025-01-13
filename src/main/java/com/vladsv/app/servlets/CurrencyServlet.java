package com.vladsv.app.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladsv.app.models.Currency;
import com.vladsv.app.repositories.impl.CurrencyRepository;
import com.vladsv.app.utils.ValidationHelper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Optional;

@WebServlet(value = "/currency/*")
public class CurrencyServlet extends HttpServlet {
    private final CurrencyRepository currencyRepository = new CurrencyRepository();
    private final ValidationHelper helper = new ValidationHelper(currencyRepository);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            String code = req.getPathInfo().substring(1);

            code = helper.getField(resp, code);
            helper.validateCurrencyCode(resp, code);

            Optional<Currency> optional = currencyRepository.findByCode(code);
            Currency currency = optional.orElseThrow(() -> {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return new NoSuchElementException("currency not found");
            });
            resp.getWriter().write(new ObjectMapper()
                    .writer()
                    .withDefaultPrettyPrinter()
                    .writeValueAsString(currency));
        } catch (IllegalArgumentException | SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        }
    }
    //TODO: reflect on exception handling system, complete exchange rates functionality;
}
