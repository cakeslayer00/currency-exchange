package com.vladsv.app.model;

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
