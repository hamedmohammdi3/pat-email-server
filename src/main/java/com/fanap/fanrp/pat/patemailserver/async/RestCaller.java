package com.fanap.fanrp.pat.patemailserver.async;

import java.util.Map;

public interface RestCaller {

    Response sendToAsync(Request request, byte asyncMessageType) throws Exception;

    String callPost(String url, Map<String, Object> parameters) throws Exception;

}