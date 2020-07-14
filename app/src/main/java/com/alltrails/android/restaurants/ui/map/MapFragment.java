package com.alltrails.android.restaurants.ui.map;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alltrails.android.restaurants.R;
import com.alltrails.android.restaurants.RestaurantStream;
import com.alltrails.android.restaurants.model.Restaurant;
import com.alltrails.android.restaurants.ui.ListAdapter;
import com.alltrails.android.restaurants.ui.restaurant_view.RestaurantViewActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

public class MapFragment extends DaggerFragment implements MapViewInterface, OnMapReadyCallback, ListAdapter.OnPageListener {

    @Inject
    Context context;

    GoogleMap mMap;

    @Inject
    ListAdapter adapter;

    MapPresenter mapPresenter;

    ProgressBar progressBar;

    RecyclerView recyclerView;

    Marker lastTapped;

    @Inject
    RestaurantStream restaurantStream;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);

        recyclerView = view.findViewById(R.id.mapRecyclerView);
        progressBar = view.findViewById(R.id.loading_progress_map);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        lastTapped = null;

        initPresenter();
        initRecyclerView();

        return view;
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateMapDataSet(List<Restaurant> restaurants) {
        mMap.clear();
        lastTapped = null;
        adapter.setDataset(restaurants);
        int i = 0;
        for (Restaurant r : restaurants) {
            if (r.getGeometry() != null && r.getGeometry().getLocationResponse() != null) {
                LatLng restaurantLocation = new LatLng(r.getGeometry().getLocationResponse().getLat(),
                        r.getGeometry().getLocationResponse().getLng());

                mMap.addMarker(new MarkerOptions()
                        .title(r.getName())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_unselected))
                        .zIndex(i++)
                        .anchor(0.5f, 1.0f)
                        .position(restaurantLocation));
            }
        }

        mMap.setOnMarkerClickListener(marker -> {
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_selected));
            recyclerView.scrollToPosition((int)(marker.getZIndex()));
            if (lastTapped != null) {
                lastTapped.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_unselected));
            }
            lastTapped = marker;

            return true;
        });
    }

    @Override
    public void onFailure(String showMessage) {
        Toast.makeText(context,  showMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void updateMapLocation(Location location) {
        LatLng newLocation = new LatLng(location.getLatitude(), location.getLongitude());

        mMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newLocation,
                15), 1000, null);

    }

    private void initPresenter() {
        mapPresenter = new MapPresenter( this, restaurantStream);
        mapPresenter.updateMapWithLocation();
        mapPresenter.updateMapWithDataSet();
    }

    private void initRecyclerView(){
        LinearLayoutManager linearLayoutManager
                = new LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false);

        recyclerView.setLayoutManager(linearLayoutManager);
        adapter.setOnPageListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onItemClick(int index) {
        mapPresenter.registerClick(index);
        Intent intent = new Intent(context, RestaurantViewActivity.class);
        startActivity(intent);
    }
}
