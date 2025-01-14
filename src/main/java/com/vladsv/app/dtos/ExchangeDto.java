package com.vladsv.app.dtos;

import com.vladsv.app.models.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class ExchangeDto {
    private Currency baseCurrency;
    private Currency targetCurrency;
    private String rate;
    private String amount;
    private String convertedAmount;
}
