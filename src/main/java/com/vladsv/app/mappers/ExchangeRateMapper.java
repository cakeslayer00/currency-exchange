package com.vladsv.app.mappers;

import com.vladsv.app.dtos.ExchangeRateDto;
import com.vladsv.app.models.ExchangeRate;
import com.vladsv.app.repositories.impl.CurrencyRepository;

import java.sql.SQLException;

public class ExchangeRateMapper {
    private final CurrencyRepository currencyRepository = new CurrencyRepository();

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public ExchangeRateDto map(ExchangeRate exchangeRate) throws SQLException {
        return ExchangeRateDto.builder()
                .id(exchangeRate.getId())
                .baseCurrency(currencyRepository.findById(exchangeRate.getBaseCurrencyId()).get())
                .targetCurrency(currencyRepository.findById(exchangeRate.getTargetCurrencyId()).get())
                .rate(exchangeRate.getRate())
                .build();
    }
}
