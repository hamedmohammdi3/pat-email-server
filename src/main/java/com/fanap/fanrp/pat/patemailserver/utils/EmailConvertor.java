package com.fanap.fanrp.pat.patemailserver.utils;

import com.fanap.fanrp.pat.patemailserver.core.db.POPEmail;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EmailConvertor {

    public static Map<String,String> resultSet = new HashMap<>();
    public static Map<String,POPEmail> resultSetEmail = new HashMap<>();

    final String EMAIL_RAW_FORMAT = "To: 0036958980 <0036958980@pat.ir>\n" +
            "From: =?UTF-8?B?2YXYs9i52YjYryDZgdix2K3Zhtin2qk=?= <2991341858@pat.ir>\n" +
            "Subject: #SUBJECT\n" +
            "Message-ID: #Message-ID\n" +
            "Date: #DATE\n" +
            "User-Agent: Mozilla/5.0 (X11; Linux x86_64; rv:78.0) Gecko/20100101\n" +
            " Thunderbird/78.11.0\n" +
            "MIME-Version: 1.0\n" +
            "Content-Type: multipart/alternative;\n" +
            " boundary=\"------------#HASH\"\n" +
            "Content-Language: en-US\n" +
            "\n" +
            "This is a multi-part message in MIME format.\n" +
            "--------------#HASH\n" +
            "Content-Type: text/plain; charset=utf-8; format=flowed\n" +
            "Content-Transfer-Encoding: 8bit\n" +
            "\n" +
            "\n" +
            "#PLAIN\n" +
            "\n" +
            "\n" +
            "--------------#HASH\n" +
            "Content-Type: text/html; charset=utf-8\n" +
            "Content-Transfer-Encoding: 8bit\n" +
            "\n" +
            "<html>\n" +
            "  <head>\n" +
            "\n" +
            "    <meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">\n" +
            "  </head>\n" +
            "  <body>\n" +
            "    #HTML\n" +
            "  </body>\n" +
            "</html>\n" +
            "\n" +
            "--------------#HASH--";

    public POPEmail toMailRaw(Map<String, Object> data){
        POPEmail popEmail = new POPEmail();
        String result = EMAIL_RAW_FORMAT;
        int popId = 1;



        for(String key : data.keySet()){
            String value = data.get(key).toString();
            switch (key){
                case "c_subject":
                    result = result.replace("#SUBJECT", value);
                    break;
                case "body":
                    result = result.replace("#PLAIN", value);
                    result = result.replace("#HTML", value);
                    break;
                case "c_inserttime":
                    result = result.replace("#DATE", "Tue, 31 Aug 2021 01:45:12 +0430");
                    break;
                case "c_letter_no":
                    result = result.replace("#Message-ID", " <"+value+"@pat.ir>");
                    break;
                case "c_id":
                    //result = result.replace("#SUBJECT", value);
                    popEmail.setPopId(Integer.parseInt(value));
                    popEmail.setDbId(Integer.parseInt(value));
                    break;
            }
            popId++;
        }
        String hash = UUID.randomUUID().toString().replaceAll("-","").substring(0,24);
        result = result.replaceAll("#HASH", hash);

        popEmail.setSize(result.length());

        resultSet.put(String.valueOf(popEmail.getPopId()), result);
        resultSetEmail.put(String.valueOf(popEmail.getPopId()), popEmail);
        return popEmail;
    }
}
