package com.tastr.Utilities;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.tastr.Yelp.YelpActivity;

import static java.lang.String.valueOf;

/**
 * Created by Remixt on 10/23/2016.
 */

public class LocationHandler implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    // for defining which activity class is asking for the devices location.
    Activity activity;
    //Permission Variables
    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 1;

    //Variables for requesting locations from Google.
    private static final long POLL_FREQ = 100;
    private static final long FASTEST_UPDATE_FREQ = 100 * 5;
    private LocationRequest mLocationRequest;
    private GoogleApiClient googleApiClient;

    // Location Variables
    private String currentLat = null;
    private String currentLong = null;

    // Constructor, requires activity input. If you are performing this operation from within the activity then simply call new LocationHandler(this);
    public LocationHandler(Activity activity) {
        this.activity = activity;
    }

    public void askForlocation(){
        // Check GPS permissions on the device and ask for them if they aren't already granted.
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ACCESS_COARSE_LOCATION);
        }

        // If permission is granted/already enabled then contact google services.
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            //initialize the gps search parameters.
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(POLL_FREQ);
            mLocationRequest.setFastestInterval(FASTEST_UPDATE_FREQ);

            //checks if google services are available and connects to their location API
            if (servicesAvailable()) {
                googleApiClient = new GoogleApiClient.Builder(activity)
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .build();
                googleApiClient.connect();
            } else {
                System.err.println("Google Play Services Unavailable");
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    break;
                }
                else {

                }
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(YelpActivity.class.getSimpleName(), "Can't connect to Google Play Services!");
        System.err.println("CONNECTION TO GOOGLE FAILED");
    }
    //Start requesting location information from Google if connection is successful.
    @Override
    public void onConnected(Bundle dataBundle) {
        System.err.println("SUCCESSFULLY CONNECTED TO GOOGLE PLAY");
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ACCESS_COARSE_LOCATION);
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
    }
    //Checks to make sure Google Play isn't offline.
    private boolean servicesAvailable() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(activity);
        return result == ConnectionResult.SUCCESS;
    }
    // If the connection from google stops coming in do stuff.
    @Override
    public void onConnectionSuspended(int i) {
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, this);
    }
    // Changes Variables Latitude and Longitude when a new location is found.
    public void onLocationChanged(Location location) {

        if (location != null) {
            currentLong = valueOf(location.getLongitude());
            currentLat = valueOf(location.getLatitude());
            System.out.println("Found Current Location: " + currentLat +" " + currentLong);
            stopLocationUpdates();
        }
    }

    public String getCurrentLong() {
        return currentLong;
    }

    public String getCurrentLat() {
        return currentLat;
    }

}
