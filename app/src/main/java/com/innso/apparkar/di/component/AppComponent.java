package com.innso.apparkar.di.component;


import com.google.firebase.database.FirebaseDatabase;
import com.innso.apparkar.api.config.ApiConfig;
import com.innso.apparkar.api.config.TokenAuthenticator;
import com.innso.apparkar.api.controller.ApplicationController;
import com.innso.apparkar.api.controller.MapsController;
import com.innso.apparkar.api.controller.PlacesController;
import com.innso.apparkar.di.modules.ApiModule;
import com.innso.apparkar.di.modules.AppModule;
import com.innso.apparkar.di.modules.FirebaseListenerModule;
import com.innso.apparkar.managers.preferences.PrefsManager;
import com.innso.apparkar.provider.CarWashProvider;
import com.innso.apparkar.provider.ParkingProvider;
import com.innso.apparkar.provider.ResourceProvider;

import javax.inject.Singleton;

import dagger.Component;
import retrofit2.Retrofit;

@Singleton
@Component(modules = {AppModule.class, ApiModule.class, FirebaseListenerModule.class})
public interface AppComponent {

    PrefsManager preferenceManager();

    ResourceProvider resourceProvider();

    FirebaseDatabase firebaseDatabase();

    /**
     * Apis
     **/

    ApiConfig getApiConfig();

    Retrofit retrofit();

    TokenAuthenticator tokenAuthenticator();

    /**
     * Listeners
     */

    ParkingProvider parkingsProvider();

    CarWashProvider carWashProvider();

    /**
     * Controllers
     */
    PlacesController placesController();

    MapsController mapsController();

    ApplicationController applicationController();
}
