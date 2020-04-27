package com.github.pmlakner.leaguemealone.handler;

import com.github.pmlakner.leaguemealone.service.ChatService;
import com.github.pmlakner.leaguemealone.util.Status;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

public class RouteHandler implements HttpHandler {
    private static final Logger LOGGER = Logger.getLogger(RouteHandler.class.getName());
    private ChatService<HttpExchange, Status> chatService;

    public RouteHandler(ChatService<HttpExchange, Status> chatService) {
        this.chatService = chatService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String method = exchange.getRequestMethod();
        String requesterAddress = exchange.getRemoteAddress().toString();
        LOGGER.info(String.format("Received %s request from %s", method, requesterAddress));

        InputStream is = exchange.getRequestBody();
        if (method.equalsIgnoreCase("GET")) {
            try {
                chatService.configChatConnection(exchange);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
