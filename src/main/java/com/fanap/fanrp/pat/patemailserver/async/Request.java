package com.fanap.fanrp.pat.patemailserver.async;

import com.fanap.fanrp.pat.patemailserver.enums.MessageType;
import com.fanap.fanrp.pat.patemailserver.vo.ProcessVo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mehrdad Dehnamaki
 */

public class Request implements Serializable {
    private RequestHeader requestHeader;
    private MessageType messageType;
    private List<ProcessVo> processData = new ArrayList<>();
    private String content;     // can be LoginInfo, PasswordVo, UserInfo, processInstanceId, FilteringOptions or Text message
    private String engineName;
    private Long peerId;
    private long asyncTracker;

    private String trackerId;


    public Request(MessageType messageType, String processName, Long processInstanceId, String taskId, Long threadIndex) {
        this.messageType = messageType;
        ProcessVo processData = new ProcessVo();
        processData.setProcessInstanceId(processInstanceId);
        processData.setProcessCode(processName);
        processData.setNodeId(taskId);
        processData.setThreadIndex(threadIndex);
        this.processData.add(processData);

    }

    public Request() {
    }

    public Request(MessageType messageType, ProcessVo processData) {
        this.messageType = messageType;
        this.processData.add(processData);
    }

    public Request(MessageType messageType, String textMessage) {
        this.messageType = messageType;
        this.content = textMessage;
    }

    public Request(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getTrackerId() {
        return trackerId;
    }

    public void setTrackerId(String trackerId) {
        this.trackerId = trackerId;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public List<ProcessVo> getProcessData() {
        return processData;
    }

    public void setProcessData(List<ProcessVo> processData) {
        this.processData = processData;
    }

    public RequestHeader getRequestHeader() {
        return requestHeader;
    }

    public void setRequestHeader(RequestHeader requestHeader) {
        this.requestHeader = requestHeader;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEngineName() {
        return engineName;
    }

    public void setEngineName(String engineName) {
        this.engineName = engineName;
    }

    public Long getPeerId() {
        return peerId;
    }

    public void setPeerId(Long peerId) {
        this.peerId = peerId;
    }

    public long getAsyncTracker() {
        return asyncTracker;
    }

    public void setAsyncTracker(long asyncTracker) {
        this.asyncTracker = asyncTracker;
    }

}