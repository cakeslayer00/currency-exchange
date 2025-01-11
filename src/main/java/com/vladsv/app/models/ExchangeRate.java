package com.vladsv.app.models;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExchangeRate {
    private Integer id;
    private Integer baseRateId;
    private Integer targetRateId;
    private Double rate;
}
