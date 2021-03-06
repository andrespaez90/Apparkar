package com.innso.apparkar.api.service;

import com.innso.apparkar.api.models.Parking;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PlacesApi {

    @GET("parking_lots.json")
    Single<Map<String, Parking>> getParkingData(@Query("orderBy") String attribute, @Query("equalTo") String value);

    @POST("parking_lots.json")
    Single<Parking> addNewParking(@Body Parking newParking);

}
