package com.aviv.konnek2;

import android.util.Log;

import com.aviv.konnek2.data.network.ApiClient;
import com.aviv.konnek2.interfaces.ImageIdPresenter;
import com.aviv.konnek2.interfaces.ImageIdView;
import com.aviv.konnek2.interfaces.SignInApiInterface;
import com.aviv.konnek2.models.SignInResponse;
import com.aviv.konnek2.models.UserModel;
import com.aviv.konnek2.utils.Constant;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Lenovo on 23-07-2017.
 */

public class AppImageIdPresenter implements ImageIdPresenter {


    private ImageIdView imageIdView;
    private UserModel userModel;

    public AppImageIdPresenter(ImageIdView imageIdView) {
        this.imageIdView = imageIdView;

    }

    @Override
    public void validateImageIdUpload(String mobileNumber, String qbUserId, String imageId) {


        SignInApiInterface service = ApiClient.getClient().create(SignInApiInterface.class);
        Call<SignInResponse> call = service.imageIdUpload(Constant.TAG_IMAGE_ID, mobileNumber, qbUserId, imageId);
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {

                if (response.body().getResponseCode() != null) {

//                    signInView.verifyOtp(response.body().getResponseCode());
                }

            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {

            }
        });


    }
}
