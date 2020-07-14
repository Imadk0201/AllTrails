package com.alltrails.android.restaurants.di.module;

import android.app.Application;
import android.content.Context;

import com.alltrails.android.restaurants.RestaurantStream;
import com.alltrails.android.restaurants.network.NetworkInterface;
import com.alltrails.android.restaurants.ui.ListAdapter;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.alltrails.android.restaurants.Utils.BASE_URL;

@Module
public class AppModule {

    OkHttpClient providesOkHttpClientBuilder() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.readTimeout(2, TimeUnit.MINUTES);

        return builder.build();
    }

    @Provides
    @Singleton
    NetworkInterface provideNetworkApi(Retrofit retrofit) {
        return retrofit.create(NetworkInterface.class);
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(providesOkHttpClientBuilder())
                .build();
    }

    @Provides
    @Singleton
    RestaurantStream provideListStream() {
        return new RestaurantStream();
    }

    @Provides
    ListAdapter provideMyAdapter(Context context){
        return new ListAdapter(context);
    }

    @Provides
    Context provideContext(Application application) {
        return application.getApplicationContext();
    }

}
