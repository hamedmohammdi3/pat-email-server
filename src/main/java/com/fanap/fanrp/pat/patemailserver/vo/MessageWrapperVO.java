package com.fanap.fanrp.pat.patemailserver.vo;

public class MessageWrapperVO {

    private byte type;
    private String content;
    private long trackerId;

    public byte getType() {
        return type;
    }

    public void setType(byte type) {

        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTrackerId() {
        return trackerId;
    }

    public void setTrackerId(long trackerId) {
        this.trackerId = trackerId;
    }
}