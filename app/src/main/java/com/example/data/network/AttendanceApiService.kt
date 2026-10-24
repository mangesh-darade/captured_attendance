package com.example.data.network

import com.example.data.model.AttendanceLoginResponse
import com.example.data.model.AttendanceSetupResponse
import com.example.data.model.DeriveLocationPreviewResponse
import com.example.data.model.StoreAttendanceResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface AttendanceApiService {

    @FormUrlEncoded
    @POST
    suspend fun setup(
        @Url url: String,
        @Field("android_code") androidCode: String
    ): Response<AttendanceSetupResponse>

    @FormUrlEncoded
    @POST
    suspend fun login(
        @Url url: String,
        @Field("phone") phone: String,
        @Field("password") password: String
    ): Response<AttendanceLoginResponse>

    @POST
    suspend fun logout(@Url url: String): Response<AttendanceLoginResponse>

    @GET
    suspend fun getInitData(@Url url: String): Response<com.example.data.model.AttendanceInitResponse>

    @GET
    suspend fun getTodayAttendance(
        @Url url: String,
        @Query("token") token: String,
        @Query("month") month: String? = null,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null
    ): Response<com.example.data.model.TodayAttendanceResponse>

    @GET
    suspend fun getUsersForEnrollment(
        @Url url: String,
        @Query("token") token: String
    ): Response<com.example.data.model.EnrollmentUsersResponse>

    @FormUrlEncoded
    @POST
    suspend fun registerFace(
        @Url url: String,
        @Field("token") token: String,
        @Field("user_id") userId: Int,
        @Field("descriptor") descriptor: String,
        @Field("photo") photo: String? = null
    ): Response<com.example.data.model.RegisterFaceResponse>

    @GET
    suspend fun getCapturedPage(@Url url: String): Response<ResponseBody>

    @FormUrlEncoded
    @POST
    suspend fun deriveLocationPreview(
        @Url url: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String,
        @Field("landmark") landmark: String,
        @Field("selected_derived_location") selectedDerivedLocation: String,
        @Field("descriptor") descriptor: String?,
        @Field("token") token: String? = null
    ): Response<DeriveLocationPreviewResponse>

    @FormUrlEncoded
    @POST
    suspend fun storeAttendance(
        @Url url: String,
        @Field("descriptor") descriptor: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String,
        @Field("landmark") landmark: String,
        @Field("notes") notes: String,
        @Field("attendance_type") attendanceType: String,
        @Field("selected_derived_location") selectedDerivedLocation: String,
        @Field("token") token: String? = null,
        @Field("punched_at") punchedAt: String? = null
    ): Response<StoreAttendanceResponse>

    @GET
    suspend fun getSites(@Url url: String): Response<ResponseBody>

    @FormUrlEncoded
    @POST
    suspend fun sendClientLog(
        @Url url: String,
        @Field("android_code") androidCode: String,
        @Field("device_name") deviceName: String,
        @Field("app_version") appVersion: String,
        @Field("log_level") logLevel: String,
        @Field("tag") tag: String,
        @Field("message") message: String,
        @Field("details") details: String,
        @Field("token") token: String? = null
    ): Response<ResponseBody>
}

interface NominatimService {
    @GET("reverse")
    suspend fun reverseGeocode(
        @Query("format") format: String = "jsonv2",
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("zoom") zoom: Int = 18,
        @Query("addressdetails") addressDetails: Int = 1,
        @Header("User-Agent") userAgent: String = "CaptureAttendanceKiosk/1.0 (Android Native; contact: support@elintom.com)"
    ): Response<ResponseBody>
}
