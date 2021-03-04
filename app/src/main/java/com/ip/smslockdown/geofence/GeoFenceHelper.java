package com.ip.smslockdown.geofence;

import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingRequest;
import com.ip.smslockdown.models.LocationModel;

import java.util.ArrayList;
import java.util.List;


public class GeoFenceHelper extends ContextWrapper {

    private List<Geofence> geofenceList = new ArrayList<>();
    private PendingIntent geofencePendingIntent;
    private String streetAddress;
    private Context context;
    private LocationModel locationModel;

    public GeoFenceHelper(Context base) {
        super(base);
        locationModel = LocationModel.builder().streetAddress(streetAddress).context(this).build();
    }

    public void setGeofenceList() {


        geofenceList.add(new Geofence.Builder()
                .setRequestId("id")
                .setCircularRegion(
                        Double.parseDouble(String.valueOf(locationModel.getLatitude())),
                        Double.parseDouble(String.valueOf(locationModel.getLongitude())),
                        (float) 2000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
                .setNotificationResponsiveness(1000)
                .build());


    }

    public GeofencingRequest getGeofencingRequest(Geofence geofence) {

        return new GeofencingRequest.Builder()
                .addGeofence(geofence)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL)
                .build();
    }

    public Geofence getGeofence(String id, String streetAddress) {

        locationModel = LocationModel.builder().streetAddress(streetAddress).context(this).build();

        return new Geofence.Builder()
                .setCircularRegion(
                        Double.parseDouble(String.valueOf(locationModel.getLatitude())),
                        Double.parseDouble(String.valueOf(locationModel.getLongitude())),
                        (float) 2000)
                .setRequestId(id)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
                .setNotificationResponsiveness(1000)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();


    }

    public PendingIntent getPendingIntent() {

        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }

        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        geofencePendingIntent = PendingIntent.getBroadcast(this, 1111, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return geofencePendingIntent;
    }

    public String getErrorString(Exception e) {
        if (e instanceof ApiException) {
            ApiException apiException = (ApiException) e;
            switch (apiException.getStatusCode()) {
                case GeofenceStatusCodes
                        .GEOFENCE_NOT_AVAILABLE:
                    return "GEOFENCE_NOT_AVAILABLE";
                case GeofenceStatusCodes
                        .GEOFENCE_TOO_MANY_GEOFENCES:
                    return "GEOFENCE_TOO_MANY_GEOFENCES";
                case GeofenceStatusCodes
                        .GEOFENCE_TOO_MANY_PENDING_INTENTS:
                    return "GEOFENCE_TOO_MANY_PENDING_INTENTS";

            }
        }
        return e.getLocalizedMessage();
    }

}
