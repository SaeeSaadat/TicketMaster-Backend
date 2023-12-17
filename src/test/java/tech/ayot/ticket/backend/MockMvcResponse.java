package tech.ayot.ticket.backend;

import jakarta.servlet.http.Cookie;

public record MockMvcResponse<T>(T body, Cookie[] cookies) {
}
