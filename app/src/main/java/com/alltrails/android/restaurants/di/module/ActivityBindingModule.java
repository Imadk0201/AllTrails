package com.alltrails.android.restaurants.di.module;

import com.alltrails.android.restaurants.ui.MainActivity;
import com.alltrails.android.restaurants.ui.home.HomeFragment;
import com.alltrails.android.restaurants.ui.map.MapFragment;
import com.alltrails.android.restaurants.ui.restaurant_view.RestaurantViewActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBindingModule {

    @ContributesAndroidInjector(modules = {MainActivityModule.class})
    abstract MainActivity bindMainActivity();

    @ContributesAndroidInjector(modules = {MainActivityModule.class})
    abstract HomeFragment bindHomeFragment();

    @ContributesAndroidInjector(modules = {MainActivityModule.class})
    abstract MapFragment bindProfileFragment();

    @ContributesAndroidInjector
    abstract RestaurantViewActivity bindPageActivity();
}
