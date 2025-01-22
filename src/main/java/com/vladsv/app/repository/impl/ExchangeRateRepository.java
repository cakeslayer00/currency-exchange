package com.vladsv.app.repository.impl;

import com.vladsv.app.exception.CurrencyDoesNotExistsException;
import com.vladsv.app.model.Currency;
import com.vladsv.app.model.ExchangeRate;
import com.vladsv.app.repository.CrudRepository;
import com.vladsv.app.util.DataSource;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateRepository implements CrudRepository<ExchangeRate> {

    @Override
    public Optional<ExchangeRate> findById(int id) throws SQLException {
        return Optional.empty();
    }

    public Optional<ExchangeRate> findByCurrencyCodePair(String base, String target) throws SQLException {
        String query = "SELECT * FROM exchange_rates WHERE base_currency_id = ? AND target_currency_id = ?";
        try (Connection conn = DataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            CurrencyRepository currencyRepository = new CurrencyRepository();

            Currency baseCurrency = currencyRepository.findByCode(base).orElseThrow(
                    () -> new CurrencyDoesNotExistsException(String.format("Currency with code '%s' not found", base))
            );
            Currency targetCurrency = currencyRepository.findByCode(target).orElseThrow(
                    () -> new CurrencyDoesNotExistsException(String.format("Currency with code '%s' not found", target))
            );

            stmt.setInt(1, baseCurrency.getId());
            stmt.setInt(2, targetCurrency.getId());
            stmt.execute();
            ResultSet rs = stmt.getResultSet();

            if (!rs.next()) {
                return Optional.empty();
            }
            return Optional.of(getExchangeRate(rs));
        }
    }

    @Override
    public List<ExchangeRate> findAll() throws SQLException {
        String query = "SELECT * FROM exchange_rates";
        try (Connection connection = DataSource.getConnection(); Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
            List<ExchangeRate> exchangeRates = new ArrayList<>();
            while (resultSet.next()) {
                exchangeRates.add(getExchangeRate(resultSet));
            }
            return exchangeRates;
        }
    }

    @Override
    public void save(ExchangeRate exchangeRate) throws SQLException {
        String query = "INSERT INTO exchange_rates(base_currency_id, target_currency_id, rate) VALUES(?, ?, ?)";
        try (Connection conn = DataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, exchangeRate.getBaseCurrencyId());
            stmt.setInt(2, exchangeRate.getTargetCurrencyId());
            stmt.setBigDecimal(3, exchangeRate.getRate());
            stmt.execute();
        }
    }

    @Override
    public void update(ExchangeRate exchangeRate) throws SQLException {
        String query = "UPDATE exchange_rates SET rate = ? WHERE id = ?";
        try (Connection conn = DataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setBigDecimal(1, exchangeRate.getRate());
            stmt.setInt(2, exchangeRate.getId());
            stmt.execute();
        }
    }

    @Override
    public void delete(ExchangeRate exchangeRate) throws SQLException {

    }

    private ExchangeRate getExchangeRate(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        int baseCurrencyId = resultSet.getInt("base_currency_id");
        int targetCurrencyId = resultSet.getInt("target_currency_id");
        BigDecimal rate = resultSet.getBigDecimal("rate");

        return ExchangeRate.builder().id(id).baseCurrencyId(baseCurrencyId).targetCurrencyId(targetCurrencyId).rate(rate).build();
    }
}
