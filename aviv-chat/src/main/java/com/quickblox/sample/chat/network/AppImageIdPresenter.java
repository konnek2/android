package com.quickblox.sample.chat.network;

import android.util.Log;

import com.quickblox.sample.chat.interfaces.ImageIdDownloadView;
import com.quickblox.sample.chat.interfaces.ImageIdPresenter;
import com.quickblox.sample.chat.models.ChatSignInResponse;
import com.quickblox.sample.chat.utils.Constant;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Lenovo on 23-07-2017.
 */

public class AppImageIdPresenter implements ImageIdPresenter {

    ImageIdDownloadView imageIdDownloadView;

    public AppImageIdPresenter(ImageIdDownloadView imageIdDownloadView)

    {
        this.imageIdDownloadView = imageIdDownloadView;

    }

    @Override
    public void validateImageId( final String qbUserId) {



        ApiInterface service = ChatApiClient.getClient().create(ApiInterface.class);
        Call<ChatSignInResponse> call = service.imageIdDownload(Constant.TAG_IMAGE_ID_DOWNLOAD, qbUserId);
        call.enqueue(new Callback<ChatSignInResponse>() {
            @Override
            public void onResponse(Call<ChatSignInResponse> call, Response<ChatSignInResponse> response) {

                if (response.body().getResponseCode() != null) {

                    String imageId=response.body().getImageId();

                    if (imageId!= null) {
                        imageIdDownloadView.imageIdCallBack(qbUserId,imageId);
                    } else {

                    }

                }
            }


            @Override
            public void onFailure(Call<ChatSignInResponse> call, Throwable t) {
                Log.d(" AppImageIdDOWN ", "  AppImageId Failure  " + t.getMessage());

            }
        });

    }
}
