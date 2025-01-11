package com.vladsv.app.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladsv.app.models.Currency;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet(value = "/currencies")
public class CurrencyServlet extends HttpServlet {
    //TODO:Break down to repositories, apply HikariCP, checkout difference between DAOs and Repos, checkout is there
    //TODO:better way to extract data through JDBC, cuz lines 30-34 looks ugly
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection("jdbc:sqlite:D:\\applications\\sqlite\\currency.db");

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM currencies");
            List<Currency> currencies = new ArrayList<>();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String code = resultSet.getString("code");
                String name = resultSet.getString("full_name");
                String sign = resultSet.getString("sign");

                currencies.add(
                        Currency.builder()
                                .id(id)
                                .name(name)
                                .code(code)
                                .sign(sign).build()
                );
            }

            ObjectMapper mapper = new ObjectMapper();
            String res = mapper.writeValueAsString(currencies);
            resp.setContentType("application/json");
            resp.getWriter().write(res);

            statement.close();

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
