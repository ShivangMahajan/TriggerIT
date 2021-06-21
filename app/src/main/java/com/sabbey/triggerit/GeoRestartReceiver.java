//package com.sabbey.triggerit;
//
//import android.Manifest;
//import android.app.Activity;
//import android.app.PendingIntent;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentSender;
//import android.content.pm.PackageManager;
//import android.util.Log;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.core.app.ActivityCompat;
//
//import com.google.android.gms.common.api.ApiException;
//import com.google.android.gms.common.api.ResolvableApiException;
//import com.google.android.gms.location.Geofence;
//import com.google.android.gms.location.GeofencingClient;
//import com.google.android.gms.location.GeofencingRequest;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.location.LocationSettingsRequest;
//import com.google.android.gms.location.LocationSettingsResponse;
//import com.google.android.gms.location.LocationSettingsStatusCodes;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class GeoRestartReceiver extends BroadcastReceiver {
//    private GeofencingClient geofencingClient;
//    private GeofenceHelper geofenceHelper;
//    private PendingIntent pendingIntent;
//
//    @Override
//    public void onReceive(final Context context, final Intent intent) {
//        geofencingClient = new GeofencingClient(context);
//        geofenceHelper = new GeofenceHelper(context);
//        pendingIntent = geofenceHelper.getPendingIntent();
//
//        List<GeoObject> list = new ArrayList<>();
//        list = PrefsConfig.readFromPrefs(context);
//
//
//        for (GeoObject geoObject : list) {
//            Geofence geofence = geofenceHelper.getGeofence(geoObject.name, geoObject.latLng, geoObject.radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
//            GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
//            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
//            else
//            geofencingClient.addGeofences(geofencingRequest, pendingIntent)
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                              @Override
//                                              public void onSuccess(Void aVoid) {
//
//                                                  Log.v("here", "added");
//                                                  //Toast.makeText(context, "Added", Toast.LENGTH_LONG);
//                                              }
//                                          }
//                    ).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    //Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
//                    Log.v("here", "error: " + e.getMessage());
//                }
//            });
//
//        }
//    }
//
//}