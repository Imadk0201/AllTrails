package com.alltrails.android.restaurants.ui.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alltrails.android.restaurants.R;
import com.alltrails.android.restaurants.RestaurantStream;
import com.alltrails.android.restaurants.model.PlacesResponse;
import com.alltrails.android.restaurants.network.NetworkInterface;
import com.alltrails.android.restaurants.ui.ListAdapter;
import com.alltrails.android.restaurants.ui.restaurant_view.RestaurantViewActivity;
import com.alltrails.android.restaurants.ui.map.MapFragment;

import java.util.Optional;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

public class HomeFragment extends DaggerFragment implements HomeViewInterface, ListAdapter.OnPageListener {

    @Inject
    Context context;

    @Inject
    ListAdapter adapter;

    HomePresenter homePresenter;

    @Inject
    NetworkInterface networkInterface;

    @Inject
    MapFragment mapFragment;

    ProgressBar progressBar;

    RecyclerView recyclerView;

    @Inject
    RestaurantStream restaurantStream;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.repoListRecyclerView);
        progressBar = view.findViewById(R.id.loading_progress);

        initPresenter();
        initRecyclerView();

        return view;
    }

    private void initPresenter() {
        homePresenter = new HomePresenter(networkInterface, restaurantStream, this);
        homePresenter.fetchRestaurants(context.getString(R.string.google_maps_key), Optional.empty());
        homePresenter.subscribeToSearch();
    }

    private void initRecyclerView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter.setOnPageListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void displayRestaurants(PlacesResponse placesResponse) {
        adapter.setDataset(placesResponse.getRestaurants());
    }

    @Override
    public void onFailure(String showMessage) {
        Toast.makeText(context,  showMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void submitFilter(Optional<String> search) {
        homePresenter.fetchRestaurants(context.getString(R.string.google_maps_key), search);
    }

    @Override
    public void onItemClick(int index) {
        homePresenter.registerClick(index);
        Intent intent = new Intent(context, RestaurantViewActivity.class);
        startActivity(intent);
    }
}
