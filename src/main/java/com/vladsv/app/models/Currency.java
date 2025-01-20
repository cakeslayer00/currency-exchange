package com.vladsv.app.models;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Currency {
    private Integer id;
    private String code;
    private String name;
    private String sign;
}
