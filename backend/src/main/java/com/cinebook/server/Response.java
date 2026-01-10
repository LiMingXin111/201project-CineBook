package com.cinebook.server;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class Response {
    private final int statusCode;
    private final String contentType;
    private final String body;
    private final Map<String, String> headers = new LinkedHashMap<>();

    public Response(int statusCode, String contentType, String body) {
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.body = body;
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("HTTP/1.1 ")
               .append(statusCode)
               .append(" ")
               .append(getStatusText(statusCode))
               .append("\r\n");
        if (contentType != null) {
            builder.append("Content-Type: ").append(contentType).append("\r\n");
        }
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }
        int length = body == null ? 0 : body.getBytes(StandardCharsets.UTF_8).length;
        builder.append("Content-Length: ").append(length).append("\r\n");
        builder.append("Connection: close\r\n");
        builder.append("\r\n");
        if (body != null) {
            builder.append(body);
        }
        return builder.toString();
    }

    private String getStatusText(int code) {
        switch (code) {
            case 200:
                return "OK";
            case 201:
                return "Created";
            case 204:
                return "No Content";
            case 400:
                return "Bad Request";
            case 401:
                return "Unauthorized";
            case 403:
                return "Forbidden";
            case 404:
                return "Not Found";
            case 500:
                return "Internal Server Error";
            case 501:
                return "Not Implemented";
            default:
                return "OK";
        }
    }
}
