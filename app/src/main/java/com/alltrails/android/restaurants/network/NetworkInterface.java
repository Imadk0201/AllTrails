package com.alltrails.android.restaurants.network;

import com.alltrails.android.restaurants.model.PlacesResponse;

import java.util.Optional;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NetworkInterface {

    @GET("/maps/api/place/nearbysearch/json")
    Observable<PlacesResponse> getRestaraunts(@Query("location") String location,
                                              @Query("radius") String radius,
                                              @Query("type") String type,
                                              @Query("key") String api_key);

    @GET("/maps/api/place/nearbysearch/json")
    Observable<PlacesResponse> getRestarauntsWithKeyWord(@Query("location") String location,
                                              @Query("radius") String radius,
                                              @Query("type") String type,
                                              @Query("keyword") String keyword,
                                              @Query("key") String api_key);

}
