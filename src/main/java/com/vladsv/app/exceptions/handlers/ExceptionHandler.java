package com.vladsv.app.exceptions.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladsv.app.dtos.AcknowledgmentDto;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ExceptionHandler {
    public void handle(HttpServletResponse resp, int httpStatusCode, String message) throws IOException {
        resp.setStatus(httpStatusCode);
        resp.getWriter().write(new ObjectMapper().writeValueAsString(
                AcknowledgmentDto.builder()
                        .message(message)
                        .build()
        ));
    }
}
