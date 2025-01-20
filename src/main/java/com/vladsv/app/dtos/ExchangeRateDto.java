package com.vladsv.app.dtos;

import com.vladsv.app.models.Currency;
import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ExchangeRateDto {
    private int id;
    private Currency baseCurrency;
    private Currency targetCurrency;
    private BigDecimal rate;
}
