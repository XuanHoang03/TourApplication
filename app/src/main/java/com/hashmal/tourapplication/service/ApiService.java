package com.hashmal.tourapplication.service;

import com.hashmal.tourapplication.service.dto.BaseResponse;
import com.hashmal.tourapplication.service.dto.CreateBookingRequest;
import com.hashmal.tourapplication.service.dto.CreateSystemUserRequest;
import com.hashmal.tourapplication.service.dto.CreateTourRequest;
import com.hashmal.tourapplication.service.dto.PaymentRequest;
import com.hashmal.tourapplication.service.dto.PaymentResponse;
import com.hashmal.tourapplication.service.dto.StatisticDTO;
import com.hashmal.tourapplication.service.dto.TourResponseDTO;
import com.hashmal.tourapplication.service.dto.TourScheduleResponseDTO;
import com.hashmal.tourapplication.service.dto.UpdateProfileRequest;
import com.hashmal.tourapplication.service.dto.UpdateUserByAdminRequest;
import com.hashmal.tourapplication.service.dto.UserManagementDTO;
import com.hashmal.tourapplication.service.dto.YourTourDTO;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("user/login")
    Call<BaseResponse> login(@Body Map<String, String> body);

    @POST("user/sys-user-login")
    Call<BaseResponse> sysLogin(@Body Map<String, String> body);

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

    @POST("/api/v1/tours/bookings")
    Call<BaseResponse> createBooking(@Body CreateBookingRequest request);

    @PUT("/api/v1/tours/bookings/{bookingId}/payment-status")
    Call<BaseResponse> updatePaymentStatus(
            @Path("bookingId") Long bookingId,
            @Query("paymentStatus") Integer paymentStatus);

    @GET("/api/v1/tours/{tourId}/schedules")
    Call<List<TourScheduleResponseDTO>> getTourSchedulesByTourId(@Path("tourId") String tourId);

    @POST("api/payment/create-payment")
    Call<PaymentResponse> createPayment(@Body PaymentRequest paymentRequest);

    @GET("/api/v1/tours/bookings/user/{userId}")
    Call<BaseResponse> getBookingsByUserId(@Path("userId") String userId);

    @GET("/api/v1/tours/your-tour")
    Call<YourTourDTO> getYourTour(
            @Query("bookingId") Long bookingId
    );

    @PUT("user/update")
    Call<BaseResponse> updateProfile(
            @Body UpdateProfileRequest request
            );

    @Multipart
    @POST("user/upload-avatar")
    Call<BaseResponse> uploadAvatar(
            @Part MultipartBody.Part file,
            @Part("profileId") RequestBody profileId
    );

    @GET("system/admin/get-statistics")
    Call<StatisticDTO> getStatistics();

    @GET("system/admin/user-management")
    Call<UserManagementDTO> getUserManagement();

    @PUT("system/admin/user-management/update")
    Call<BaseResponse> updateUserByAdmin(@Body UpdateUserByAdminRequest request) ;

    @GET("system/admin/staff-management")
    Call<UserManagementDTO> getStaffManagement();

    @PUT("system/admin/staff-management/update")
    Call<BaseResponse> updateStaffByAdmin(@Body UpdateUserByAdminRequest request) ;

    @POST("system/admin/create-sys-user")
    Call<BaseResponse> createNewSysUser(@Body CreateSystemUserRequest request) ;

    @POST("/api/v1/tours")
    Call<BaseResponse> createTour(@Body CreateTourRequest request);

    @GET("/api/v1/tours/locations")
    Call<List<YourTourDTO.Location>> getAllLocations();

    @Multipart
    @POST("/upload/image")
    Call<BaseResponse> uploadImage(@Part MultipartBody.Part file);

}
