package com.hashmal.tourapplication.service;

import com.hashmal.tourapplication.service.dto.BaseResponse;
import com.hashmal.tourapplication.service.dto.CreateBookingRequest;
import com.hashmal.tourapplication.service.dto.CreateSystemUserRequest;
import com.hashmal.tourapplication.service.dto.CreateTourRequest;
import com.hashmal.tourapplication.service.dto.PaymentRequest;
import com.hashmal.tourapplication.service.dto.PaymentResponse;
import com.hashmal.tourapplication.service.dto.StatisticDTO;
import com.hashmal.tourapplication.service.dto.SysUserDTO;
import com.hashmal.tourapplication.service.dto.TourGuideScheduleDTO;
import com.hashmal.tourapplication.service.dto.TourResponseDTO;
import com.hashmal.tourapplication.service.dto.TourScheduleResponseDTO;
import com.hashmal.tourapplication.service.dto.UpdateProfileRequest;
import com.hashmal.tourapplication.service.dto.UpdateTourPackageRequest;
import com.hashmal.tourapplication.service.dto.UpdateTourRequest;
import com.hashmal.tourapplication.service.dto.UpdateUserByAdminRequest;
import com.hashmal.tourapplication.service.dto.UserManagementDTO;
import com.hashmal.tourapplication.service.dto.YourTourDTO;
import com.hashmal.tourapplication.service.dto.CreatePackageRequest;
import com.hashmal.tourapplication.service.dto.UserBookingDTO;
import com.hashmal.tourapplication.service.dto.UserDTO;

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

    @GET("/api/v1/tours/for-user")
    Call<List<TourResponseDTO>> getAllToursForUser(@Query("userId") String userId);

    @POST("/api/v1/tours/bookings")
    Call<BaseResponse> createBooking(@Body CreateBookingRequest request);

    @PUT("/api/v1/tours/bookings/{bookingId}/payment-status")
    Call<BaseResponse> updatePaymentStatus(
            @Path("bookingId") Long bookingId,
            @Query("paymentStatus") Integer paymentStatus);

    @GET("/api/v1/tours/{tourId}/schedules")
    Call<List<TourScheduleResponseDTO>> getTourSchedulesByTourId(@Path("tourId") String tourId);

    @GET("/api/v1/tours/get-tour-info")
    Call<TourResponseDTO> getTourInfo(@Query("tourId") String tourId);

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

    @PUT("user/sys-user-update")
    Call<BaseResponse> updateSysProfile(
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
    Call<BaseResponse> updateUserByAdmin(@Body UpdateUserByAdminRequest request);

    @GET("system/admin/staff-management")
    Call<UserManagementDTO> getStaffManagement();

    @PUT("system/admin/staff-management/update")
    Call<BaseResponse> updateStaffByAdmin(@Body UpdateUserByAdminRequest request);

    @POST("system/admin/create-sys-user")
    Call<BaseResponse> createNewSysUser(@Body CreateSystemUserRequest request);

    @POST("/api/v1/tours")
    Call<BaseResponse> createTour(@Body CreateTourRequest request);

    @PUT("/api/v1/tours/{tourId}")
    Call<BaseResponse> updateTour(
            @Path("tourId") String tourId,
            @Body UpdateTourRequest request);

    @GET("/api/v1/tours/locations")
    Call<List<YourTourDTO.Location>> getAllLocations();

    @Multipart
    @POST("/upload/image")
    Call<BaseResponse> uploadImage(@Part MultipartBody.Part file);

    @POST("/api/v1/tours/add-package")
    Call<BaseResponse> addPackage(@Body CreatePackageRequest request);

    @PUT("/api/v1/tours/update-package")
    Call<BaseResponse> updateTourPackage(@Body UpdateTourPackageRequest request);

    @PUT("/api/v1/tours/modify-status")
    Call<BaseResponse> modifyTourStatus(
            @Query("tourId") String tourId,
            @Query("status") Integer status
    );

    @PUT("/api/v1/tours/package/modify-status")
    Call<BaseResponse> modifyPackageStatus(
            @Query("packageId") Long packageId,
            @Query("status") Integer status
    );

    @GET("user/get-list-tour-guide")
    Call<List<SysUserDTO>> getTourGuide(@Query("status") Integer status);

    @GET("/api/v1/tours/get-staff-info")
    Call<SysUserDTO> getStaffInfo(@Query("staffId") String staffId);

    @GET("/api/v1/tours/get-tour-guide-schedule")
    Call<List<TourGuideScheduleDTO>> getTourGuideSchedules(@Query("tourGuideId") String tourGuideId);

    @GET("/api/v1/tours/get-schedule")
    Call<TourScheduleResponseDTO> getTourSchedule(@Query("tourScheduleId") String tourScheduleId);

    @PUT("/api/v1/tours/add-tour-guide")
    Call<BaseResponse> modifyTourGuideForTour(@Query("tourScheduleId") String tourScheduleId, @Query("tourGuideId") String tourGuideId);

    @GET("/api/v1/tours/get-available-tour-guide")
    Call<List<SysUserDTO>> getAvailableTourGuide(@Query("startTime") String startTime, @Query("endTime") String endTime);

    @GET("/api/v1/tours/get-schedule-bookings")
    Call<List<UserBookingDTO>> getUserBookingsByTourSchedule(@Query("tourScheduleId") String tourScheduleId);

    @PUT("/api/v1/tours/schedule/modify-booking")
    Call<BaseResponse> modifyBooking(@Query("bookingId") Long bookingId, @Query("scheduleId") String scheduleId, @Query("action") String action);

    @GET("/data/user")
    Call<UserDTO> getFullUserInformation(@Query("phoneNumber") String phoneNumber);

    @GET("/api/v1/tours/get-scheduled-tour")
    Call<List<TourScheduleResponseDTO>> getScheduledTours(
            @Query("tourScheduleId") String tourScheduleId,
            @Query("tourId") String tourId,
            @Query("status") String status,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate,
            @Query("tourGuideId") String tourGuideId,
            @Query("isAvailable") Boolean isAvailable
    );
}
