package com.alltrails.android.restaurants;

import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.alltrails.android.restaurants.model.PlacesResponse;
import com.alltrails.android.restaurants.model.Restaurant;
import com.alltrails.android.restaurants.network.NetworkInterface;
import com.alltrails.android.restaurants.ui.home.HomePresenter;
import com.alltrails.android.restaurants.ui.home.HomeViewInterface;
import com.alltrails.android.restaurants.ui.map.MapPresenter;
import com.alltrails.android.restaurants.ui.map.MapViewInterface;

import org.junit.Before;
import org.junit.BeforeClass;
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

import static com.alltrails.android.restaurants.Utils.PLACE_TYPE;
import static com.alltrails.android.restaurants.Utils.SEARCH_RADIUS;
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
public class MapPresenterTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    RestaurantStream restaurantStream;

    MapPresenter mapPresenter;

    @Mock
    MapViewInterface mapViewInterface;

    @Before
    public void setUp(){

        MockitoAnnotations.initMocks(this);
        RxAndroidPlugins.setMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());

        mapPresenter = new MapPresenter(mapViewInterface, restaurantStream);
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
    public void registerClickOnMap_shouldEmitIndex() {
        int testIndex = 2;

        mapPresenter.registerClick(testIndex);
        verify(restaurantStream).updateDisplayRestaurant(testIndex);

        testIndex = 5;
        mapPresenter.registerClick(testIndex);
        verify(restaurantStream).updateDisplayRestaurant(testIndex);
    }

    @Test
    public void updateMapWithLocation_shouldEmitLocation() {
        Location testLocation = mock(Location.class);
        testLocation.setLatitude(36.94);
        testLocation.setLongitude(117.31);

        when(restaurantStream.getUpdatedLocation()).thenReturn(Observable.just(testLocation));
        mapPresenter.updateMapWithLocation();
        verify(mapViewInterface).updateMapLocation(testLocation);
    }

    @Test
    public void updateMapWithDataset_shouldUpdateDataset() {

        List<Restaurant> restaurants = new ArrayList<>();
        when(restaurantStream.getRestaurantResponseList()).thenReturn(Observable.just(restaurants));
        mapPresenter.updateMapWithDataSet();
        verify(mapViewInterface).updateMapDataSet(eq(restaurants));
    }
}