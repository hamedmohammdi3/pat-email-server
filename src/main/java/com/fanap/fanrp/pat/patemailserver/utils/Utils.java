package com.fanap.fanrp.pat.patemailserver.utils;

import com.fanap.fanrp.pat.patemailserver.async.Request;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.HashMap;
import java.util.Map;

public class Utils {

    public static String findToken(String data) throws JsonProcessingException {
        Map<String,String> loginResponseMap = JsonUtil.read(data, HashMap.class);
        String loginContent = loginResponseMap.get("content");
        Request request = JsonUtil.read(loginContent, Request.class);
        return request.getContent();
    }
}
