package com.fanap.fanrp.pat.patemailserver.service;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class RestApiService {

    private final String BASE_URL = "/api/";
    private final String AUTHORIZE_URL = "authorize/";

    private static RestApiService instance;

    private RestApiService(){}

    public static RestApiService getInstance()
    {
        if (instance == null)
        {
            synchronized (RestApiService.class)
            {
                if(instance==null)
                {
                    instance = new RestApiService();
                }
            }
        }
        return instance;
    }

    public void runServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8086), 0);
        server.createContext( BASE_URL.concat(AUTHORIZE_URL).concat("register"), new ServerListener());
        server.setExecutor(null);
        server.start();
        System.out.println("Ready to receive  REST API requests on port 8086...");
    }
}
