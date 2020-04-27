package com.github.pmlakner.leaguemealone.model.webserver;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;

public class HttpsProxyServer implements WebServer {
    private final HttpsServer server;
    private final HttpHandler handler;
    private String keystoreFileName;
    private String keystorePassword;

    /** Creates a server at localhost on any available port, expects a jks file for ksFilename */
    public HttpsProxyServer(HttpHandler handler, String ksFileName, String ksPass) throws Exception {
        this(handler, ksFileName, ksPass, 0, "localhost");
    }

    /** Creates server at localhost on supplied port */
    public HttpsProxyServer(HttpHandler handler, String ksFileName, String ksPass, int port) throws Exception {
        this(handler, ksFileName, ksPass, port, "localhost");
    }

    /** Creates a server listening on hostname:port */
    public HttpsProxyServer(HttpHandler handler, String ksFileName, String ksPass, int port, String hostname) throws Exception {
        this.handler = handler;
        server = HttpsServer.create(new InetSocketAddress(hostname, port), 0);
        configureForTLS(ksFileName, ksPass);
    }

    private void configureForTLS(String ksFileName, String ksPass) throws Exception{
        char[] keystorePassword = ksPass.toCharArray();
        SSLContext sslContext = SSLContext.getInstance("TLS");
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(new FileInputStream(ksFileName), keystorePassword);
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(keyStore, keystorePassword);
        sslContext.init(keyManagerFactory.getKeyManagers(), null, null);
        server.setHttpsConfigurator(new HttpsConfigurator(sslContext){
            public void configure(HttpsParameters params){
                try {
                    // initialise the SSL context
                    SSLContext context = getSSLContext();
                    SSLEngine engine = context.createSSLEngine();
                    params.setNeedClientAuth(false);
                    params.setCipherSuites(engine.getEnabledCipherSuites());
                    params.setProtocols(engine.getEnabledProtocols());

                    // Set the SSL parameters
                    SSLParameters sslParameters = context.getSupportedSSLParameters();
                    params.setSSLParameters(sslParameters);

                } catch (Exception ex) {
                    System.out.println("Failed to create HTTPS port");
                }
            }
        });

    }

    @Override
    public void registerRoute(String route) {
        server.createContext(route, handler);
    }

    @Override
    public String startServer() {
//        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
//        server.setExecutor(threadPoolExecutor);
        server.setExecutor(null);
        server.start();
        return "http://127.0.0.1:" + server.getAddress().getPort();
    }
}
