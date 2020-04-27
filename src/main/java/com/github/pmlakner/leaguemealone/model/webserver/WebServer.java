package com.github.pmlakner.leaguemealone.model.webserver;

public interface WebServer {
    public void registerRoute(String route);

    /** Returns address of webserver */
    public String startServer();
}
