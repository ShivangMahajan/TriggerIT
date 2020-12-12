package com.sabbey.triggerit;

import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.model.LatLng;

public class GeofenceHelper extends ContextWrapper {
    PendingIntent pendingIntent;

    public GeofenceHelper(Context base) {
        super(base);
    }

    public Geofence getGeofence(String Id, LatLng latLng, float radius, int transitionType)
    {
        return new Geofence.Builder()
                .setRequestId(Id)
                .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                .setTransitionTypes(transitionType)
                .setLoiteringDelay(5000)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();
    }

    public GeofencingRequest getGeofencingRequest(Geofence geofence)
    {
        return new GeofencingRequest.Builder()
                .addGeofence(geofence)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build();
    }

    public PendingIntent getPendingIntent()
    {
        if (pendingIntent != null)
            return pendingIntent;

        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return  pendingIntent;
    }
}
