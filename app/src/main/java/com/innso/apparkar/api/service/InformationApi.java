package com.innso.apparkar.api.service;

import com.innso.apparkar.api.models.Parking;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface InformationApi {

    @GET("parking_lots.json")
    Observable<Map<String, Parking>> getParkingData();

    @POST("parking_lots.json/")
    Observable<Parking> addNewParking(@Body Parking newParking);

}
