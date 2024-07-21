package com.fanap.fanrp.pat.patemailserver.service;

import com.fanap.fanrp.pat.patemailserver.utils.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ServerListener  implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        if (httpExchange.getRequestMethod().equals("POST")) {

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody(), StandardCharsets.UTF_8));
            StringBuilder jsonData = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                jsonData.append(line);
            }
            Object openOffice = JsonUtil.getMapper().readValue(jsonData.toString(), Object.class);

        }
    }
}
