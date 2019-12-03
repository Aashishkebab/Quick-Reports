package com.akp.ceg4110.quickreports;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Debug;
import android.os.Environment;
import android.os.FileUtils;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
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

    /**
     * DatabaseAccessor constructor. Creates the tables incident_table and image_table for the database
     * @param db SQLiteDatabase that will be the database for this accessor class. Example creation:
     *           DatabaseAccessor db = new DatabaseAccessor(this.openOrCreateDatabase(DatabaseAccessor.DATABASE_NAME, MODE_PRIVATE, null));
     */
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
        String createPictureTable = String.format("CREATE TABLE IF NOT EXISTS %1$s ( %2$s INTEGER PRIMARY KEY AUTOINCREMENT, %3$s VARCHAR(255), %4$s VARCHAR(255));",
                PICTURE_TABLE, PICTURE_PRIMARY_COLUMN, PICTURE_NAME_COLUMN, PICTURE_COLUMN, PICTURE_PRIMARY_COLUMN);
        try{
            db.execSQL(createIncidentTable); //Add incident table to DB
            db.execSQL(createPictureTable);   //Add picture table to DB
        }catch(Exception e){
            throw e;
        }
    }

    /**
     * Takes an incident object and parses the information stored in it to add it to the database
     * @param incident Incident object that contains all information needed for the database entry
     *                 (String name, String description, String weather, List<Bitmap> images)
     * @throws IncidentAlreadyExistsException If the name in incident already exists in the database
     */
    public void addIncident(@NonNull Incident incident) throws IncidentAlreadyExistsException{
        //TEMPLATE:
        //INSERT INTO incident_table VALUES ('incident.getName()', 'incident.getDescription()');
        //Also written as:
        //INSERT INTO incident_table VALUES (incident.toString());
        //Doesn't include the pictures
        String insertIncidentTable = String.format("INSERT INTO %1$s VALUES ('%2$s', '%3$s', '%4$s');",
                INCIDENT_TABLE, incident.getName(), incident.getDescription(), incident.getWeather());
        List<String> images = incident.getImages();
        try{
            db.execSQL(insertIncidentTable);
            //Add pictures to picture table here
            if(images != null){
                for(int i = 0; i < images.size(); i++){
                    String imagePath = images.get(i);
                    ContentValues imageNameInsert = new ContentValues();
                   //Associate the name of incident to the column that holds name in image_table
                    imageNameInsert.put(PICTURE_NAME_COLUMN, incident.getName());
                    //Associate the picture with the picture column in the image_table
                    imageNameInsert.put(PICTURE_COLUMN, imagePath);
                    //Add the name and picture to the image_table
                    db.insert(PICTURE_TABLE, null, imageNameInsert);
                }
            }else{
                incident.setImages(new ArrayList<String>(){
                });
            }
        }catch(android.database.sqlite.SQLiteConstraintException e){
            throw new IncidentAlreadyExistsException();
        }catch(Exception e){
            System.out.println("Error adding incident to table: " + e.getMessage());
            throw e;
        }
    }


    /**
     * Takes a new incident object and updates the current version of that incident in the database with
     * the new incident's values.
     * @param incident Incident that contains the new values for the current incident
     * @param originalName String that is the name of the incident that is currently in the database
     * @throws IncidentAlreadyExistsException If the new name in incident.getName() already exists in the database
     */
    public void updateIncident(@NonNull Incident incident, @NonNull String originalName){
        //TEMPLATE:
        //UPDATE incident_table SET description = 'incident.getDescription()' WHERE name = 'incident.getName()';
        String updateIncident = String.format("UPDATE %1$s SET description = '%2$s', name = '%3$s', weather = '%4$s' WHERE name = '%5$s';",
                INCIDENT_TABLE, incident.getDescription(), incident.getName(), incident.getWeather(), originalName);
        //Used to count the number of images that the incident contains
        String getImageCount = String.format("SELECT * FROM %1$s WHERE name = '%2$s';",
                PICTURE_TABLE, originalName);
        //Remove current pictures from table corresponding to originalName so new pictures can be added,
        //if image list is the same, the original images will be added
        String deletePictures = String.format("DELETE FROM %1$s WHERE name = '%2$s';",
                PICTURE_TABLE, originalName);
        try{
            db.execSQL(updateIncident); //update values in incident_table
            Cursor imageCountCursor = db.rawQuery(getImageCount, null);
            //Make sure there are pictures for the incident before attempting to remove them
            if(imageCountCursor.getCount() > 0){
                db.execSQL(deletePictures);  //remove pictures from picture table
            }
            imageCountCursor.close();
            //Add updated picture list to picture table
            List<String> images = incident.getImages();
            for(int i = 0; i < images.size(); i++){
                String imageName = images.get(i);
                ContentValues pictureInsert = new ContentValues();
                pictureInsert.put(PICTURE_NAME_COLUMN, incident.getName());
                pictureInsert.put(PICTURE_COLUMN, imageName);
                db.insert(PICTURE_TABLE, null, pictureInsert);
            }
        }catch(Exception e){
            throw e;
        }
    }

    /**
     * Removes all values from the incident with the passed in name from the database
     * @param name String the name of the incident to remove from database
     */
    public void removeIncident(@NonNull String name){
        //TEMPLATE:
        //DELETE FROM incident_table WHERE name='incident.getName()';
        String deleteIncident = String.format("DELETE FROM %1$s WHERE name = '%2$s';", INCIDENT_TABLE, name);
        String deletePictures = String.format("DELETE FROM %1$s WHERE name = '%2$s';", PICTURE_TABLE, name);
        try{
            db.execSQL(deleteIncident); //Remove incident from incident_table
            db.execSQL(deletePictures); //Remove incident from image_table
        }catch(Exception e){
            throw e;
        }
    }

    /**
     * Given a name of an incident, gets all the values corresponding to that incident and creates an incident
     * containing all corresponding data and returns it
     * @param name String to search for in the database
     * @return Incident containing corresponding values based on the name provided
     */
    public Incident getIncident(String name){   //TODO handle special case of name not found
        Cursor incidentCursor = null;   //Holds query results from incident_table
        Cursor imageTableCursor = null; //Holds query results form image_table
        String incidentQuery = String.format("SELECT * FROM %1$s WHERE name = '%2$s';", INCIDENT_TABLE, name);
        String imageQuery = String.format("SELECT * FROM %1$s WHERE name = '%2$s';", PICTURE_TABLE, name);
        Incident incident;
        try{
            incidentCursor = db.rawQuery(incidentQuery, null);
            incidentCursor.moveToFirst();
            //Get numeric value for the different columns in the incident_table
            int incidentNameIndex = incidentCursor.getColumnIndex(NAME_COLUMN);
            int incidentDescriptionIndex = incidentCursor.getColumnIndex(DESCRIPTION_COLUMN);
            int incidentWeatherIndex = incidentCursor.getColumnIndex(INCIDENT_WEATHER);

            //Create and incident and add everything but the images here
            incident = new Incident(incidentCursor.getString(incidentNameIndex));
            incident.setDescription(incidentCursor.getString(incidentDescriptionIndex));
            incident.setWeather(incidentCursor.getString(incidentWeatherIndex));

            //Make object to hold the images
            imageTableCursor = db.rawQuery(imageQuery, null);   //Get the images for incident
            int imagePictureIndex = imageTableCursor.getColumnIndex(PICTURE_COLUMN);
            List<String> images = new ArrayList<String>();
            imageTableCursor.moveToFirst();
            //Add all the bitmaps to a list for the incident
            for(int i = 0; i < imageTableCursor.getCount(); i++){
                String imageName = imageTableCursor.getString(imagePictureIndex);
                images.add(imageName);
            }
            //Close the cursors to prevent memory leaks
            imageTableCursor.close();
            incidentCursor.close();
            incident.setImages(images);
            return incident;
        }catch(Exception e){
            System.out.println("Error getting incident " + name + ": " + e.getMessage());
            throw e;
        }
    }

    /**
     * Returns a list of all the incidents that are stored in the database
     * @return List<Incident> of all incidents in the database
     */
    public List<Incident> getAllIncidents(){
        List<Incident> allIncidents = new ArrayList<Incident>();
        //Object to hold the incident_table query results
        Cursor incidentQueryResults = null;
        //Get numeric values for the columns in the incident_table
        int nameIndex;
        int descriptionIndex;
        int weatherIndex;
        //Object to hold the pictures
        Cursor pictureCursor = null;
        String selectAllQuery = String.format("SELECT * FROM %s;", INCIDENT_TABLE);
        try{
            incidentQueryResults = db.rawQuery(selectAllQuery, null);
            //Get numeric values for the columns in the incident_table
            nameIndex = incidentQueryResults.getColumnIndex(NAME_COLUMN);
            descriptionIndex = incidentQueryResults.getColumnIndex(DESCRIPTION_COLUMN);
            weatherIndex = incidentQueryResults.getColumnIndex(INCIDENT_WEATHER);
            incidentQueryResults.moveToFirst();
            //Get name, description, and weather from incident table
            for(int i = 0; i < incidentQueryResults.getCount(); i++){
                //Get the info from the incident_table and store them in Strings to be added to incident later
                String name = incidentQueryResults.getString(nameIndex);
                String weather = incidentQueryResults.getString(weatherIndex);
                //Make query to get pictures
                String selectPicturesQuery = String.format("SELECT %1$s FROM %2$s WHERE name = '%3$s';",
                        PICTURE_COLUMN, PICTURE_TABLE, name);
                pictureCursor = db.rawQuery(selectPicturesQuery, null);
                pictureCursor.moveToFirst();
                List<String> images = new ArrayList<String>();
                int imageIndex = pictureCursor.getColumnIndex(PICTURE_COLUMN);
                //Iterate through pictures from cursor and add them to a List<Bitmap>
                for(int j = 0; j < pictureCursor.getCount(); j++){
                    String imageName = pictureCursor.getString(imageIndex);
                    images.add(imageName);
                    pictureCursor.moveToNext();
                }
                pictureCursor.close();  //Close cursor to prevent memory leak
                //Create new incident and add values from database to it
                Incident incident = new Incident(name);
                incident.setDescription(incidentQueryResults.getString(descriptionIndex));
                incident.setWeather(weather);
                incident.setImages(images);
                allIncidents.add(incident);
                incidentQueryResults.moveToNext();
            }
            incidentQueryResults.close(); //Close cursor to prevent memory leak
        }catch(Exception e){
            System.out.println("Error getting all incidents: " + e.getMessage());
            throw e;
        }
        return allIncidents;
    }

    /**
     * Returns the Bitmap of the file specified by path
     * @param path String - path of the file to be returned
     * @return Bitmap of the file specified by path
     */
    private Bitmap getImage(String path){
        return BitmapFactory.decodeFile(path);
    }

    /**
     * Takes an Bitmap of an image and stores it to the file system and returns the path of the file
     * @param image Bitmap of the image to be stored in file system
     * @return String of the path of the file that was created
     */
//    private String storeImage(Bitmap image){
//        //Get the path of application
//        ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
//        File myDir = cw.getDir(savedImages, Context.MODE_PRIVATE);
//
//        //Create name for this specific image
//        String fImageName = "image" + imageNumber + ".png";
//        File fImage = new File(myDir, fImageName);
//        //Replace image if it already exists
//        if(fImage.exists()){
//            fImage.delete();
//        }
//        try{
//            FileOutputStream out = new FileOutputStream(fImage);
//            image.compress(Bitmap.CompressFormat.PNG, 100, out);
//            out.flush();
//            out.close();
//            imageNumber++;
//        }catch(Exception e){
//            System.out.println("Error storing image " + fImageName + " in file system - " + e.getMessage());
//        }
//        return myDir + fImageName;
//    }

    /**
     * Converts Bitmap image into byte[] representation
     * @param image Bitmap of the image that needs to be converted to a byte[]
     * @return byte[] representation of the passed in Bitmap
     */
    private byte[] imageToByte(Bitmap image){
        ByteArrayOutputStream oStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 50, oStream);
        return oStream.toByteArray();
    }

    /**
     * Converts a byte[] into a Bitmap representation of the image
     * @param bImage byte[] of the image that needs to be converted to a Bitmap
     * @return Bitmap representation of the passed in byte[]
     */
    private Bitmap byteToImage(byte[] bImage){
        return BitmapFactory.decodeByteArray(bImage, 0, bImage.length);
    }

    //FOR DEBUGGING ONLY
    @Deprecated void dropAllTables(){
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

    //FOR DEBUGGING ONLY
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

    /**
     * Gets the total number of images stored
     * @return int - number of images that are stored in the database
     */
    public int getIncidentCount(){
        int size = -1;
        Cursor countCursor;
        try{
            countCursor = db.rawQuery("SELECT * FROM incident_table;", null);
            size = countCursor.getCount();
        }catch(Exception e){
            System.out.println("Error getting count of incidents");
        }
        return size;
    }

    //FOR DEBUGGING ONLY
    @Deprecated public void removeAllRows(){
        String removeIncidentRows = String.format("DELETE FROM %1$s;", INCIDENT_TABLE);
        String removePictureRows = String.format("DELETE FROM %1$s;", PICTURE_TABLE);
        try{
            db.execSQL(removeIncidentRows);
            db.execSQL(removePictureRows);
        }catch(Exception e){
            System.out.println("Error deleting all rows: " + e.getMessage());
        }

    }

    //FOR DEBUGGING ONLY
    public void removeAllPictures(){
        String removePictureRows = String.format("DELETE FROM %1$s;", PICTURE_TABLE);
        try{
            db.execSQL(removePictureRows);
        }catch(Exception e){
            System.out.println("Error deleting all rows: " + e.getMessage());
        }
    }
}
/**
 * We need this custom exception class because we need to throw a CHECKED exception if the incident
 * already exists, which FORCES the calling method to handle when an incident already exists.
 */
class IncidentAlreadyExistsException extends Exception{
    IncidentAlreadyExistsException(){
    }
}
