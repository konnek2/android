package com.quickblox.sample.groupchatwebrtc.model;

/**
 * Created by Lenovo on 28-06-2017.
 */

public class CallLogModel {

    private String callOpponentName;
    private String callUserName;
    private String callTime;
    private String callStatus;
    private String callDate;
    private String callPriority;
    private String callType;
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCallOpponentName() {
        return callOpponentName;
    }

    public void setCallOpponentName(String callOpponentName) {
        this.callOpponentName = callOpponentName;
    }

    public String getCallUserName() {
        return callUserName;
    }

    public void setCallUserName(String callUserName) {
        this.callUserName = callUserName;
    }

    public String getCallTime() {
        return callTime;
    }

    public void setCallTime(String callTime) {
        this.callTime = callTime;
    }

    public String getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(String callStatus) {
        this.callStatus = callStatus;
    }

    public String getCallDate() {
        return callDate;
    }

    public void setCallDate(String callDate) {
        this.callDate = callDate;
    }

    public String getCallPriority() {
        return callPriority;
    }

    public void setCallPriority(String callPriority) {
        this.callPriority = callPriority;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }
}
