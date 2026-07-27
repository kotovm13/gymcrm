package com.example.gymcrm.rest.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class RestLoggingFilter extends OncePerRequestFilter {
    public static final String TRANSACTION_ID = "transactionId";

    private static final Logger LOGGER = LoggerFactory.getLogger(RestLoggingFilter.class);
    private static final String TRANSACTION_ID_HEADER = "X-Transaction-Id";
    private static final String VALID_TRANSACTION_ID = "[A-Za-z0-9._-]{1,64}";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String transactionId = transactionId(request);
        MDC.put(TRANSACTION_ID, transactionId);
        response.setHeader(TRANSACTION_ID_HEADER, transactionId);

        try {
            LOGGER.info("REST call started transactionId={}, method={}, endpoint={}, query={}",
                    transactionId,
                    request.getMethod(),
                    request.getRequestURI(),
                    sanitizeQuery(request.getQueryString()));
            filterChain.doFilter(request, response);
            LOGGER.info("REST call completed transactionId={}, method={}, endpoint={}, status={}",
                    transactionId,
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus());
        } finally {
            MDC.remove(TRANSACTION_ID);
        }
    }

    private String transactionId(HttpServletRequest request) {
        String transactionId = request.getHeader(TRANSACTION_ID_HEADER);
        if (transactionId == null || !transactionId.matches(VALID_TRANSACTION_ID)) {
            return UUID.randomUUID().toString();
        }
        return transactionId;
    }

    private String sanitizeQuery(String queryString) {
        if (queryString == null) {
            return "";
        }
        return queryString.replaceAll("(?i)(password=)[^&]*", "$1***");
    }
}
