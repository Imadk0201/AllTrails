package com.alltrails.android.restaurants.ui;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.alltrails.android.restaurants.RestaurantStream;
import com.alltrails.android.restaurants.network.NetworkInterface;
import com.alltrails.android.restaurants.ui.map.MapFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.alltrails.android.restaurants.R;
import com.alltrails.android.restaurants.ui.home.HomeFragment;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import dagger.android.support.DaggerFragment;

public class MainActivity extends DaggerAppCompatActivity implements SearchView.OnQueryTextListener {

    public static final int REQUEST_CODE_LOCATION = 1000;

    Button navigationButton;

    @Inject
    Context context;

    DaggerFragment currentFragment;

    FragmentManager fragmentManager;

    FusedLocationProviderClient fusedLocationClient;

    @Inject
    HomeFragment homeFragment;

    @Inject
    MapFragment mapFragment;

    @Inject
    NetworkInterface networkInterface;

    @Inject
    RestaurantStream restaurantStream;

    SearchView searchView;

    Boolean shouldRefreshData;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigationButton = findViewById(R.id.button_navigation);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        fragmentManager = getSupportFragmentManager();
        shouldRefreshData = false;
        currentFragment = null;

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        validateLocationPermission();
        initButtonNavigation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_bar, menu);

        MenuItem item = menu.findItem(R.id.search);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if (shouldRefreshData) {
                    restaurantStream.updateSubmitSearchQuery("");
                    shouldRefreshData = !shouldRefreshData;
                }

                return true;
            }
        });

        searchView.setOnQueryTextListener(this);

        return true;
    }

    private void initButtonNavigation() {
        fragmentManager.beginTransaction().add(R.id.main_container, mapFragment, "page_list").hide(mapFragment).commit();
        fragmentManager.beginTransaction().add(R.id.main_container, homeFragment, "page_map").commit();

        currentFragment = homeFragment;

        navigationButton.setOnClickListener(irrelevant -> {
            if (navigationButton.getText().toString().equals(getString(R.string.map_key))) {
                fragmentManager.beginTransaction().hide(currentFragment).show(mapFragment).commit();
                currentFragment = mapFragment;

                navigationButton.setText(R.string.list_key);
                navigationButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_format_list_bulleted_24dp, 0,0,0);
            } else {
                fragmentManager.beginTransaction().hide(currentFragment).show(homeFragment).commit();
                currentFragment = homeFragment;

                navigationButton.setText(R.string.map_key);
                navigationButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_map_24dp, 0,0,0);
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        restaurantStream.updateSubmitSearchQuery(query);

        shouldRefreshData = true;
        searchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    /* Simple method that requests location permissions if they are not granted */
    private void validateLocationPermission() {
        // Check for location access permissions
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);
        } else {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    restaurantStream.setUpdatedLocation(location);
                }
            });
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                        if (location != null) {
                            restaurantStream.setUpdatedLocation(location);
                        }
                    });
                } else {
                    Toast.makeText(this, "Location Permissions Required", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }
}
