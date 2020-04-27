package com.github.pmlakner.leaguemealone.model.chatproxy;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.pmlakner.leaguemealone.util.Status;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import static java.nio.charset.StandardCharsets.US_ASCII;

public class HttpsChatProxy implements ChatProxy <HttpExchange, Status> {

    private static final Logger LOGGER = Logger.getLogger(HttpsChatProxy.class.getName());
    private final HttpClient clientProxy;
    private final String destinationServer;

    public HttpsChatProxy(String destinationServer) {
        this.destinationServer = destinationServer;
        clientProxy = HttpClient.newHttpClient();
    }

    private CompletableFuture<Map<String,String>> sendRequest(HttpRequest request) {
        UncheckedObjectMapper objectMapper = new UncheckedObjectMapper();
        return HttpClient.newHttpClient()
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(objectMapper::readValue);
    }

    class UncheckedObjectMapper extends com.fasterxml.jackson.databind.ObjectMapper {
        /**
         * Parses the given JSON string into a Map.
         */
        Map<String, String> readValue(String content) {
            try {
                return this.readValue(content, new TypeReference<>() {
                });
            } catch (IOException ioe) {
                throw new CompletionException(ioe);
            }
        }
    }


    @Override
    //TODO: HANDLE THIS EXCEPTION AND RETURN RESPONSE WITH BAD CODE
    public HttpExchange configChatConnection(HttpExchange interceptedExchange) throws Exception {
        HttpRequest modifiedRequest = getModifiedRequest(interceptedExchange);

        LOGGER.info(String.format("Sending modified HTTP request to %s", modifiedRequest.uri().toString()));
        HttpResponse<InputStream> interceptedResponse = clientProxy.send(modifiedRequest, HttpResponse.BodyHandlers.ofInputStream());
        LOGGER.info(String.format("Response received from %s. Forwarding to local", destinationServer));

        modifyResponse(interceptedResponse, interceptedExchange);
        return interceptedExchange;
    }

    @Override
    public void updateStatus(Status newStatus) {

    }

    private HttpRequest getModifiedRequest(HttpExchange interceptedExchange) throws IOException {
        String requestMethod = interceptedExchange.getRequestMethod();
        Headers headers = interceptedExchange.getRequestHeaders();
        InputStream bodyInputStream = interceptedExchange.getRequestBody();

        System.out.println("context: " + interceptedExchange.getHttpContext().toString());
        System.out.println("protocol: " + interceptedExchange.getProtocol());
        System.out.println("requestURI: " + interceptedExchange.getRequestURI().toString());
        System.out.println("local: " + interceptedExchange.getLocalAddress().toString());
        System.out.println("remote: " + interceptedExchange.getRemoteAddress().toString());

        // Read body as stream
        /*
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(destinationServer))
                .method(requestMethod, HttpRequest.BodyPublishers.ofInputStream(() -> bodyInputStream));
        */

        // Read body as string
        String requestBody = new String(bodyInputStream.readAllBytes(), US_ASCII);
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(destinationServer + interceptedExchange.getRequestURI()))
                .method(requestMethod, HttpRequest.BodyPublishers.ofString(requestBody));

        for(Map.Entry<String, List<String>> header : headers.entrySet()) {
            for(String value : header.getValue()) {
                if (!header.getKey().equalsIgnoreCase("Host")) {
                    requestBuilder.header(header.getKey(), value);
                }
                System.out.println(header.getKey() + " : " + value);
            }
        }
        System.out.println(requestBody);
        return requestBuilder.build();
    }

    private void modifyResponse(HttpResponse<InputStream> interceptedResponse, HttpExchange interceptedExchange) throws IOException {

        Map<String, List<String>> responseHeaders = interceptedResponse.headers().map();
        for(Map.Entry<String, List<String>> header : responseHeaders.entrySet()) {
            for(String value : header.getValue()) {
                    System.out.println(header.getKey() + " : " + value);
            }
        }
//        GZIPInputStream gzipInputStream = new GZIPInputStream(interceptedResponse.body());
//        InputStreamReader isReader = new InputStreamReader(gzipInputStream);
//        BufferedReader reader = new BufferedReader(isReader);
//        StringBuffer sb = new StringBuffer();
//        String str;
//        while((str = reader.readLine())!= null){
//            sb.append(str);
//        }
//        System.out.println(sb.toString());

        int statusCode = interceptedResponse.statusCode();
        System.out.println("STATUS CODE: " + statusCode);
        interceptedExchange.getResponseHeaders().putAll(responseHeaders);
        interceptedExchange.sendResponseHeaders(statusCode, 0);
        OutputStream responseBodyStream = interceptedExchange.getResponseBody();
        interceptedResponse.body().transferTo(responseBodyStream);
        responseBodyStream.close();
//        PrintStream printStream = new PrintStream(responseBodyStream);
//        printStream.print(interceptedResponse.body());
//        printStream.close();
    }

}
