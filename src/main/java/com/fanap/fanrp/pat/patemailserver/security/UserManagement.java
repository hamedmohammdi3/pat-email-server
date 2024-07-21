package com.fanap.fanrp.pat.patemailserver.security;

import com.fanap.fanrp.pat.patemailserver.async.AsyncMessageSender;
import com.fanap.fanrp.pat.patemailserver.utils.Utils;

import java.util.HashMap;
import java.util.Map;

public class UserManagement {

    private static Map<String,String> USER_TOKEN_MAP = new HashMap<>();

    public static User getTokenFromEngine(String userName){
        String engineName = "tfa.ow";

        try {

            String result = AsyncMessageSender.loginRequest(userName.replaceAll("@", ":"), engineName);
            System.out.println(" RESULT ## " + result);
            String token = Utils.findToken(result);
            if (token == null || token.trim().isEmpty()){
                return null;
            }
            USER_TOKEN_MAP.put(userName, token);
            User user = new User();
            user.username = userName;
            user.token = token;
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean findToken(String userName){
        return USER_TOKEN_MAP.containsKey(userName);
    }

    public static String getToken(String userName){
        return USER_TOKEN_MAP.get(userName);
    }
}
