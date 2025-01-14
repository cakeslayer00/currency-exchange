package com.vladsv.app.repositories.impl;

import com.vladsv.app.models.Currency;
import com.vladsv.app.repositories.CrudRepository;
import com.vladsv.app.utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyRepository implements CrudRepository<Currency> {
    @Override
    public Optional<Currency> findById(int id) throws SQLException {
        String query = "SELECT * FROM currencies WHERE id = ?";
        try (Connection conn = DataSource.getConnection();
             PreparedStatement st = conn.prepareStatement(query)) {
            st.setInt(1, id);
            st.execute();
            ResultSet resultSet = st.getResultSet();

            if (!resultSet.next()) {
                return Optional.empty();
            }
            return Optional.of(getCurrency(resultSet));
        }
    }

    public Optional<Currency> findByCode(String code) throws SQLException {
        String query = "SELECT * FROM currencies WHERE code = ?";

        try (Connection conn = DataSource.getConnection();
             PreparedStatement st = conn.prepareStatement(query)) {
            st.setString(1, code);
            st.execute();
            ResultSet resultSet = st.getResultSet();

            if (!resultSet.next()) {
                return Optional.empty();
            }
            return Optional.of(getCurrency(resultSet));
        }
    }

    @Override
    public List<Currency> findAll() throws SQLException {
        String query = "SELECT * FROM currencies";

        try (Connection conn = DataSource.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            List<Currency> currencies = new ArrayList<>();
            while (rs.next()) {
                currencies.add(getCurrency(rs));
            }
            return currencies;
        }
    }

    @Override
    public void save(Currency currency) throws SQLException {
        String query = "INSERT INTO currencies(full_name,code,sign) VALUES(?, ?, ?)";

        try (Connection conn = DataSource.getConnection();
             PreparedStatement st = conn.prepareStatement(query)) {
            st.setString(1, currency.getName());
            st.setString(2, currency.getCode());
            st.setString(3, currency.getSign());
            st.execute();
        }
    }

    @Override
    public void update(Currency currency) throws SQLException {

    }

    @Override
    public void delete(Currency currency) throws SQLException {

    }

    private Currency getCurrency(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String code = rs.getString("code");
        String name = rs.getString("full_name");
        String sign = rs.getString("sign");

        return Currency.builder()
                .id(id)
                .code(code)
                .name(name)
                .sign(sign)
                .build();
    }
}
