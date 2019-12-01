package com.akp.ceg4110.quickreports;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class Incident{

    private String name;
    private String description;
    private String weather;
    private List<Bitmap> images;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Activity activity;
    private double lon;
    private double lat;

    public Incident(String name, Activity activity){
        this.name = name;
        this.activity = activity;
        images = new ArrayList<Bitmap>();
    }

    public Incident(){
        //this.activity = new Activity();
        images = new ArrayList<Bitmap>();
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setImages(List<Bitmap> images){
        this.images = images;
    }

    public void setWeather(String weather){
        locationManager = (LocationManager)activity.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener(){
            @Override
            public void onLocationChanged(Location location){
                lon = location.getLongitude();
                lat = location.getLatitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras){

            }

            @Override
            public void onProviderEnabled(String provider){

            }

            @Override
            public void onProviderDisabled(String provider){
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivity(intent);
            }
        };
        if(activity.checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && activity.checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            activity.requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET
            }, 1);
            return;
        }
        this.weather = getWeather();
    }

    public void addImage(Bitmap image){
        images.add(image);
    }

    public String getName(){
        return name;
    }

    public String getDescription(){
        return description;
    }

    public List<Bitmap> getImages(){
        return images;
    }

    public String getWeather(){
        if(activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && activity.checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                activity.requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                }, 1);
        }
        locationManager.requestLocationUpdates("gps", 60000, 1000, locationListener);

        return weather;
    }

    @Override
    public String toString(){
        return String.format("'%1$s', '%2$s', '%3$s'", name, description, weather);
    }

}
