package com.vladsv.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import com.vladsv.app.dto.ExchangeDto;
import com.vladsv.app.model.ExchangeRate;
import com.vladsv.app.repository.CurrencyRepository;
import com.vladsv.app.repository.ExchangeRateRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.Optional;

public class ExchangeService {
    private final ExchangeRateRepository exchangeRateRepository;

    public ExchangeService(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }

    public Optional<ExchangeRate> getExchangeRate(String baseCurrencyCode, String targetCurrencyCode) throws SQLException {
        Optional<ExchangeRate> exchangeRate = exchangeRateRepository.findByCurrencyCodePair(baseCurrencyCode, targetCurrencyCode);

        if (exchangeRate.isEmpty()) {
            exchangeRate = getByReverseRate(targetCurrencyCode, baseCurrencyCode);
        }

        if (exchangeRate.isEmpty()) {
            exchangeRate = getByCrossRate(baseCurrencyCode, targetCurrencyCode);
        }

        return exchangeRate;
    }

    public void wrapResponse(HttpServletResponse resp, CurrencyRepository currencyRepository, String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate, BigDecimal convertedAmount, BigDecimal result) throws IOException, SQLException {
        resp.getWriter().write(
                new ObjectMapper()
                        .writeValueAsString(ExchangeDto.builder()
                                .baseCurrency(currencyRepository.findByCode(baseCurrencyCode).orElseThrow())
                                .targetCurrency(currencyRepository.findByCode(targetCurrencyCode).orElseThrow())
                                .rate(String.format("%.2f", rate))
                                .amount(String.format("%.2f", convertedAmount))
                                .convertedAmount(String.format("%.2f", result))
                                .build()));
    }

    private Optional<ExchangeRate> getByCrossRate(String baseCurrencyCode, String targetCurrencyCode) throws SQLException {
        Optional<ExchangeRate> byUSDbaseA = exchangeRateRepository.findByCurrencyCodePair("USD", baseCurrencyCode);
        Optional<ExchangeRate> byUSDbaseB = exchangeRateRepository.findByCurrencyCodePair("USD", targetCurrencyCode);

        if (byUSDbaseA.isPresent() && byUSDbaseB.isPresent()) {
            return Optional.of(ExchangeRate.builder().baseCurrencyId(byUSDbaseA.get().getTargetCurrencyId()).targetCurrencyId(byUSDbaseB.get().getTargetCurrencyId()).rate(byUSDbaseB.get().getRate().divide(byUSDbaseA.get().getRate(), 6, RoundingMode.HALF_DOWN)).build());
        }

        return Optional.empty();
    }

    private Optional<ExchangeRate> getByReverseRate(String targetCurrencyCode, String baseCurrencyCode) throws SQLException {
        Optional<ExchangeRate> byReverse = exchangeRateRepository.findByCurrencyCodePair(targetCurrencyCode, baseCurrencyCode);

        if (byReverse.isPresent()) {
            byReverse.get().setRate(BigDecimal.ONE.divide(
                    byReverse.get().getRate(),
                    6,
                    RoundingMode.HALF_DOWN)
            );
            return byReverse;
        }

        return Optional.empty();
    }
}
