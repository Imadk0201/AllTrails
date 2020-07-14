package com.alltrails.android.restaurants.di.component;

import android.app.Application;

import com.alltrails.android.restaurants.BaseApplication;
import com.alltrails.android.restaurants.di.module.ActivityBindingModule;
import com.alltrails.android.restaurants.di.module.AppModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component (modules = {AndroidSupportInjectionModule.class,
        ActivityBindingModule.class,
        AppModule.class})
public interface AppComponent extends AndroidInjector<BaseApplication> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        AppComponent.Builder application(Application application);

        AppComponent build();
    }
}
