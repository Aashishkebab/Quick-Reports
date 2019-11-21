package com.akp.ceg4110.quickreports;

import android.graphics.Bitmap;

import java.util.List;

public class Incident {

    private String name;
    private String description;
    private String weather;
    private List<Bitmap> images;

    public Incident(){

    }

    public Incident(String name){
        this.name = name;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setName(String name){ this.name = name;}

    public void setImages(List<Bitmap> images) {this.images = images;}

    public void setWeather(String weather){this.weather = weather;}

    public void addImage(Bitmap image){ images.add(image);}

    public String getName(){
        return name;
    }

    public String getDescription(){
        return description;
    }

    public List<Bitmap> getImages() {return images;}

    public String getWeather(){return weather;}

    @Override
    public String toString(){
        return String.format("'%1$s', '%2$s', '%3$s'", name, description, weather);
    }

}
