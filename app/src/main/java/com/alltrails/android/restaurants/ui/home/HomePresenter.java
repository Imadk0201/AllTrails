package com.alltrails.android.restaurants.ui.home;

import android.util.Pair;

import com.alltrails.android.restaurants.RestaurantStream;
import com.alltrails.android.restaurants.network.NetworkInterface;

import java.util.Optional;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static com.alltrails.android.restaurants.Utils.PLACE_TYPE;
import static com.alltrails.android.restaurants.Utils.SEARCH_RADIUS;

public class HomePresenter {

    private final HomeViewInterface mainViewInterface;
    private final NetworkInterface networkInterface;
    private final RestaurantStream restaurantStream;

    public HomePresenter(NetworkInterface networkInterface, RestaurantStream restaurantStream, HomeViewInterface mainViewInterface) {
        this.mainViewInterface = mainViewInterface;
        this.networkInterface = networkInterface;
        this.restaurantStream = restaurantStream;
    }

    public void fetchRestaurants(String key, Optional<String> filterKeyWord){
        mainViewInterface.showLoading();
        restaurantStream
                .getUpdatedLocation()
                .take(1)
                .switchMap(location -> filterKeyWord.isPresent() ? networkInterface.getRestarauntsWithKeyWord
                        (location.getLatitude() + "," + location.getLongitude(),
                                SEARCH_RADIUS, PLACE_TYPE, filterKeyWord.get(), key) : networkInterface.getRestaraunts
                        (location.getLatitude() + "," + location.getLongitude(),
                                SEARCH_RADIUS, PLACE_TYPE, key))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(error -> mainViewInterface.onFailure(error.getMessage()))
                .doOnComplete(mainViewInterface::dismissLoading)
                .subscribe(placesResponse -> {
                    mainViewInterface.displayRestaurants(placesResponse);
                    restaurantStream.updateRestaurantResponseList(placesResponse.getRestaurants());
                });
    }

    public void subscribeToSearch() {
        restaurantStream
                .getSubmitSearchQuery()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(irrelevant ->  mainViewInterface.showLoading())
                .doOnComplete(mainViewInterface::dismissLoading)
                .doOnError(error -> mainViewInterface.onFailure(error.getMessage()))
                .subscribe(searchKey -> {
                    if (searchKey != null && searchKey.length() > 0) {
                        mainViewInterface.submitFilter(Optional.of(searchKey));
                    } else {
                        mainViewInterface.submitFilter(Optional.empty());
                    }
                });
    }

    public void registerClick(int index) {
        restaurantStream.updateDisplayRestaurant(index);
    }
}
