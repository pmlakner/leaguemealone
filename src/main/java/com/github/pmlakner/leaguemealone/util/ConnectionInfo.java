package com.github.pmlakner.leaguemealone.util;

public class ConnectionInfo {
    String host;
    int port;

    public ConnectionInfo (String host, int port) {
        this.host = host;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }
}
