package com.alltrails.android.restaurants.di.module;

import com.alltrails.android.restaurants.ui.home.HomeFragment;
import com.alltrails.android.restaurants.ui.map.MapFragment;

import dagger.Module;
import dagger.Provides;

@Module
public class MainActivityModule {
    @Provides
    MapFragment providesProfileFragment() {
        return new MapFragment();
    }

    @Provides
    HomeFragment providesHomeFragment() {
        return new HomeFragment();
    }
}
