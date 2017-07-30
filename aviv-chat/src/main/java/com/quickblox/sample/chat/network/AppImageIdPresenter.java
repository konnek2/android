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


        Log.d("AppImageIdDOWN ", "validateImageId  qbUserId " + qbUserId);
        ApiInterface service = ChatApiClient.getClient().create(ApiInterface.class);
        Call<ChatSignInResponse> call = service.imageIdDownload(Constant.TAG_IMAGE_ID_DOWNLOAD, qbUserId);
        call.enqueue(new Callback<ChatSignInResponse>() {
            @Override
            public void onResponse(Call<ChatSignInResponse> call, Response<ChatSignInResponse> response) {
                Log.d(" AppImageIdDOWN ", " AppImageId  ====> onResponse" + response.body().getResponseCode());
                if (response.body().getResponseCode() != null) {
                    Log.d(" AppImageIdDOWN ", " AppImageId   onResponse != null getImageId ====> " + response.body().getImageId());
                    Log.d(" AppImageIdDOWN ", " AppImageId   onResponse != null getQbUserId ====> " + response.body().getQbUserId());

                    String imageId=response.body().getImageId();
                    Log.d("IMAGEID_CHECK", "ImageId : " +imageId);

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
