package com.alltrails.android.restaurants.ui.restaurant_view;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.alltrails.android.restaurants.R;
import com.alltrails.android.restaurants.RestaurantStream;
import com.alltrails.android.restaurants.model.Restaurant;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

import static com.alltrails.android.restaurants.Utils.BASE_URL;
import static com.alltrails.android.restaurants.Utils.DOLLAR_SIGN;

public class RestaurantViewActivity extends DaggerAppCompatActivity implements RestaurantViewInterface, OnMapReadyCallback {

    CardView cardView;

    @Inject
    Context context;

    GoogleMap googleMap;

    ImageView displayImage, backArrow;

    TextView repoTitle, repoSubTitle, numberRatings, otherInfo;

    RatingBar ratingBar;

    RestaurantPresenter restaurantPresenter;

    @Inject
    RestaurantStream restaurantStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_page);
        backArrow = findViewById(R.id.back_arrow);
        cardView = findViewById(R.id.cardView);
        displayImage = findViewById(R.id.movie_image);
        repoTitle = findViewById(R.id.restaurant_title);
        repoSubTitle = findViewById(R.id.reposubtitle);
        ratingBar = findViewById(R.id.rating_bar);
        numberRatings = findViewById(R.id.number_ratings);
        otherInfo = findViewById(R.id.other_info);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        initPagePresenter();
        setLayoutParams();

        backArrow.setOnClickListener(onClick -> onBackPressed());
    }

    void initPagePresenter() {
        restaurantPresenter = new RestaurantPresenter(restaurantStream, this);
        restaurantPresenter.subscribeToRestaurantDisplay();
        restaurantPresenter.markCurrentLocation();
    }

    @Override
    public void displayRestaurant(Restaurant restaurant) {

        System.out.println("*************" + restaurant.getBusinessStatus());
        System.out.println("*************" + restaurant.getReference());
        System.out.println("*************" + restaurant.getVicinity());
        System.out.println("*************" + restaurant.getPlusCode().getCompoundCode());
        System.out.println("*************" + restaurant.getGeometry().getLocationResponse());
        System.out.println("*************" + restaurant.getPlaceId());
        System.out.println("*************" + restaurant);



        if (restaurant.getPhotos() != null && restaurant.getPhotos().size() > 0) {

            Picasso.with(context).load(constructPhotoReference(restaurant.getPhotos().get(0)
                    .getPhotoReference())).resize(600, 400).into(displayImage);
        }

        if (restaurant.getName() != null) {
            repoTitle.setText(restaurant.getName());

            if (restaurant.getGeometry() != null && restaurant.getGeometry().getLocationResponse()!=null) {
                LatLng latLng = new LatLng(restaurant.getGeometry().getLocationResponse().getLat(),
                        restaurant.getGeometry().getLocationResponse().getLng());

                googleMap.addMarker(new MarkerOptions()
                        .title(restaurant.getName() + " Location")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_selected))
                        .anchor(0.5f, 1.0f)
                        .position(latLng));
            }
        }

        if (restaurant.getRating() != null) {
            ratingBar.setRating(restaurant.getRating());
        }

        if (restaurant.getUserRatingsTotal() !=  null) {
            String rating = "(" + restaurant.getUserRatingsTotal() + ")";
            numberRatings.setText(rating);
        }

        if (restaurant.getPriceLevel() != null) {
            String price = new String(new char[restaurant.getPriceLevel()]).replace("\0", DOLLAR_SIGN);
            otherInfo.setText(price);

            otherInfo.append(" • ");
        }

        if (restaurant.getOpeningHours() != null) {
            String open = restaurant.getOpeningHours().getOpenNow() ? "Open Now" : "Closed";

            otherInfo.append(open);
        }

        if (restaurant.getVicinity() != null) {

            otherInfo.append("\n" + restaurant.getVicinity());
        }
    }

    @Override
    public void addLocationMarker(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        googleMap.addMarker(new MarkerOptions()
                .title("My Location")
                .anchor(0.5f, 1.0f)
                .position(latLng));

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,
                13), 1000, null);

    }

    private String constructPhotoReference(String photoReference) {

        return BASE_URL + "/maps/api/place/photo?photoreference=" + photoReference
                + "&sensor=false&maxheight=300&maxwidth=300&key=" + context.getString(R.string.google_maps_key);
    }

    private void setLayoutParams() {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) cardView.getLayoutParams();

        layoutParams.setMarginStart(0);
        layoutParams.setMarginEnd(0);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }
}
