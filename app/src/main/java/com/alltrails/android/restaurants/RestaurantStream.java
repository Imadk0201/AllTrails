package com.alltrails.android.restaurants;

import android.location.Location;

import com.alltrails.android.restaurants.model.Restaurant;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class RestaurantStream {
    private BehaviorSubject<List<Restaurant>> restaurantResponseList = BehaviorSubject.create();

    private BehaviorSubject<Location> updatedLocation = BehaviorSubject.create();

    private BehaviorSubject<String> submitSearchQuery = BehaviorSubject.create();

    private BehaviorSubject<Integer> displayRestaurantId = BehaviorSubject.create();

    public void setUpdatedLocation(Location location) {
        updatedLocation.onNext(location);
    }

    public void updateSubmitSearchQuery(String submitted) {
        submitSearchQuery.onNext(submitted);
    }

    public void updateRestaurantResponseList(List<Restaurant> restaurants) {
        restaurantResponseList.onNext(restaurants);
    }

    public void updateDisplayRestaurant(Integer restaurantId) {
        displayRestaurantId.onNext(restaurantId);
    }

    public Observable<String> getSubmitSearchQuery() {
        return submitSearchQuery.hide();
    }

    public Observable<Location> getUpdatedLocation() {
        return updatedLocation.hide();
    }

    public Observable<List<Restaurant>> getRestaurantResponseList() {
        return restaurantResponseList.hide();
    }

    public Observable<Integer> getDisplayRestaurantId() {
        return displayRestaurantId.hide();
    }
}
