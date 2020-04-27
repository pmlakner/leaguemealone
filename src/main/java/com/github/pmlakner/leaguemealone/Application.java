package com.github.pmlakner.leaguemealone;

import com.github.pmlakner.leaguemealone.handler.RouteHandler;
import com.github.pmlakner.leaguemealone.model.chatproxy.ChatProxy;
import com.github.pmlakner.leaguemealone.model.chatproxy.HttpsChatProxy;
import com.github.pmlakner.leaguemealone.model.webserver.HttpProxyServer;
import com.github.pmlakner.leaguemealone.service.ChatService;
import com.github.pmlakner.leaguemealone.service.ChatServiceImpl;
import com.github.pmlakner.leaguemealone.util.Status;
import com.github.pmlakner.leaguemealone.util.Utilities;
import com.sun.net.httpserver.HttpExchange;

import java.util.logging.*;

public class Application {
    private static final Logger LOGGER = Logger.getLogger(Application.class.getName());

    public static void main(String[] args) throws Exception {

        String riotClientConfigServer = "https://clientconfig.rpg.riotgames.com";
        String leagueConfig = "/Users/Shared/Riot Games/Riot Client.app/Contents/Resources/system.yaml";
        String riotConfig = "/Applications/League of Legends.app/Contents/LoL/system.yaml";
        String localhost = "http://127.0.0.1:";


        ChatProxy<HttpExchange, Status> chatProxy = new HttpsChatProxy(riotClientConfigServer);
        ChatService<HttpExchange, Status> chatService = new ChatServiceImpl<HttpExchange, Status>(chatProxy);
        RouteHandler routeHandler = new RouteHandler(chatService);
        HttpProxyServer webServer = new HttpProxyServer(routeHandler);

        webServer.registerRoute("/");
        String proxyAddress = webServer.startServer();
        LOGGER.info(String.format("Starting proxy server at %s", proxyAddress));

        Utilities.modifyFile(leagueConfig, riotClientConfigServer, proxyAddress);
        Utilities.modifyFile(riotConfig, riotClientConfigServer,  proxyAddress);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Utilities.modifyFile(leagueConfig, proxyAddress, riotClientConfigServer);
            Utilities.modifyFile(riotConfig, proxyAddress, riotClientConfigServer);
        }));
    }
}
