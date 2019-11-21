package com.akp.ceg4110.quickreports;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseAccessor
{

    public static final String DATABASE_NAME = "Incidents";

    public static final String INCIDENT_TABLE = "incident_table";
    public static final String NAME_COLUMN = "name";
    public static final String DESCRIPTION_COLUMN = "description";
    public static final String INCIDENT_WEATHER = "weather";

    public static final String PICTURE_TABLE  = "image_table";
    public static final String PICTURE_NAME_COLUMN = "name";
    public static final String PICTURE_PRIMARY_COLUMN = "picture_reference";
    public static final String PICTURE_COLUMN = "picture";

    private SQLiteDatabase db;


    public DatabaseAccessor(SQLiteDatabase db){
        this.db = db;
        //Build string for creating the incident_table table
        //TEMPLATE:
        //CREATE TABLE IF NOT EXISTS incident_table (name VARCHAR(255), description VARCHAR(255), PRIMARY KEY (name));
        String createIncidentTable = String.format("CREATE TABLE IF NOT EXISTS %1$s (%2$s VARCHAR(255), %3$s VARCHAR(255), %4$s VARCHAR(500), PRIMARY KEY(%5$s));",
                INCIDENT_TABLE, NAME_COLUMN, DESCRIPTION_COLUMN, INCIDENT_WEATHER, NAME_COLUMN);
        //Build string for creating the image_table table
        //TEMPLATE:
        //CREATE TABLE IF NOT EXISTS image_table (picture_reference INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR(255), picture BLOB);
        String createPictureTable = String.format("CREATE TABLE IF NOT EXISTS %1$s ( %2$s INTEGER PRIMARY KEY AUTOINCREMENT, %3$s VARCHAR(255), %4$s BLOB);",
                PICTURE_TABLE, PICTURE_PRIMARY_COLUMN, PICTURE_NAME_COLUMN, PICTURE_COLUMN, PICTURE_PRIMARY_COLUMN);
        try{
            db.execSQL(createIncidentTable); //Add incident table to DB
            db.execSQL(createPictureTable);   //Add picture table to DB
        }catch(Exception e){
            System.out.println("Error creating table: " + e.getMessage());
        }
    }

    public void addIncident(Incident incident){
        //TEMPLATE:
        //INSERT INTO incident_table VALUES ('incident.getName()', 'incident.getDescription()');
        //Also written as:
        //INSERT INTO incident_table VALUES (incident.toString());
        //Doesn't include the images
        String insertIncidentTable = String.format("INSERT INTO %1$s VALUES ('%2$s', '%3$s', '%4$s');",
                INCIDENT_TABLE, incident.getName(), incident.getDescription(), incident.getWeather());
        List<Bitmap> images = incident.getImages();
        try{
            db.execSQL(insertIncidentTable);
            //Add pictures to picture table
            for(int i = 0; i < images.size(); i++) {
                ContentValues imageNameInsert = new ContentValues();
                imageNameInsert.put(PICTURE_NAME_COLUMN, incident.getName());
                imageNameInsert.put(PICTURE_COLUMN, imageToByte(images.get(i)));
                db.insert(PICTURE_TABLE, null, imageNameInsert);
            }
        }catch(Exception e){
            System.out.println("Error inserting incident into DB: " + e.getMessage());
        }
    }


    //Name isn't mutable
    public void updateIncident(Incident incident, String originalName){
        //TEMPLATE:
        //UPDATE incident_table SET description = 'incident.getDescription()' WHERE name = 'incident.getName()';
        String updateIncident = String.format("UPDATE %1$s SET description = '%2$s', name = '%3$s', weather = '%4$s' WHERE name = '%5$s';",
                INCIDENT_TABLE, incident.getDescription(), incident.getName(), incident.getWeather(), originalName);
        String updatePicture = String.format("UPDATE %1$s SET name = '%2$s' WHERE name = '%3$s';",
                PICTURE_TABLE, incident.getName(), originalName);
        try {
            db.execSQL(updateIncident);
            db.execSQL(updatePicture);
        }catch(Exception e){
            System.out.println("Error updating incident: " + e.getMessage());
        }
    }

    public void removeIncident(String name){
        //TEMPLATE:
        //DELETE FROM incident_table WHERE name='incident.getName()';
//        String deleteStatement = "DELETE FROM " + INCIDENT_TABLE + " WHERE name=" + "'" + incident.getName() + "';";
        String deleteIncident = String.format("DELETE FROM %1$s WHERE name = '%2$s';", INCIDENT_TABLE, name);
        String deletePictures = String.format("DELETE FROM %1$s WHERE name = '%2$s';", PICTURE_TABLE, name);
        try{
            db.execSQL(deleteIncident);
            db.execSQL(deletePictures);
            System.out.println("Removed pictures from incident: " + name);
        }catch(Exception e){
            System.out.println("Error removing incident: " + e.getMessage());
        }
    }

    public Incident getIncident(String name){
        Cursor incidentCursor = null;
        Cursor imageTableCursor = null;
        String incidentQuery = String.format("SELECT * FROM %1$s WHERE name = '%2$s';", INCIDENT_TABLE, name);
        String imageQuery = String.format("SELECT * FROM %1$s WHERE name = '%2$s';", PICTURE_TABLE, name);
        Incident incident;
        try{
            incidentCursor = db.rawQuery(incidentQuery, null);
            incidentCursor.moveToFirst();
            int incidentNameIndex = incidentCursor.getColumnIndex(NAME_COLUMN);
            int incidentDescriptionIndex = incidentCursor.getColumnIndex(DESCRIPTION_COLUMN);
            int incidentWeatherIndex = incidentCursor.getColumnIndex(INCIDENT_WEATHER);
            //Add name and description to the incident object
            incident = new Incident(incidentCursor.getString(incidentNameIndex),
                    incidentCursor.getString(incidentDescriptionIndex),
                    incidentCursor.getString(incidentWeatherIndex));

            imageTableCursor = db.rawQuery(imageQuery, null);
            int imagePictureIndex = imageTableCursor.getColumnIndex(PICTURE_COLUMN);
            List<Bitmap> images = new ArrayList<Bitmap>();
            imageTableCursor.moveToFirst();
            //Add all the bitmaps to a list for the incident
            for(int i = 0; i < imageTableCursor.getCount(); i++){
                byte[] blobImage = imageTableCursor.getBlob(imagePictureIndex);
                Bitmap bMap = BitmapFactory.decodeByteArray(blobImage, 0, blobImage.length);
                images.add(bMap);
            }
            imageTableCursor.close();
            incidentCursor.close();
            incident.setImages(images);
            return incident;
        }catch(Exception e){
            System.out.println("Error getting incident " + name + ": " + e.getMessage());
        }
        return null;
    }

    public List<Incident> getAllIncidents(){
        List<Incident> allIncidents = new ArrayList<Incident>();
        Cursor queryResults = null;
        int nameIndex;
        int descriptionIndex;
        int weatherIndex;
        Cursor pictureCursor = null;
        String selectAllQuery = String.format("SELECT * FROM %s;", INCIDENT_TABLE);
        try{
            queryResults = db.rawQuery(selectAllQuery, null);
            nameIndex = queryResults.getColumnIndex(NAME_COLUMN);
            descriptionIndex = queryResults.getColumnIndex(DESCRIPTION_COLUMN);
            weatherIndex = queryResults.getColumnIndex(INCIDENT_WEATHER);
            queryResults.moveToFirst();
            //Get name, description, and weather from incident table
            for(int i = 0; i < queryResults.getCount(); i++){
                String name = queryResults.getString(nameIndex);
                String weather = queryResults.getString(weatherIndex);
                String selectPicturesQuery = String.format("SELECT %1$s FROM %2$s WHERE name = '%3$s';",
                        PICTURE_COLUMN, PICTURE_TABLE, name);
                pictureCursor = db.rawQuery(selectPicturesQuery, null);
                pictureCursor.moveToFirst();
                List<Bitmap> images = new ArrayList<Bitmap>();
                int imageCount = pictureCursor.getCount();
                //Get pictures from picture table
                for(int j = 0; j < pictureCursor.getCount(); j++){
                    byte[] imageBytes = pictureCursor.getBlob(j);
                    Bitmap bitImage = byteToImage(imageBytes);
                    images.add(bitImage);
                    pictureCursor.moveToNext();
                }
                pictureCursor.close();
                Incident incident = new Incident(name, queryResults.getString(descriptionIndex), weather, images);
                allIncidents.add(incident);
                queryResults.moveToNext();
            }
            queryResults.close();
        }catch(Exception e){
            System.out.println("Error getting all incidents: " + e.getMessage());
        }
        return allIncidents;
    }

    private byte[] imageToByte(Bitmap image){
        ByteArrayOutputStream oStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 50, oStream);
        byte[] imageBytes = oStream.toByteArray();
        return imageBytes;
    }

    private Bitmap byteToImage(byte[] bImage){
        Bitmap bitmapImage = BitmapFactory.decodeByteArray(bImage, 0, bImage.length);
        return bitmapImage;
    }

    public void dropAllTables(){
        String dropIncidentTable = "DROP TABLE " + INCIDENT_TABLE;
        String dropImageTable = "DROP TABLE " + PICTURE_TABLE;
        try{
            db.execSQL(dropIncidentTable);
            System.out.println("Dropped incident table");
            db.execSQL(dropImageTable);
            System.out.println("Dropped picture table");
        }catch(Exception e){
            System.out.println("Error dropping tables: " + e.getMessage());
        }
    }

    public int getImageCount(){
        int size = -1;
        Cursor countCursor;
        try{
            countCursor = db.rawQuery("SELECT * FROM image_table;", null);
            size = countCursor.getCount();
        }catch(Exception e){
            System.out.println("Error getting count: " + e.getMessage());
        }
        return size;
    }

    public void removeAllRows(){
        String removeIncidentRows = String.format("DELETE FROM %1$s;", INCIDENT_TABLE);
        String removePictureRows = String.format("DELETE FROM %1$s;", PICTURE_TABLE);
        try{
            db.execSQL(removeIncidentRows);
            db.execSQL(removePictureRows);
        }catch(Exception e){
            System.out.println("Error deleting all rows: " + e.getMessage());
        }

    }
}
