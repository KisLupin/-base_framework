package com.backend.config.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RequestLogger extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestLogger.class);
    private static final int MAX_LENGTH = 100000;

    private String getContentAsString(byte[] buf, String charsetName) {
        if (buf == null || buf.length == 0)
            return "";
        int length = Math.min(buf.length, MAX_LENGTH);
        try {
            return new String(buf, 0, length, charsetName);
        } catch (UnsupportedEncodingException ex) {
            return "Unsupported Encoding";
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        StringBuilder reqInfo = new StringBuilder().append("[").append(startTime % 10000).append("] ")
                .append(request.getMethod()).append(" ").append(request.getRequestURL());

        String queryString = request.getQueryString();
        if (queryString != null) {
            reqInfo.append("?").append(queryString);
        }

        if (request.getAuthType() != null) {
            reqInfo.append(", authType=").append(request.getAuthType());
        }
        if (request.getUserPrincipal() != null) {
            reqInfo.append(", principalName=").append(request.getUserPrincipal().getName());
        }

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        filterChain.doFilter(wrappedRequest, wrappedResponse);
        long duration = System.currentTimeMillis() - startTime;

        StringBuilder logResq =
                new StringBuilder(wrappedRequest.getRemoteAddr() + " Request => " + reqInfo + "\n");

        StringBuilder params = new StringBuilder("HEADER: ");
        Map<String, String> mheader = getHeaders(request);
        for (String key : mheader.keySet()) {
            params.append(key).append("=").append(mheader.get(key)).append("; ");
        }

        logResq.append(params).append("\nPARAMS: ");
        for (String key : wrappedRequest.getParameterMap().keySet()) {
            params = new StringBuilder();
            for (String value : wrappedRequest.getParameterMap().get(key)) {
                params.append(value).append(",");
            }
            logResq.append(key).append("=").append(params).append("; ");
        }

        String requestBody = this.getContentAsString(wrappedRequest.getContentAsByteArray(),
                request.getCharacterEncoding());
        if (requestBody.length() > 0) {
            logResq.append("Request body:\n").append(requestBody);
        }
        LOGGER.info(logResq.toString());

        StringBuilder logRes = new StringBuilder();
        logRes.append(wrappedRequest.getRemoteAddr()).append(" Response <= ").append(reqInfo)
                .append(": returned status=").append(response.getStatus()).append(" in ").append(duration)
                .append("ms\n");
        byte[] buf = wrappedResponse.getContentAsByteArray();
        logRes.append("Response body:\n").append(getContentAsString(buf, "UTF-8"));

        LOGGER.info(logRes.toString());
        wrappedResponse.copyBodyToResponse();
    }

    public static Map<String, String> getHeaders(HttpServletRequest r) {
        Map<String, String> result = new HashMap<>();
        Enumeration<?> names = r.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            String value = r.getHeader(name);
            result.put(name, value);
        }
        return result;
    }

}
