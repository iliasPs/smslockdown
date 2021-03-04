package com.ip.smslockdown.models;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.common.util.CollectionUtils;

import java.io.IOException;
import java.util.List;

import lombok.Builder;
import lombok.NonNull;

@Builder
public class LocationModel {

    private final static String TAG = LocationModel.class.getSimpleName();
    @NonNull
    private final String streetAddress;
    @NonNull
    private final Context context;


    public Double getLatitude(){
        double lat = 0.00;
        try {
            Geocoder geocoder = new Geocoder(context);
            List<Address> address = geocoder.getFromLocationName(streetAddress, 1);

            if(!CollectionUtils.isEmpty(address) && address.size()>0){
                lat = address.get(0).getLatitude();
            }
        } catch (IOException e) {
            Log.e(TAG, "Cannot get location data from given Street Address", e);
        }
        return lat;
    }

    public Double getLongitude(){
        double lon = 0.00;
        try {
            Geocoder geocoder = new Geocoder(context);
            List<Address> address = geocoder.getFromLocationName(streetAddress, 1);

            if(!CollectionUtils.isEmpty(address) && address.size()>0){
                lon = address.get(0).getLatitude();
            }
        } catch (IOException e) {
            Log.e(TAG, "Cannot get location data from given Street Address", e);
        }
        return lon;
    }
}
