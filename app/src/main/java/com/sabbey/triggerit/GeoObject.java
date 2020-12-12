package com.sabbey.triggerit;

import com.google.android.gms.maps.model.LatLng;

import java.util.UUID;

public class GeoObject {

    String name;
    LatLng latLng;
    int radius;

    GeoObject(String name, LatLng latLng, int radius)
    {
       this.name = name;
       this.latLng = latLng;
       this.radius = radius;
    }

}
