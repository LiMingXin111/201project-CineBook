package com.cinebook.server;

import com.cinebook.controller.AuthController;
import com.cinebook.controller.MovieController;
import com.cinebook.controller.OrderController;
import com.cinebook.controller.ReportController;
import com.cinebook.util.JsonUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class HttpServer {
    private final int port;
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;

    private final AuthController authController = new AuthController();
    private final MovieController movieController = new MovieController();
    private final OrderController orderController = new OrderController();
    private final ReportController reportController = new ReportController();

    public HttpServer(int port) {
        this.port = port;
    }

    public void start() {
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("Server listening on port " + port);

            while (true) {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    if (key.isAcceptable()) {
                        handleAccept(key);
                    } else if (key.isReadable()) {
                        handleRead(key);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverChannel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
        System.out.println("New client connected: " + clientChannel.getRemoteAddress());
    }

    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ByteBuffer buffer = ByteBuffer.allocate(8192);

        int bytesRead = clientChannel.read(buffer);
        if (bytesRead == -1) {
            clientChannel.close();
            return;
        }

        while (bytesRead > 0) {
            buffer.flip();
            output.write(buffer.array(), 0, bytesRead);
            buffer.clear();
            bytesRead = clientChannel.read(buffer);
        }

        String requestData = new String(output.toByteArray(), StandardCharsets.UTF_8);
        if (requestData.trim().isEmpty()) {
            clientChannel.close();
            return;
        }

        Request request = parseRequest(requestData);
        Response response = handleRequest(request);
        addCorsHeaders(response);

        sendResponse(clientChannel, response);
        clientChannel.close();
    }

    private Request parseRequest(String requestData) {
        String[] lines = requestData.split("\r\n");
        String[] firstLine = lines[0].split(" ");

        String method = firstLine[0];
        String rawPath = firstLine[1];
        String protocol = firstLine.length > 2 ? firstLine[2] : "HTTP/1.1";

        String path = rawPath;
        String queryString = null;
        int queryIndex = rawPath.indexOf('?');
        if (queryIndex >= 0) {
            path = rawPath.substring(0, queryIndex);
            if (queryIndex + 1 < rawPath.length()) {
                queryString = rawPath.substring(queryIndex + 1);
            }
        }

        Request request = new Request(method, path, protocol);
        if (queryString != null && !queryString.isEmpty()) {
            parseQueryParams(request, queryString);
        }

        int bodyStart = requestData.indexOf("\r\n\r\n");
        if (bodyStart != -1) {
            String headersPart = requestData.substring(0, bodyStart);
            String body = requestData.substring(bodyStart + 4);

            String[] headerLines = headersPart.split("\r\n");
            for (int i = 1; i < headerLines.length; i++) {
                String[] headerParts = headerLines[i].split(": ", 2);
                if (headerParts.length == 2) {
                    request.addHeader(headerParts[0], headerParts[1]);
                }
            }

            if (request.getHeader("Content-Type") != null &&
                request.getHeader("Content-Type").contains("application/json")) {
                request.setBody(body);
            } else if (!body.trim().isEmpty()) {
                request.setBody(body);
            }
        }

        return request;
    }

    private void parseQueryParams(Request request, String queryString) {
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            if (pair.trim().isEmpty()) {
                continue;
            }
            String[] parts = pair.split("=", 2);
            String key = urlDecode(parts[0]);
            String value = parts.length > 1 ? urlDecode(parts[1]) : "";
            request.addQueryParam(key, value);
        }
    }

    private String urlDecode(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            return value;
        }
    }

    private Response handleRequest(Request request) {
        try {
            if ("OPTIONS".equals(request.getMethod())) {
                return new Response(204, "text/plain", null);
            }
            if (request.getPath().startsWith("/api/auth")) {
                return authController.handleRequest(request);
            } else if (request.getPath().startsWith("/api/movies")) {
                return movieController.handleRequest(request);
            } else if (request.getPath().startsWith("/api/orders")) {
                return orderController.handleRequest(request);
            } else if (request.getPath().startsWith("/api/reports")) {
                return reportController.handleRequest(request);
            } else {
                return new Response(404, "text/plain", "Not Found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(500, "application/json", JsonUtil.toJson(
                new ErrorResponse("Internal Server Error", e.getMessage())
            ));
        }
    }

    private void addCorsHeaders(Response response) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type, Admin");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
    }

    private void sendResponse(SocketChannel clientChannel, Response response) throws IOException {
        String responseStr = response.toString();
        ByteBuffer buffer = ByteBuffer.wrap(responseStr.getBytes(StandardCharsets.UTF_8));
        clientChannel.write(buffer);
    }

    private static class ErrorResponse {
        public String error;
        public String message;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }
    }
}
