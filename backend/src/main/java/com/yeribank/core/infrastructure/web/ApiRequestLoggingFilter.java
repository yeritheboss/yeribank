package com.yeribank.core.infrastructure.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class ApiRequestLoggingFilter extends OncePerRequestFilter {

  private static final Logger log = LoggerFactory.getLogger(ApiRequestLoggingFilter.class);

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    long startedAt = System.currentTimeMillis();

    try {
      filterChain.doFilter(request, response);
    } finally {
      long elapsedMs = System.currentTimeMillis() - startedAt;
      int status = response.getStatus();
      String query = request.getQueryString() == null ? "" : "?" + request.getQueryString();

      if (status >= 400) {
        log.warn(
            "{} {}{} -> {} ({} ms)",
            request.getMethod(),
            request.getRequestURI(),
            query,
            status,
            elapsedMs);
      } else {
        log.info(
            "{} {}{} -> {} ({} ms)",
            request.getMethod(),
            request.getRequestURI(),
            query,
            status,
            elapsedMs);
      }
    }
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();
    return path.startsWith("/swagger-ui")
        || path.startsWith("/v3/api-docs")
        || path.startsWith("/actuator");
  }
}
