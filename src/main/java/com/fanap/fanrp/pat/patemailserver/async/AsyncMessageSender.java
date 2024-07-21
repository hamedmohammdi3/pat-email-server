package com.fanap.fanrp.pat.patemailserver.async;

import com.fanap.fanrp.pat.patemailserver.enums.AsyncMessageType;
import com.fanap.fanrp.pat.patemailserver.enums.MessageType;
import com.fanap.fanrp.pat.patemailserver.utils.JsonUtil;
import com.fanap.fanrp.pat.patemailserver.vo.ProcessVo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AsyncMessageSender {

    public static String loginRequest(String userName,String engineName) throws Exception {
        RequestHeader requestHeader = new RequestHeader();

        Request request = new Request();
        request.setRequestHeader(requestHeader);
        request.setMessageType(MessageType.CUSTOME_LOGIN);

        request.setEngineName("tfa.ow");
        request.setContent(userName);
        RestCallerImpl restCaller = new RestCallerImpl(engineName);
        Response response = restCaller.sendToAsync(request, AsyncMessageType.MESSAGE);
        return response.getContent();
    }

    public static <T> String bpCallRequest(T objToSend,String processCode, String nodeId, String engineName, String token) throws Exception {
        RequestHeader requestHeader = new RequestHeader();


        Request request = new Request();
        request.setRequestHeader(requestHeader);
        request.setMessageType(MessageType.INIT_PROCESS);
        ProcessVo processVo = new ProcessVo();
        processVo.setProcessCode(processCode);
        processVo.setNodeId(nodeId);

        processVo.setDataContent(JsonUtil.write(objToSend));

        processVo.setProcessTrackerId(UUID.randomUUID().toString());
        processVo.setEngineName(engineName);
        List<ProcessVo> processVos = new ArrayList<>();
        processVos.add(processVo);
        request.setProcessData(processVos);
        request.setEngineName(engineName);
        RestCallerImpl restCaller = new RestCallerImpl(engineName, token);
        Response response = restCaller.sendToAsync(request, AsyncMessageType.MESSAGE);
        return response.getContent();
    }
}
