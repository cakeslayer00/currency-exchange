package com.vladsv.app.repositories.impl;

import com.vladsv.app.models.ExchangeRate;
import com.vladsv.app.repositories.CrudRepository;

import java.util.List;

public class ExchangeRateRepository implements CrudRepository<ExchangeRate> {
    @Override
    public ExchangeRate findById(int id) {
        return null;
    }

    public ExchangeRate findByBaseAndTargetCurrency(String base, String target) {return null;}

    @Override
    public List<ExchangeRate> findAll() {
        return List.of();
    }

    @Override
    public void save(ExchangeRate exchangeRate) {

    }

    @Override
    public void update(ExchangeRate exchangeRate) {

    }

    @Override
    public void delete(ExchangeRate exchangeRate) {

    }
}
