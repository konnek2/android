package com.aviv.konnek2;

import android.content.Intent;
import android.util.Log;

import com.aviv.konnek2.data.network.ApiClient;
import com.aviv.konnek2.interfaces.ProfileView;
import com.aviv.konnek2.interfaces.SignInApiInterface;
import com.aviv.konnek2.interfaces.profilePresenter;
import com.aviv.konnek2.models.SignInResponse;
import com.aviv.konnek2.models.UserModel;
import com.aviv.konnek2.ui.activity.HomeActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Lenovo on 28-06-2017.
 */

public class AppProfilePresenter implements profilePresenter {

    public static final String TAG = AppSigInPresenter.class.getSimpleName();

    private ProfileView profileView;

    public AppProfilePresenter(ProfileView profileView) {
        this.profileView = profileView;

    }

    @Override
    public void validateProfile(String tag, String name, String mobile, String email,
                                String dataOfBirth, String gender, String city, String country, String zipCode) {

        try {
            SignInApiInterface service = ApiClient.getClient().create(SignInApiInterface.class);
            Call<SignInResponse> call = service.postProfile(tag, name, mobile, email,
                    dataOfBirth, gender, city, country, zipCode);
            call.enqueue(new Callback<SignInResponse>() {
                @Override
                public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                    try {

                        Log.d("APPLogin", " onResponse validateProfile getName "+response.body().getName());
                        Log.d("APPLogin", " onResponse validateProfile getCity "+response.body().getCity());
                        Log.d("APPLogin", " onResponse validateProfile getMobileNumber "+response.body().getMobileNumber());
                        Log.d("APPLogin", " onResponse validateProfile getDateOfBirth "+response.body().getDateOfBirth());
                        Log.d("APPLogin", " onResponse validateProfile getZipCode "+response.body().getZipCode());
                        Log.d("APPLogin", " onResponse validateProfile getGender "+response.body().getGender());
                        UserModel userModel = new UserModel();
                        if (response.body().getUserId() != null) {
                            userModel.setUserId(response.body().getUserId());
                        }
                        if (response.body().getName() != null) {
                            userModel.setName(response.body().getName());
                        } else {
                            userModel.setName("");
                        }
                        if (response.body().getMobileNumber() != null) {
                            userModel.setMobileNumber(response.body().getMobileNumber());
                        } else {
                            userModel.setMobileNumber("");
                        }
                        if (response.body().getEmail() != null) {
                            userModel.setEmail(response.body().getEmail());
                        } else {
                            userModel.setEmail("");
                        }
                        if (response.body().getDateOfBirth() != null) {
                            userModel.setDateOfBirth(response.body().getDateOfBirth());
                        } else {
                            userModel.setDateOfBirth("");
                        }
                        if (response.body().getGender() != null) {
                            userModel.setGender(response.body().getGender());
                        } else {
                            userModel.setGender("");
                        }
                        if (response.body().getZipCode() != null) {
                            userModel.setZipCode(response.body().getZipCode());
                        } else {
                            userModel.setZipCode("");
                        }
                        if (response.body().getCreatedAt() != null) {
                            userModel.setCreatedAt(response.body().getCreatedAt());
                        } else {
                            userModel.setCreatedAt("");
                        }
                        if (response.body().getUpdatedAt() != null) {
                            userModel.setUpdatedAt(response.body().getUpdatedAt());
                        } else {
                            userModel.setUpdatedAt("");
                        }
                        Konnnek2.usersTableDAO.insertuserDetails(userModel);
                        Konnnek2.tableBackUpManagerDAO.databaseDB(Konnnek2.getAppContext());
                        Intent profileSave = new Intent(Konnnek2.getAppContext(), HomeActivity.class);
                        Konnnek2.getAppContext().startActivity(profileSave);

                    } catch (Exception e) {
                        e.getMessage();
                    }
                }

                @Override
                public void onFailure(Call<SignInResponse> call, Throwable t) {
                    Log.d("APPLogin", " onResponse validateProfile onFailure");
                }

            });


        } catch (Exception e) {
            e.getMessage();
        }

    }
}
