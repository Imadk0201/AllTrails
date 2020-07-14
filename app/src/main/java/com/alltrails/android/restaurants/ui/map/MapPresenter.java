package com.alltrails.android.restaurants.ui.map;

import com.alltrails.android.restaurants.RestaurantStream;
import com.alltrails.android.restaurants.network.NetworkInterface;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MapPresenter {

    private final MapViewInterface mapViewInterface;
    private final RestaurantStream restaurantStream;

    public MapPresenter(MapViewInterface mapViewInterface, RestaurantStream restaurantStream) {
        this.mapViewInterface = mapViewInterface;
        this.restaurantStream = restaurantStream;
    }

    public void updateMapWithLocation() {
        restaurantStream
                .getUpdatedLocation()
                .take(1)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(error -> mapViewInterface.onFailure(error.getMessage()))
                .subscribe(mapViewInterface::updateMapLocation);
    }

    public void updateMapWithDataSet() {
        restaurantStream
                .getRestaurantResponseList()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(error -> mapViewInterface.onFailure(error.getMessage()))
                .subscribe(mapViewInterface::updateMapDataSet);
    }

    public void registerClick(int index) {
        restaurantStream.updateDisplayRestaurant(index);
    }

}
