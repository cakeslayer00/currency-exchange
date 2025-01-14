package com.vladsv.app.filter;

import com.vladsv.app.repositories.impl.CurrencyRepository;
import com.vladsv.app.repositories.impl.ExchangeRateRepository;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;

import java.io.IOException;

@WebFilter(value = "/*")
public class ApplicationFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        chain.doFilter(request, response);
    }
}
