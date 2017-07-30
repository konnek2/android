package com.quickblox.sample.chat.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Lenovo on 23-07-2017.
 */

public class ChatSignInResponse {

    @SerializedName("Tag")
    private String tag;
    @SerializedName("qbUserId")
    private String qbUserId;
    @SerializedName("imageId")
    private String imageId;
    @SerializedName("mobileNumber")
    private String mobileNumber;
    @SerializedName("responseCode")
    private String responseCode;


    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getQbUserId() {
        return qbUserId;
    }

    public void setQbUserId(String qbUserId) {
        this.qbUserId = qbUserId;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }
}
