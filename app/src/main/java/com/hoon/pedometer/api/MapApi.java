package com.hoon.pedometer.api;

import android.support.annotation.NonNull;

import com.hoon.pedometer.api.response.ReverseGeocodeResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 네이버 맵 API
 */
public interface MapApi {

    /**
     * 좌표 -> 주소 변환 API
     *
     * @param query 검색할 좌표 값 (x,y 형식)
     * @return call
     */
    @GET("v1/map/reversegeocode")
    Call<ReverseGeocodeResponse> reverseGeocode(@NonNull @Query("query") String query);
}
