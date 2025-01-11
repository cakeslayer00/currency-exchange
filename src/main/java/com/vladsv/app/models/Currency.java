package com.vladsv.app.models;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Currency {
    private Integer id;
    private String code;
    private String name;
    private String sign;
}
