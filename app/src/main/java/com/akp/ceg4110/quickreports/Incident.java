package com.akp.ceg4110.quickreports;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class Incident {

    private String name;
    private String description;
    private String weather;
    private List<String> images;


    public Incident(String name){
        this.name = name;
        this.description = "";
        this.weather = "";
        images = new ArrayList<String>();
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setName(String name){this.name = name;}

    public void setImages(List<String> images) {this.images = images;}

    public void setWeather(String weather){this.weather = weather;}

    public void addImage(String image){images.add(image);}

    public String getName(){
        return name;
    }

    public String getDescription(){
        return description;
    }

    public List<String> getImages() {return images;}

    public String getWeather(){return weather;}

    @Override
    public String toString(){
        return String.format("'%1$s', '%2$s', '%3$s'", name, description, weather);
    }

}
