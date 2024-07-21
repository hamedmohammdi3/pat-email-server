package com.fanap.fanrp.pat.patemailserver.async;

public class Response {

    private String content;
    private boolean hasError;

    public Response() {
    }

    public Response(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean getHasError() {
        return hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }
}
