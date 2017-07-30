package com.aviv.konnek2.interfaces;

import com.aviv.konnek2.models.SignInResponse;
import com.aviv.konnek2.utils.Constant;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Lenovo on 21-06-2017.
 */

public interface SignInApiInterface {

    //    /This method is used for "POST"
    @GET(Constant.BASE_URL)
    Call<SignInResponse> post(
            @Query("tag") String method,
            @Query("name") String name,
            @Query("mobileNumber") String mobileNumber,
            @Query("country") String country
    );

    @GET(Constant.BASE_URL)
    Call<SignInResponse> imageIdUpload(
            @Query("tag") String method,
            @Query("mobileNumber") String mobileNumber,
            @Query("qbUserId") String qbUserId,
            @Query("imageId") String imageId

    );

    @GET(Constant.BASE_URL)
    Call<SignInResponse> otpVerifyPost(
            @Query("tag") String method,
            @Query("mobileNumber") String mobileNumber,
            @Query("otp") String otp

    );

    @GET(Constant.BASE_URL)
    Call<SignInResponse> otpResendPost(
            @Query("tag") String method,
            @Query("mobileNumber") String mobileNumber


    );

    @GET(Constant.BASE_URL)
    Call<SignInResponse> getImageId(
            @Query("tag") String method,
            @Query("qbUserId") String qbUserId


    );

    //This method is used for "GET"
    @GET(Constant.BASE_URL)
    Call<SignInResponse> postProfile(
            @Query("tag") String method,
            @Query("name") String name,
            @Query("mobileNumber") String mobileNumber,
            @Query("email") String email,
            @Query("dateOfBirth") String dateOfBirth,
            @Query("gender") String gender,
            @Query("city") String city,
            @Query("country") String country,
            @Query("zipCode") String zipCode
    );
}
