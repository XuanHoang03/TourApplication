package com.hashmal.tourapplication.service;

import com.hashmal.tourapplication.service.dto.BaseResponse;
import com.hashmal.tourapplication.service.dto.RegisterUserDTO;
import com.hashmal.tourapplication.service.dto.TourResponseDTO;
import com.hashmal.tourapplication.service.dto.TourScheduleResponseDTO;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
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

    @GET("/api/v1/tours")
    Call<List<TourResponseDTO>> getAllTours();

    @GET("/api/v1/tours/{tourId}/schedules")
    Call<List<TourScheduleResponseDTO>> getTourSchedulesByTourId(@Path("tourId") String tourId);
}
