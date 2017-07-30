package com.aviv.konnek2.interfaces;

/**
 * Created by Lenovo on 19-06-2017.
 */

public interface SignInView {


    void setUserNameError();

    void setPasswordError();

    void showProgress();

    void hideProgress();

    void navigateToHome();

    void signInResponse(String str);
    void verifyOtp(String str);
    void reSendOtpResponse(String str);
}
