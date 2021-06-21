package com.sabbey.triggerit;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            return;
        }
        int transitionType = geofencingEvent.getGeofenceTransition();
        Log.v("here", "here");
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        NotificationManager n = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Log.v("here", "entered");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (n.isNotificationPolicyAccessGranted()) {
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        Toast.makeText(context, "Profile changed to silent", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Log.v("here","exited");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (n.isNotificationPolicyAccessGranted()) {
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                        Toast.makeText(context, "Profile changed to normal", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
}
