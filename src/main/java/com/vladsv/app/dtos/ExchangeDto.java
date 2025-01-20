package com.vladsv.app.dtos;

import com.vladsv.app.models.Currency;
import lombok.*;

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
