package com.vladsv.app.exception.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladsv.app.dto.AcknowledgmentDto;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

public class ExceptionHandler {
    public void handle(HttpServletResponse resp, int httpStatusCode, String message) throws IOException {
        resp.setStatus(httpStatusCode);
        resp.getWriter().write(new ObjectMapper().writeValueAsString(
                AcknowledgmentDto.builder()
                        .message(message)
                        .build()
        ));
    }

    public void handleSQLException(HttpServletResponse resp, SQLException ex) throws IOException {
        if (ex.getMessage().contains("[SQLITE_CONSTRAINT_UNIQUE]")) {
            handle(resp, HttpServletResponse.SC_CONFLICT,
                    "Violation of unique constraint. Written field already exists.");
        } else {
            handle(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }
}
