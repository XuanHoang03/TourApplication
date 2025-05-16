package com.hashmal.tourapplication.service;

import com.hashmal.tourapplication.service.dto.BaseResponse;
import com.hashmal.tourapplication.service.dto.RegisterUserDTO;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @POST("user/login")
    Call<BaseResponse> login(@Body Map<String, String> body);

    @POST("user/forgot-password")
    Call<BaseResponse> forgotPassword(@Body Map<String, String> body);

    @POST("user/register")
    Call<BaseResponse> registerUser(@Body Map<String, Object> request);
    @POST("user/verify-code")
    Call<BaseResponse> verifyCode(
            @Query("code") String code,
            @Query("userId") String userId
    );
    @POST("user/send-otp")
    Call<BaseResponse> sendOtpViaEmail(
            @Query("phoneNumber") String phoneNumber,
            @Query("type") String type
    );
}
