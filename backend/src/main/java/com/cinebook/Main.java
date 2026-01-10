package com.cinebook;

import com.cinebook.db.DatabaseInitializer;
import com.cinebook.server.HttpServer;

public class Main {
    public static void main(String[] args) {
        DatabaseInitializer.init();
        int port = 8080;
        HttpServer server = new HttpServer(port);
        server.start();
        System.out.println("CineBook Server started on port " + port);
    }
}
