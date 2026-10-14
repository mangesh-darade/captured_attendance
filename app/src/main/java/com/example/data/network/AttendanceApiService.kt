package com.example.data.network

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

    @GET
    suspend fun getInitData(@Url url: String): Response<com.example.data.model.AttendanceInitResponse>

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
        @Field("token") token: String? = null
    ): Response<StoreAttendanceResponse>
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
