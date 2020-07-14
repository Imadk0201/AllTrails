package com.alltrails.android.restaurants;

import android.location.Location;
import android.util.Pair;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.alltrails.android.restaurants.model.Restaurant;
import com.alltrails.android.restaurants.ui.map.MapPresenter;
import com.alltrails.android.restaurants.ui.map.MapViewInterface;
import com.alltrails.android.restaurants.ui.restaurant_view.RestaurantPresenter;
import com.alltrails.android.restaurants.ui.restaurant_view.RestaurantViewInterface;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.internal.schedulers.ExecutorScheduler;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class RestaurantPresenterTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    RestaurantStream restaurantStream;

    RestaurantPresenter restaurantPresenter;

    @Mock
    RestaurantViewInterface restaurantViewInterface;

    @Before
    public void setUp(){

        MockitoAnnotations.initMocks(this);
        RxAndroidPlugins.setMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());

        restaurantPresenter = new RestaurantPresenter(restaurantStream, restaurantViewInterface);
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
    public void markCurrentLocation_shouldCreateMarker() {
        Location location = mock(Location.class);

        when(restaurantStream.getUpdatedLocation()).thenReturn(Observable.just(location));
        restaurantPresenter.markCurrentLocation();
        verify(restaurantViewInterface).addLocationMarker(eq(location));
    }

    @Test
    public void subscribeToRestaurantDisplay_shouldReturnRestaurant() {
        Location location = mock(Location.class);
        List<Restaurant> restaurants = new ArrayList<>();
        restaurants.add(null);
        restaurants.add(null);
        restaurants.add(null);
        Restaurant restaurant = mock(Restaurant.class);
        restaurants.add(restaurant);
        int nonNullIndex = 3;

        when(restaurantStream.getRestaurantResponseList()).thenReturn(Observable.just(restaurants));
        when(restaurantStream.getDisplayRestaurantId()).thenReturn(Observable.just(nonNullIndex));

        restaurantPresenter.subscribeToRestaurantDisplay();
        verify(restaurantViewInterface).displayRestaurant(eq(restaurants.get(nonNullIndex)));
    }

}