package com.example.tableorderserver.Remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GeoCoordinates {
    @GET("maps/geocodes/json")
    Call<String> getGeoCode(@Query("address") String address);

    @GET("maps/api/geocodes/json")
    Call<String> getDirection(@Query("origin") String origin, @Query("destination") String destination);

}
