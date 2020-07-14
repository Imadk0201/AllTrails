package com.alltrails.android.restaurants.ui.restaurant_view;


import androidx.core.util.Pair;

import com.alltrails.android.restaurants.RestaurantStream;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;

public class RestaurantPresenter {

    private final RestaurantViewInterface restaurantViewInterface;
    private final RestaurantStream restaurantStream;

    public RestaurantPresenter(RestaurantStream restaurantStream, RestaurantViewInterface restaurantViewInterface) {
        this.restaurantStream = restaurantStream;
        this.restaurantViewInterface = restaurantViewInterface;
    }

    public void subscribeToRestaurantDisplay() {

        Observable.combineLatest(restaurantStream.getRestaurantResponseList().take(1),
                restaurantStream.getDisplayRestaurantId().take(1), Pair::create)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pairResponse -> restaurantViewInterface.displayRestaurant(pairResponse.first.get(pairResponse.second)));
    }

    public void markCurrentLocation() {

        restaurantStream
                .getUpdatedLocation()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(restaurantViewInterface::addLocationMarker);
    }

}
