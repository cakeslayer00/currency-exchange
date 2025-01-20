package com.vladsv.app.utils;

import com.vladsv.app.dtos.ExchangeRateDto;
import com.vladsv.app.models.ExchangeRate;
import com.vladsv.app.repositories.impl.CurrencyRepository;
import org.modelmapper.ModelMapper;

import java.sql.SQLException;

public class MapperConfig {
    public static ModelMapper getConfiguredMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(ExchangeRate.class, ExchangeRateDto.class).addMappings(mapper -> {
            mapper.using(ctx -> {
                try {
                    return new CurrencyRepository().findById(((ExchangeRate) ctx.getSource()).getBaseCurrencyId()).orElseThrow();
                } catch (SQLException e) {
                    throw new RuntimeException("Error fetching base currency", e);
                }
            }).map(source -> source, ExchangeRateDto::setBaseCurrency);
            mapper.using(ctx -> {
                try {
                    return new CurrencyRepository().findById(((ExchangeRate) ctx.getSource()).getTargetCurrencyId()).orElseThrow();
                } catch (SQLException e) {
                    throw new RuntimeException("Error fetching target currency", e);
                }
            }).map(source -> source, ExchangeRateDto::setTargetCurrency);
        });
        return modelMapper;
    }
}
