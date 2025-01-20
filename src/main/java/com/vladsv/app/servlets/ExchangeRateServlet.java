package com.vladsv.app.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladsv.app.dtos.ExchangeRateDto;
import com.vladsv.app.exceptions.ExchangeRateDoesNotExistsException;
import com.vladsv.app.exceptions.RequiredParamMissingException;
import com.vladsv.app.exceptions.handlers.ExceptionHandler;
import com.vladsv.app.models.ExchangeRate;
import com.vladsv.app.repositories.impl.ExchangeRateRepository;
import com.vladsv.app.utils.Validator;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.modelmapper.ModelMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import static com.vladsv.app.utils.MapperConfig.getConfiguredMapper;

@WebServlet(value = "/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private final ExchangeRateRepository exchangeRateRepository = new ExchangeRateRepository();
    private final ExceptionHandler handler = new ExceptionHandler();
    private final Validator validator = new Validator();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            resp.setStatus(HttpServletResponse.SC_OK);

            String base = validator.getRequiredParameter(req.getPathInfo().substring(1, 4));
            String target = validator.getRequiredParameter(req.getPathInfo().substring(4));

            ExchangeRate exchangeRate = exchangeRateRepository.findByCurrencyCodePair(base, target).orElseThrow(() -> new ExchangeRateDoesNotExistsException("Exchange rate with this code pair doesn't exists"));
            wrapResponse(resp, exchangeRate);
        } catch (RequiredParamMissingException | IllegalArgumentException e) {
            handler.handle(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            handler.handle(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database is unavailable");
        } catch (NoSuchElementException e) {
            handler.handle(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            resp.setStatus(HttpServletResponse.SC_OK);

            String base = validator.getRequiredParameter(req.getPathInfo().substring(1, 4));
            String target = validator.getRequiredParameter(req.getPathInfo().substring(4));
            String rate = validator.getRequiredParameter(req.getParameter("rate"));

            ExchangeRate exchangeRate = exchangeRateRepository.findByCurrencyCodePair(base, target).orElseThrow(() -> new ExchangeRateDoesNotExistsException("Exchange rate with this code pair doesn't exists"));
            exchangeRate.setRate(BigDecimal.valueOf(Double.parseDouble(rate)));
            exchangeRateRepository.update(exchangeRate);
            wrapResponse(resp, exchangeRate);
        } catch (RequiredParamMissingException | IllegalArgumentException e) {
            handler.handle(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            handler.handle(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (NoSuchElementException e) {
            handler.handle(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }

    private void wrapResponse(HttpServletResponse resp, ExchangeRate exchangeRate) throws IOException {
        ModelMapper modelMapper = getConfiguredMapper();
        resp.getWriter().write(new ObjectMapper().writeValueAsString(modelMapper.map(exchangeRate, ExchangeRateDto.class)));
    }
}
