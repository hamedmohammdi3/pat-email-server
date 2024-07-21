package com.fanap.fanrp.pat.patemailserver.async;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;

public class RequestHeader implements Cloneable, Serializable {
    private String trackerId;
    private String token;   // For internal use
    private String id_token;    // For SSO. Communicate with other systems
    private String pod_token;
    private Long pod_token_expiry;
    private String ssoToken;
    private long requesterPostId;
    private Long[] userIds;   // User IDs of people whom message must be sent to
    private Long permittedUserId;
    private Integer csrf_token;

    private Long[] ssoIds;
    @JsonIgnore
    private Long peerId;    // For use between Engine and TaskManager
    @JsonIgnore
    private String peerName;    // For use between two Engine
    @JsonIgnore
    private long asyncTracker;

    public RequestHeader() {
    }

    public long getRequesterPostId() {
        return requesterPostId;
    }

    public void setRequesterPostId(long requesterPostId) {
        this.requesterPostId = requesterPostId;
    }

    public String getTrackerId() {
        return trackerId;
    }

    public void setTrackerId(String trackerId) {
        this.trackerId = trackerId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getPeerId() {
        return peerId;
    }

    public void setPeerId(Long peerId) {
        if (peerId != null && peerId == 0L) {
            this.peerId = null;
        } else {
            this.peerId = peerId;
        }
    }

    public Long[] getUserIds() {
        return userIds;
    }

    public void setUserIds(Long[] userIds) {
        this.userIds = userIds;
    }

    public long getAsyncTracker() {
        return asyncTracker;
    }

    public void setAsyncTracker(long asyncTracker) {
        this.asyncTracker = asyncTracker;
    }

    public String getId_token() {
        return id_token;
    }

    public void setId_token(String id_token) {
        this.id_token = id_token;
    }

    public Long[] getSsoIds() {
        return ssoIds;
    }

    public void setSsoIds(Long[] ssoIds) {
        this.ssoIds = ssoIds;
    }

    public String getPeerName() {
        return peerName;
    }

    public void setPeerName(String peerName) {
        this.peerName = peerName;
    }

    public String getSsoToken() {
        return ssoToken;
    }

    public void setSsoToken(String ssoToken) {
        this.ssoToken = ssoToken;
    }

    public String getPod_token() {
        return pod_token;
    }

    public void setPod_token(String pod_token) {
        this.pod_token = pod_token;
    }

    public Long getPod_token_expiry() {
        return pod_token_expiry;
    }

    public void setPod_token_expiry(Long pod_token_expiry) {
        this.pod_token_expiry = pod_token_expiry;
    }

    public Long getPermittedUserId() {
        return permittedUserId;
    }

    public void setPermittedUserId(Long permittedUserId) {
        this.permittedUserId = permittedUserId;
    }

    public Integer getCsrf_token() {
        return csrf_token;
    }

    public void setCsrf_token(Integer csrf_token) {
        this.csrf_token = csrf_token;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RequestHeader)) return false;
        RequestHeader that = (RequestHeader) o;
        return requesterPostId == that.requesterPostId &&
                asyncTracker == that.asyncTracker &&
                Objects.equals(trackerId, that.trackerId) &&
                Objects.equals(token, that.token) &&
                Objects.equals(id_token, that.id_token) &&
                Objects.equals(pod_token, that.pod_token) &&
                Objects.equals(pod_token_expiry, that.pod_token_expiry) &&
                Objects.equals(ssoToken, that.ssoToken) &&
                Arrays.equals(userIds, that.userIds) &&
                Objects.equals(permittedUserId, that.permittedUserId) &&
                Objects.equals(csrf_token, that.csrf_token) &&
                Arrays.equals(ssoIds, that.ssoIds) &&
                Objects.equals(peerId, that.peerId) &&
                Objects.equals(peerName, that.peerName);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(trackerId, token, id_token, pod_token, pod_token_expiry, ssoToken, requesterPostId, permittedUserId, csrf_token, peerId, peerName, asyncTracker);
        result = 31 * result + Arrays.hashCode(userIds);
        result = 31 * result + Arrays.hashCode(ssoIds);
        return result;
    }


    @Override
    public String toString() {
        return new StringJoiner(", ", RequestHeader.class.getSimpleName() + "{", "}")
                .add("\"trackerId\":\"" + trackerId + "\"")
                .add("\"token\":\"" + token + "\"")
                .add("\"id_token\":\"" + id_token + "\"")
                .add("\"pod_token\":\"" + pod_token + "\"")
                .add("\"pod_token_expiry\":\"" + pod_token_expiry + "\"")
                .add("\"ssoToken\":\"" + ssoToken + "\"")
                .add("\"requesterPostId\":\"" + requesterPostId + "\"")
                .add("\"userIds\":\"" + Arrays.toString(userIds) + "\"")
                .add("\"permittedUserId\":\"" + permittedUserId + "\"")
                .add("\"csrf_token\":\"" + csrf_token + "\"")
                .add("\"ssoIds\":\"" + Arrays.toString(ssoIds) + "\"")
                .add("\"peerId\":\"" + peerId + "\"")
                .add("\"peerName\":\"" + peerName + "\"")
                .add("\"asyncTracker\":\"" + asyncTracker + "\"")
                .toString();
    }
}
