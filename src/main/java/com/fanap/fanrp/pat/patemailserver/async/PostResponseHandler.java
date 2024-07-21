package com.fanap.fanrp.pat.patemailserver.async;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

class PostResponseHandler implements ResponseHandler<String> {


    @Override
    public String handleResponse(HttpResponse response) throws IOException {
        String content = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        System.out.println("receive response : " + content);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == HttpStatus.SC_OK){
            return content;
        } else {
            System.out.println("error response with status code : " + statusCode);
            throw new IOException(content);
        }
    }
}