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
    public Currency findById(int id) {
        return null;
    }

    public Optional<Currency> findByCode(String code) {
        String query = "SELECT * FROM currencies WHERE code = ?";

        try (Connection conn = DataSource.getConnection();
             PreparedStatement st = conn.prepareStatement(query);
        ) {
            st.setString(1, code);
            st.execute();
            ResultSet resultSet = st.getResultSet();

            if (!resultSet.next()) {
                return Optional.empty();
            }

            int id = resultSet.getInt("id");
            String name = resultSet.getString("full_name");
            String sign = resultSet.getString("sign");

            return Optional.of(
                    Currency.builder()
                            .id(id)
                            .code(code)
                            .name(name)
                            .sign(sign).build()
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Currency> findAll() throws SQLException {
        try (Connection conn = DataSource.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM currencies")
        ) {
            List<Currency> currencies = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                String code = rs.getString("code");
                String name = rs.getString("full_name");
                String sign = rs.getString("sign");

                currencies.add(
                        Currency.builder()
                                .id(id)
                                .name(name)
                                .code(code)
                                .sign(sign).build()
                );
            }

            return currencies;
        }
    }

    @Override
    public void save(Currency currency) {

    }

    @Override
    public void update(Currency currency) {

    }

    @Override
    public void delete(Currency currency) {

    }
}
