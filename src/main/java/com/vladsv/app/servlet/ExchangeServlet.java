package com.vladsv.app.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladsv.app.dto.ExchangeDto;
import com.vladsv.app.exception.RequiredParamMissingException;
import com.vladsv.app.exception.handlers.ExceptionHandler;
import com.vladsv.app.model.ExchangeRate;
import com.vladsv.app.repository.impl.CurrencyRepository;
import com.vladsv.app.repository.impl.ExchangeRateRepository;
import com.vladsv.app.util.Validator;
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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            resp.setStatus(HttpServletResponse.SC_CREATED);

            String baseCurrencyCode = validator.getRequiredParameter(req.getParameter("from"));
            String targetCurrencyCode = validator.getRequiredParameter(req.getParameter("to"));
            String amount = validator.getRequiredParameter(req.getParameter("amount"));

            CurrencyRepository currencyRepository = new CurrencyRepository();
            BigDecimal convertedAmount = new BigDecimal(amount);

            Optional<ExchangeRate> exchangeRate = getExchangeRate(baseCurrencyCode, targetCurrencyCode);

            BigDecimal rate = exchangeRate.orElseThrow(
                    () -> new IllegalArgumentException("There is no such exchange rate")
            ).getRate();
            BigDecimal result = rate.multiply(convertedAmount);
            wrapResponse(resp, currencyRepository, baseCurrencyCode, targetCurrencyCode, rate, convertedAmount, result);
        } catch (IllegalArgumentException | RequiredParamMissingException e) {
            handler.handle(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            handler.handle(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (NoSuchElementException e) {
            handler.handle(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }

    private Optional<ExchangeRate> getExchangeRate(String baseCurrencyCode, String targetCurrencyCode) throws SQLException {
        Optional<ExchangeRate> exchangeRate = exchangeRateRepository.findByCurrencyCodePair(baseCurrencyCode, targetCurrencyCode);

        if (exchangeRate.isEmpty()) {
            exchangeRate = getByReverseRate(targetCurrencyCode, baseCurrencyCode);
        }

        if (exchangeRate.isEmpty()) {
            exchangeRate = getByCrossRate(baseCurrencyCode, targetCurrencyCode);
        }

        return exchangeRate;
    }

    private Optional<ExchangeRate> getByCrossRate(String baseCurrencyCode, String targetCurrencyCode) throws SQLException {
        Optional<ExchangeRate> byUSDbaseA = exchangeRateRepository.findByCurrencyCodePair("USD", baseCurrencyCode);
        Optional<ExchangeRate> byUSDbaseB = exchangeRateRepository.findByCurrencyCodePair("USD", targetCurrencyCode);
        if (byUSDbaseA.isPresent() && byUSDbaseB.isPresent()) {
            return Optional.of(ExchangeRate.builder()
                    .baseCurrencyId(byUSDbaseA.get().getTargetCurrencyId())
                    .targetCurrencyId(byUSDbaseB.get().getTargetCurrencyId())
                    .rate(byUSDbaseB.get().getRate()
                            .divide(byUSDbaseA.get().getRate(), 6, RoundingMode.HALF_DOWN))
                    .build());
        }
        return Optional.empty();
    }

    private Optional<ExchangeRate> getByReverseRate(String targetCurrencyCode, String baseCurrencyCode) throws SQLException {
        Optional<ExchangeRate> byReverse = exchangeRateRepository.findByCurrencyCodePair(targetCurrencyCode, baseCurrencyCode);
        if (byReverse.isPresent()) {
            byReverse.get().setRate(BigDecimal.ONE.divide(byReverse.get().getRate(), 6, RoundingMode.HALF_DOWN));
            return byReverse;
        }
        return Optional.empty();

    }

    private void wrapResponse(HttpServletResponse resp,
                              CurrencyRepository currencyRepository,
                              String baseCurrencyCode,
                              String targetCurrencyCode,
                              BigDecimal rate,
                              BigDecimal convertedAmount,
                              BigDecimal result) throws IOException, SQLException {
        resp.getWriter().write(new ObjectMapper().writeValueAsString(ExchangeDto.builder()
                .baseCurrency(currencyRepository.findByCode(baseCurrencyCode).orElseThrow())
                .targetCurrency(currencyRepository.findByCode(targetCurrencyCode).orElseThrow())
                .rate(String.format("%.2f", rate))
                .amount(String.format("%.2f", convertedAmount))
                .convertedAmount(String.format("%.2f", result))
                .build()));
    }

}
