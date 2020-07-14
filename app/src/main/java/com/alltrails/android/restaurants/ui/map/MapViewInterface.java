package com.alltrails.android.restaurants.ui.map;

import android.location.Location;

import com.alltrails.android.restaurants.model.Restaurant;

import java.util.List;

public interface MapViewInterface {

    void showLoading();

    void dismissLoading();

    void updateMapDataSet(List<Restaurant> restaurants);

    void onFailure(String showMessage);

    void updateMapLocation(Location location);
}
