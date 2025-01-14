package com.vladsv.app.mappers;

import com.vladsv.app.dtos.ExchangeDto;
import com.vladsv.app.models.Currency;
import com.vladsv.app.repositories.impl.CurrencyRepository;

public class ExchangeMapper {
    private final CurrencyRepository currencyRepository = new CurrencyRepository();

    public ExchangeDto map(
            Currency base,
            Currency target,
            String rate,
            String amount,
            String convertedAmount
    ) {
        return ExchangeDto.builder()
                .baseCurrency(base)
                .targetCurrency(target)
                .rate(rate)
                .amount(amount)
                .convertedAmount(convertedAmount)
                .build();
    }
}
