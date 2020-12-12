package com.sabbey.triggerit;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.location.Geofence;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PrefsConfig {
    public static void writeInPrefs(Context context, List<GeoObject> list1)
    {
        List<GeoObject> list = readFromPrefs(context);
        if (list == null)
            list = new ArrayList<>();
        list.add(list1.get(0));
        Gson gson = new Gson();
        String jsonString = gson.toJson(list);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("TriggerIT", jsonString);
        editor.apply();
    }

    public static void updateInPrefs(Context context, List<GeoObject> list)
    {

        Gson gson = new Gson();
        String jsonString = gson.toJson(list);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("TriggerIT", jsonString);
        editor.apply();
    }

    public static List<GeoObject> readFromPrefs(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String jsonString = preferences.getString("TriggerIT", "");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<GeoObject>>(){}.getType();
        List<GeoObject> list = gson.fromJson(jsonString, type);
        return list;
    }
}
