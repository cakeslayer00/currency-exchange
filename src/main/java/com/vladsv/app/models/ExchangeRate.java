package com.vladsv.app.models;

import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ExchangeRate {
    private Integer id;
    private Integer baseCurrencyId;
    private Integer targetCurrencyId;
    private BigDecimal rate;
}
