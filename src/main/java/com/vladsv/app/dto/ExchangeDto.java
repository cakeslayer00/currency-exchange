package com.vladsv.app.dto;

import lombok.*;
import com.vladsv.app.model.Currency;

@NoArgsConstructor
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
