package com.alltrails.android.restaurants.ui.home;

import com.alltrails.android.restaurants.model.PlacesResponse;

import java.util.Optional;

public interface HomeViewInterface {

    void showLoading();

    void dismissLoading();

    void displayRestaurants(PlacesResponse placesResponse);

    void onFailure(String showMessage);

    void submitFilter(Optional<String> search);
}
