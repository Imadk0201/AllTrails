package com.alltrails.android.restaurants;

import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.alltrails.android.restaurants.model.PlacesResponse;
import com.alltrails.android.restaurants.model.Restaurant;
import com.alltrails.android.restaurants.network.NetworkInterface;
import com.alltrails.android.restaurants.ui.home.HomePresenter;
import com.alltrails.android.restaurants.ui.home.HomeViewInterface;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.Any;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.internal.schedulers.ExecutorScheduler;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.schedulers.TestScheduler;

import static com.alltrails.android.restaurants.Utils.PLACE_TYPE;
import static com.alltrails.android.restaurants.Utils.SEARCH_RADIUS;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class HomePresenterTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    HomePresenter homePresenter;

    @Mock
    NetworkInterface networkInterface;

    @Mock
    RestaurantStream restaurantStream;

    @Mock
    HomeViewInterface homeViewInterface;

    @Before
    public void setUp(){

        MockitoAnnotations.initMocks(this);
        RxAndroidPlugins.setMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());

        homePresenter = new HomePresenter(networkInterface, restaurantStream, homeViewInterface);
    }

    @BeforeClass
    public static void setUpRxSchedulers() {
        Scheduler immediate = new Scheduler() {
            @Override
            public Disposable scheduleDirect(@NonNull Runnable run, long delay, @NonNull TimeUnit unit) {
                // this prevents StackOverflowErrors when scheduling with a delay
                return super.scheduleDirect(run, 0, unit);
            }

            @Override
            public Worker createWorker() {
                return new ExecutorScheduler.ExecutorWorker(Runnable::run, true, true);
            }
        };

        RxJavaPlugins.setInitIoSchedulerHandler(scheduler -> immediate);
        RxJavaPlugins.setInitComputationSchedulerHandler(scheduler -> immediate);
        RxJavaPlugins.setInitNewThreadSchedulerHandler(scheduler -> immediate);
        RxJavaPlugins.setInitSingleSchedulerHandler(scheduler -> immediate);
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> immediate);
    }

    @Test
    public void testListClicks_shouldEmitInteger() {
        int testInt = 5;

        homePresenter.registerClick(testInt);
        verify(restaurantStream).updateDisplayRestaurant(testInt);

        testInt = 3;
        homePresenter.registerClick(testInt);
        verify(restaurantStream).updateDisplayRestaurant(testInt);
    }

    @Test
    public void searchQuerySubscriptionTest_shouldEmitFilter() {
        String search = "TestSearch";
        when(restaurantStream.getSubmitSearchQuery()).thenReturn(Observable.just(search));
        homePresenter.subscribeToSearch();
        verify(homeViewInterface).dismissLoading();
        verify(homeViewInterface).submitFilter(Optional.of(search));
    }

    @Test
    public void searchQuerySubscriptionTest_shouldEmitEmpty() {
        String search = "";
        when(restaurantStream.getSubmitSearchQuery()).thenReturn(Observable.just(search));
        homePresenter.subscribeToSearch();
        verify(homeViewInterface).dismissLoading();
        verify(homeViewInterface).submitFilter(Optional.empty());
    }

    @Test
    public void fetchRestaurantsSubscriptionTest_noFilter_shouldDispalyList() {
        String key = "someKey";
        double testLatitude = 37.34;
        double testLongitude = 115.75;

        PlacesResponse placesResponse = mock(PlacesResponse.class);
        placesResponse.setRestaurants(getRestaurants());

        Location testLocation = mock(Location.class);
        testLocation.setLatitude(testLatitude);
        testLocation.setLongitude(testLongitude);
        testLocation.setAccuracy(1);

        when(restaurantStream.getUpdatedLocation()).thenReturn(Observable.just(testLocation));
        when(networkInterface.getRestaraunts(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Observable.just(placesResponse));

        homePresenter.fetchRestaurants(key, Optional.empty());

        verify(homeViewInterface).showLoading();
        verify(networkInterface).getRestaraunts(eq(testLocation.getLatitude()
                +  "," + testLocation.getLongitude()), eq(SEARCH_RADIUS), eq(PLACE_TYPE), eq(key));
        verify(homeViewInterface).dismissLoading();
        verify(homeViewInterface).displayRestaurants(placesResponse);
        verify(restaurantStream).updateRestaurantResponseList(anyList());
    }

    @Test
    public void fetchRestaurantsSubscriptionTest_withFilter_shouldDispalyList() {
        String key = "someKey2";
        String filter = "someFilter";

        double testLatitude = 35.19;
        double testLongitude = 116.92;

        PlacesResponse placesResponse = mock(PlacesResponse.class);
        placesResponse.setRestaurants(getRestaurants());

        Location testLocation = mock(Location.class);
        testLocation.setLatitude(testLatitude);
        testLocation.setLongitude(testLongitude);
        testLocation.setAccuracy(1);

        when(restaurantStream.getUpdatedLocation()).thenReturn(Observable.just(testLocation));
        when(networkInterface.getRestarauntsWithKeyWord(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Observable.just(placesResponse));

        homePresenter.fetchRestaurants(key, Optional.of(filter));

        verify(homeViewInterface).showLoading();
        verify(networkInterface).getRestarauntsWithKeyWord(eq(testLocation.getLatitude()
                +  "," + testLocation.getLongitude()), eq(SEARCH_RADIUS), eq(PLACE_TYPE), eq(filter), eq(key));
        verify(homeViewInterface).dismissLoading();
        verify(homeViewInterface).displayRestaurants(placesResponse);
        verify(restaurantStream).updateRestaurantResponseList(anyList());
    }


    private List<Restaurant> getRestaurants() {
        List<Restaurant> restaurants = new ArrayList<>();

        Restaurant restaurant = mock(Restaurant.class);
        restaurant.setName("Restaurant1");
        restaurant.setId("id1");
        restaurant.setPlaceId("place1");
        restaurants.add(restaurant);

        Restaurant restaurant2 = mock(Restaurant.class);
        restaurant.setName("Restaurant2");
        restaurant.setId("id2");
        restaurant.setPlaceId("place2");
        restaurants.add(restaurant2);

        Restaurant restaurant3 = mock(Restaurant.class);
        restaurant.setName("Restaurant3");
        restaurant.setId("id3");
        restaurant.setPlaceId("place3");
        restaurants.add(restaurant3);

        return restaurants;
    }
}