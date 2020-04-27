package com.github.pmlakner.leaguemealone.model.webserver;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class HttpProxyServer implements WebServer {
    HttpServer server;
    HttpHandler handler;

    /** Creates a server at localhost on any available port */
    public HttpProxyServer(HttpHandler handler) throws IOException {
        this("localhost", 0, handler);
    }

    /** Creates server at localhost on supplied port */
    public HttpProxyServer(int port, HttpHandler handler) throws IOException {
        this("localhost", port, handler);
    }

    /** Creates a server listening on hostname:port */
    public HttpProxyServer(String hostname, int port, HttpHandler handler) throws IOException {
        server = HttpServer.create(new InetSocketAddress(hostname, port), 0);
        this.handler = handler;
    }

    @Override
    public void registerRoute(String route) {
        server.createContext(route, handler);
    }

    @Override
    public String startServer() {
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        server.setExecutor(threadPoolExecutor);
        server.start();
        return "http://127.0.0.1:" + server.getAddress().getPort();
    }
}
