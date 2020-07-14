package com.alltrails.android.restaurants.ui.restaurant_view;

import android.location.Location;

import com.alltrails.android.restaurants.model.Restaurant;

public interface RestaurantViewInterface {

    void displayRestaurant(Restaurant restaurant);

    void addLocationMarker(Location location);
}
