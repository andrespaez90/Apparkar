package com.innso.apparkar.ui.activities;

import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.innso.apparkar.R;
import com.innso.apparkar.api.controller.MapsController;
import com.innso.apparkar.databinding.ActivityRegisterLocationBinding;
import com.innso.apparkar.ui.BaseActivity;
import com.innso.apparkar.ui.adapters.listeners.OnMarkerDragListenerAdapter;
import com.innso.apparkar.ui.viewModels.RegisterViewModel;

import javax.inject.Inject;

public class RegisterLocationActivity extends BaseActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;

    private GoogleMap mMap;

    private Location currentLocation;

    private RegisterViewModel registerViewModel;

    private SupportMapFragment mapFragment;

    private ActivityRegisterLocationBinding binding;

    @Inject
    MapsController mapsController;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register_location);
        getComponent().inject(this);
        initView();
        requestLocationPermissions();
    }

    private void initView() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
        mapFragment.getMapAsync(this);
        registerViewModel = new RegisterViewModel();
        binding.setViewModel(registerViewModel);
    }

    @Override
    protected void successLocationPermission() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        subscribe();
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    private void subscribe() {
        registerViewModel.showParkingInformation().subscribe(this::showParkingInformation);
        registerViewModel.finisRegisterObserver().subscribe(o -> finish());
    }

    private void showParkingInformation(boolean show) {
        if (show) {
            binding.layoutParkingInformation.getRoot().setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction().hide(mapFragment).commit();
            binding.imageViewWashCar.setVisibility(View.GONE);
            binding.imageViewPetrolStation.setVisibility(View.GONE);
            binding.layoutParkingInformation.editTextParkingName.requestFocus();
        } else {
            binding.layoutParkingInformation.getRoot().setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction().show(mapFragment).commit();
            binding.imageViewWashCar.setVisibility(View.VISIBLE);
            binding.imageViewPetrolStation.setVisibility(View.VISIBLE);
            binding.editTextAddress.requestFocus();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(4.6097100, -74.0817500)));
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        addMapListeners();
    }

    private void addMapListeners() {

        mMap.setOnMarkerDragListener(new OnMarkerDragListenerAdapter() {

            @Override
            public void onMarkerDragEnd(Marker marker) {
                LatLng latLng = marker.getPosition();
                currentLocation.setLongitude(latLng.longitude);
                currentLocation.setLatitude(latLng.latitude);
                registerViewModel.setLocation(latLng);
                updateLocationAddress();
            }
        });
    }

    private void updateLocation() {

        if (currentLocation != null && mMap != null) {

            LatLng newPosition = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

            registerViewModel.setLocation(newPosition);

            CameraPosition cameraPosition = getCameraPosition(newPosition);

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            mMap.addMarker(new MarkerOptions().position(newPosition).draggable(true));
        }
    }

    @NonNull
    private CameraPosition getCameraPosition(LatLng newPosition) {
        return new CameraPosition.Builder()
                .target(newPosition)
                .zoom(15)
                .bearing(0)
                .build();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        updateLocation();
        updateLocationAddress();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private void updateLocationAddress() {
        mapsController.getAddressDescription(currentLocation.getLatitude(), currentLocation.getLongitude())
                .subscribe(address -> binding.editTextAddress.setText(address));
    }
}
