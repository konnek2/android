package com.aviv.konnek2;

import android.util.Log;

import com.aviv.konnek2.data.network.ApiClient;
import com.aviv.konnek2.data.preference.AppPreference;
import com.aviv.konnek2.interfaces.SignInApiInterface;
import com.aviv.konnek2.interfaces.SignInPresenter;
import com.aviv.konnek2.interfaces.SignInView;
import com.aviv.konnek2.models.SignInResponse;
import com.aviv.konnek2.models.UserModel;
import com.aviv.konnek2.utils.Common;
import com.aviv.konnek2.utils.Constant;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Lenovo on 19-06-2017.
 */

public class AppSigInPresenter implements SignInPresenter {

    public static final String TAG = AppSigInPresenter.class.getSimpleName();

    private SignInView signInView;
    private UserModel userModel;

    public AppSigInPresenter(SignInView signInView) {
        this.signInView = signInView;

    }

    @Override
    public void validateLoginCredentials(String userName, String mobileNumber, String countryCode) {
        userModel = new UserModel();
        Log.d("APPLOIGN123", "validateLoginCredentials " + userName);
        Log.d("APPLOIGN123", "validateLoginCredentials  " + mobileNumber);
        Log.d("APPLOIGN123", "validateLoginCredentials  " + countryCode);

        SignInApiInterface service = ApiClient.getClient().create(SignInApiInterface.class);
        Call<SignInResponse> call = service.post(Constant.TAG_LOGIN, userName, mobileNumber, countryCode);
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {

                try {
                    Log.d("APPLOIGN123", " onResponse validateLoginCredentials Success");
                    userModel = new UserModel();
                    if (response.body().getResponseCode().equals(Constant.RESPONSE_CODE) && response.body().getName() != null
                            && response.body().getMobileNumber() != null && response.body().getCountry() != null) {

                        Log.d("APPLOIGN123", "loginProcess  getUserId " + response.body().getUserId());
                        Log.d("APPLOIGN123", "loginProcess  getName " + response.body().getName());
                        Log.d("APPLOIGN123", "loginProcess getMobileNumber " + response.body().getMobileNumber());
                        Log.d("APPLOIGN123", "loginProcess  getCountry " + response.body().getCountry());
                        AppPreference.putUserId(response.body().getUserId());
                        AppPreference.putUserName(response.body().getName());
                        AppPreference.putMobileNumber(response.body().getMobileNumber());
                        AppPreference.putCountry(response.body().getCountry());
                        AppPreference.putLoginStatus(true);
                        userModel.setName(response.body().getName());
                        userModel.setMobileNumber(response.body().getMobileNumber());
                        userModel.setUserId(response.body().getUserId());
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
                        Common.displayToast("SignIn Process Success");
                        signInView.signInResponse(Constant.RESPONSE_CODE);

                    } else {

                        Log.d(TAG, "Invalid Param ");

                        Common.displayToast("Invalid Param");
                    }

                } catch (Exception e) {
                    e.getMessage();
                }
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                Log.d("APPLOIGN123", " onResponse validateLoginCredentials onFailure" + t.getMessage());
            }
        });
    }

    @Override
    public void validateOtp(String mobileNumber,String otp) {

        SignInApiInterface service = ApiClient.getClient().create(SignInApiInterface.class);
        Call<SignInResponse> call = service.otpVerifyPost(Constant.TAG_VERIFY_OTP, mobileNumber,otp);
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                Log.d("APPLOIGN123 ", " APPSignInPresenter validateOtp ====> " + response.body().getResponseCode());
                if(response.body().getResponseCode()!=null)
                {
                    Log.d("APPLOIGN123 ", " APPSignInPresenter getResponseCode()!=null ====> " + response.body().getResponseCode());
                    signInView.verifyOtp(response.body().getResponseCode());
                }

            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                Log.d("APPLOIGN123 ", " validateOtp onFailure t.getMessage() " + t.getMessage());

            }
        });




    }

    @Override
    public void reSendOtp(String mobileNumber) {

        SignInApiInterface service = ApiClient.getClient().create(SignInApiInterface.class);
        Call<SignInResponse> call = service.otpResendPost(Constant.TAG_RESEND_OTP, mobileNumber);
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                Log.d("APPLOIGN123 ", " APPSignInPresenter reSendOtp ====> " + response.body().getResponseCode());
                signInView.reSendOtpResponse(Constant.RESPONSE_CODE);
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {

            }
        });

    }
}
