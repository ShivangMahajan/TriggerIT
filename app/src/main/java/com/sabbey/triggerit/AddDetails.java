package com.sabbey.triggerit;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AddDetails extends AppCompatActivity {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static boolean isSelected;
    public static LatLng latLng;
    public LocationManager locationManager;
    private Button selectLocation, saveBtn, deleteBtn;
    private TextInputEditText name, radiusText;
    private int rad, position;
    private TextView locStatus;
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;
    private PendingIntent pendingIntent;
    private GeoObject geoObject;
    private String state;
    private Boolean isEdited;
    private List<GeoObject> geoObjectList;
    //private RadioGroup enterSoundGrp;
    private List<GeoObject> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_details);

        config();
    }

    private void config() {

        isSelected = false;
        isEdited = false;
        deleteBtn = findViewById(R.id.delete);
        deleteBtn.setVisibility(View.GONE);
        selectLocation = findViewById(R.id.selectlocation);
        name = findViewById(R.id.addname);
        //enterSoundGrp = findViewById(R.id.entersound);
        radiusText = findViewById(R.id.addradius);
        locStatus = findViewById(R.id.islocselected);
        saveBtn = findViewById(R.id.savebtn);
        geofencingClient = new GeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);
        geoObjectList = new ArrayList<>();
        list = PrefsConfig.readFromPrefs(getApplicationContext());
        if (list == null)
            list = new ArrayList<>();
        state = getIntent().getStringExtra("state");
        position = getIntent().getIntExtra("pos", -1);
        if (state.equals("edit")) {
            addValues(position);
            deleteBtn.setVisibility(View.VISIBLE);
        }
        selectLoc();
        save();
        delete();
    }

    private void delete() {
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(AddDetails.this);
                builder1.setMessage("Are you sure?");
                builder1.setCancelable(false);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int i) {

                                remove();
                                dialog.cancel();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

            }
        });

    }

    private void remove() {
        List<String> id = new ArrayList<>();
        id.add(list.get(position).name);
        geofencingClient.removeGeofences(id).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                list.remove(position);
                PrefsConfig.updateInPrefs(getApplicationContext(), list);
                Log.v("here", "here1");
                Toast.makeText(getApplicationContext(), "Deleted successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addValues(int pos) {
        if (pos != -1) {

            if (list != null) {
                name.setText(list.get(pos).name);
                radiusText.setText(String.valueOf(list.get(pos).radius));
                latLng = list.get(pos).latLng;
//                String str = list.get(pos).enterSound;
//                switch (str)
//                {
//                    case "On":
//                        enterSoundGrp.check(R.id.entersoundon);
//                        break;
//                    case "Off":
//                        enterSoundGrp.check(R.id.entersoundoff);
//                }
                isSelected = true;
                isEdited = true;
                updateUI();
            }
        }
    }

    private void selectLoc() {
        selectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLocationPermission(AddDetails.this);
                if (!isValid())
                    return;
                if (ContextCompat.checkSelfPermission(AddDetails.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    turnGpsOn();
                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    intent.putExtra("r", Integer.valueOf(radiusText.getText().toString()));
                    if (isEdited) {
                        intent.putExtra("state", "edit");
                        intent.putExtra("pos", position);
                    } else
                        intent.putExtra("state", "new");
                    startActivity(intent);
                }
                else
                {
                    //Log.v("here", "no permission");
                    Toast.makeText(getApplicationContext(), "Grant location permission", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void save() {

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkLocationPermission(AddDetails.this);
                if (!isValid())
                    return;

                if (!isSelected) {
                    Toast.makeText(getApplicationContext(), "Location is not selected", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (ContextCompat.checkSelfPermission(AddDetails.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    turnGpsOn();
                    pendingIntent = geofenceHelper.getPendingIntent();
                    //geofencingClient.removeGeofences(pendingIntent);
                    rad = Integer.parseInt(radiusText.getText().toString());
                    Boolean isDuplicated = checkDuplication();
                    if (isDuplicated) {
                        if (state.equals("new")) {
                            Toast.makeText(getApplicationContext(), "This name is already used, use another name", Toast.LENGTH_LONG).show();
                            return;
                        } else {

                        }
                    }

                    addGeofence(latLng);
                }
                else
                    Toast.makeText(getApplicationContext(), "Grant location permission", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValid() {
        if (name.getText().toString().isEmpty()) {
            name.setError("Enter valid name");
            return false;
        }

        if (radiusText.getText().toString().isEmpty()) {
            radiusText.setError("Enter valid radius");
            return false;
        } else if (Integer.parseInt(radiusText.getText().toString()) < 50) {
            radiusText.setError("Enter valid radius");
            return false;
        }
        return true;
    }

    private boolean checkDuplication() {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).name.equals(name.getText().toString()))
                return true;
        }
        return false;
    }

    private void addGeofence(LatLng latLng) {
        //int id = enterSoundGrp.getCheckedRadioButtonId();
        //RadioButton radioButton = findViewById(id);
        geoObject = new GeoObject(name.getText().toString(), latLng, rad);
        geoObjectList.add(geoObject);
        Geofence geofence = geofenceHelper.getGeofence(name.getText().toString(), latLng, rad, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.v("here", "permission missing");
            return;
        }

        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                          @Override
                                          public void onSuccess(Void aVoid) {
                                              if (state.equals("new"))
                                                  PrefsConfig.writeInPrefs(getApplicationContext(), geoObjectList);
                                              else {
                                                  list.remove(position);
                                                  list.add(position, geoObject);
                                                  PrefsConfig.updateInPrefs(getApplicationContext(), list);
                                              }
                                              Toast.makeText(getApplicationContext(), "Added", Toast.LENGTH_SHORT).show();
                                              finish();
                                          }
                                      }
                ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        if (isSelected) {
            locStatus.setText("Location selected");
            locStatus.setTextColor(Color.parseColor("#3D7314"));
        }
    }

    public static boolean checkLocationPermission(Context context) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);

            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }

    void turnGpsOn() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(getApplicationContext()).checkLocationSettings(builder.build());


        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        AddDetails.this,
                                        LocationRequest.PRIORITY_HIGH_ACCURACY);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LocationRequest.PRIORITY_HIGH_ACCURACY:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        Log.i("here", "onActivityResult: GPS Enabled by user");
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Log.i("here", "onActivityResult: User rejected GPS request");
                        break;
                    default:
                        break;
                }
                break;
        }
    }
}
