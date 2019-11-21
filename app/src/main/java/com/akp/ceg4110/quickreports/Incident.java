package com.akp.ceg4110.quickreports;

import android.graphics.Bitmap;

import java.util.List;

public class Incident {

    private String name;
    private String description;
    private String weather;
    private List<Bitmap> images;

    //This will be the only constructor, the other ones are for debugging only
    public Incident(String name, String description, String weather, List<Bitmap> images){
        this.name = name;
        this.description = description;
        this.images = images;
        this.weather = weather;
    }

    //Debugging only
    public Incident(String name, String description, List<Bitmap> images){
        this.name = name;
        this.description = description;
        this.images = images;
    }

    //Debugging only
    public Incident(String name ,String description, String weather){
        this.name = name;
        this.description = description;
        this.weather = weather;
    }

    //Debugging only
    public Incident(String name, String description){
        this.name = name;
        this.description = description;
    }
    public Incident(String name){
        this.name = name;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setName(String name){ this.name = name;}

    public void setImages(List<Bitmap> images) {this.images = images;}

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
