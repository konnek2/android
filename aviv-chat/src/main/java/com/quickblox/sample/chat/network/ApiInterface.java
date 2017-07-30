package com.quickblox.sample.chat.network;

import com.quickblox.sample.chat.models.ChatSignInResponse;
import com.quickblox.sample.chat.utils.Constant;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Lenovo on 23-07-2017.
 */

public interface ApiInterface {

    @GET(Constant.BASE_URL)
    Call<ChatSignInResponse> imageIdDownload(
            @Query("tag") String method,
            @Query("qbUserId") String qbUserId
//            @Query("imageId") String imageId

    );
}
