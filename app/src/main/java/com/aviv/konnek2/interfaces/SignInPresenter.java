package com.aviv.konnek2.interfaces;

/**
 * Created by Lenovo on 19-06-2017.
 */

public interface SignInPresenter {

    void validateLoginCredentials(String userName, String mobileNumber, String countryCode);

    void validateOtp(String mobileNumber,String otp);

    void reSendOtp(String mobileNumber);
}
