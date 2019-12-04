package com.akp.ceg4110.quickreports;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.akp.ceg4110.quickreports.ui.addincident.AddIncidentFragment;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.akp.ceg4110.quickreports.MainActivity.db;

public class AddIncidentActivity extends AppCompatActivity{

    //Unique identifier for these permissions to reference later
    static final int REQUEST_IMAGE_CAPTURE = 7;
    static final int REQUEST_WEATHER_PERMISSIONS = 9;
    static Response response;
    private String currentPhotoPath;    //Global variable for image file
    private String originalName;

    private Incident theIncident;

    @Override
    protected void onCreate(Bundle savedInstanceState){ //Auto-generated
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_incident_activity);

        try{
            this.originalName = (String)getIntent().getExtras().getCharSequence("incident_name");
            this.theIncident = db.getIncident(this.originalName);
        }catch(NullPointerException e){
            this.theIncident = new Incident("");
        }

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                                       .replace(R.id.container, AddIncidentFragment.newInstance(this.theIncident)).commitNow();
        }
    }

    /**
     * Creates a uniquely named file to save image
     *
     * @return Image file
     * @throws IOException If something went wrong in creating this file
     */
    private File createImageFile() throws IOException{
        // Create an image file name based on time and date to prevent collisions
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * Onclick for trying to take a picture
     *
     * @param view
     */
    public void dispatchTakePictureIntent(View view){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
           != PackageManager.PERMISSION_GRANTED){   //If permission is not granted
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.CAMERA }, REQUEST_IMAGE_CAPTURE);
        }else{
            takePicture();
        }
    }

    /**
     * Method for calling camera API to take picture
     */
    public void takePicture(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null){
            try{
                // Create the File where the photo should go
                File photoFile = null;
                try{
                    photoFile = createImageFile();
                }catch(IOException ex){
                    Snackbar.make(findViewById(R.id.addincident),
                                  "Error, storage full or something", Snackbar.LENGTH_INDEFINITE)
                            .show();
                }

                if(photoFile != null){  // Continue only if the File was successfully created
                    Uri photoURI = FileProvider.getUriForFile(this,
                                                              "com.akp.ceg4110.quickreports",
                                                              photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    //Take picture
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }catch(SecurityException e){    //This shouldn't occur, but just in case it does
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Onclick for trying to get weather
     *
     * @param view
     */
    public void dispatchGetWeatherIntent(View view){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
           != PackageManager.PERMISSION_GRANTED && ContextCompat
                                                           .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                                   != PackageManager.PERMISSION_GRANTED){   //If permission is not granted
            ActivityCompat.requestPermissions(this, //Request permission
                                              new String[]{
                                                      Manifest.permission.ACCESS_FINE_LOCATION,
                                                      Manifest.permission.ACCESS_COARSE_LOCATION
                                              }, REQUEST_WEATHER_PERMISSIONS);
        }else{
            fetchWeather();
        }
    }

    /**
     * Method for fetching weather
     */
    public void fetchWeather(){
        //@PJ TODO Please add your API code here
        //Use the below statement, but replace the "" with your weather result.
        //You can remove the String variable and put your result directly in setWeather if you want

        LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.darksky.net/forecast/3c40c0529aa9c7cbe9d55ba352e3c15a/" + latitude + "," + longitude + "?exclude" +
                     "=[minutely,hourly,daily,alerts,flags]")
                .get()
                .build();

        try{
            Thread getWeatherThread = new Thread(new NetworkWeatherThread(request, client));
            getWeatherThread.start();
            getWeatherThread.join();
        }catch(InterruptedException e){
            e.printStackTrace();
        }

        try{
            String stringResponse = response.body().string();
            JSONObject jsonObject = new JSONObject(stringResponse);
            String temperature = jsonObject.getJSONObject("currently").getString("temperature");
            String summary = jsonObject.getJSONObject("currently").getString("summary");
            theIncident.setWeather(temperature + "F, " + summary);
        }catch(IOException e){
            Snackbar.make(findViewById(R.id.addincident), "Error getting weather", Snackbar.LENGTH_LONG).show();
        }catch(JSONException e){
            Snackbar.make(findViewById(R.id.addincident), "Error parsing weather information", Snackbar.LENGTH_LONG).show();
            System.out.println("Error parsing weather: " + e.getMessage());
        }

        ((TextView)findViewById(R.id.weather_textview)).setText(theIncident.getWeather());

//        Toast.makeText(this, theIncident.getWeather(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults){

        //Camera
        if(requestCode == REQUEST_IMAGE_CAPTURE){// If request is cancelled, the result arrays are empty.
            if(grantResults.length > 0
               && grantResults[ 0 ] == PackageManager.PERMISSION_GRANTED){
//                Snackbar.make(findViewById(R.id.addincident), "Now try taking your picture again", Snackbar.LENGTH_INDEFINITE)
//                        .show();
                takePicture();
            }else{
                //If user temporarily denied
                if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    //Chain together a whole number of methods because laziness, and show an alert
                    builder.setMessage(
                            "Look, you tried to take a picture, but then you didn't let me do that.\nYou are the epitome of " +
                            "oxyMORON.")
                           .setTitle("Why must you be so difficult?")
                           .setPositiveButton("Whatever", null).create().show();
                }else{  //If the permission was permanently denied
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage( //Can be changed if too silly
                                        "The camera cannot be used if the permissible permission permitting the usage of the camera, which " +
                                        "is a camera and also so happens to be a camera, is denied in a method that creates a denial of " +
                                        "such a permissible permission that permits the accessible accessing of the camera.")
                           .setTitle("Camera permission has been denied!")
                           .setPositiveButton("Yee", null).create().show();
                }
            }
        }

        //Location
        if(requestCode == REQUEST_WEATHER_PERMISSIONS){
            if(grantResults.length > 0 && grantResults[ 0 ] == PackageManager.PERMISSION_GRANTED){
                fetchWeather(); //If it was granted, call the original method we originally wanted to call
            }else{
                //If user temporarily denied
                if(shouldShowRequestPermissionRationale(
                        Manifest.permission.ACCESS_FINE_LOCATION) || shouldShowRequestPermissionRationale(
                        Manifest.permission.ACCESS_COARSE_LOCATION)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    //Chain together a whole number of methods because laziness, and show an alert
                    builder.setMessage("Bruh, I need your location.").setTitle("Really, dude?")
                           .setPositiveButton(
                                   "I'll consider it", null).create().show();
                }else{  //If the permission was permanently denied
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(
                            "You don't want to be tracked, that's cool. Just don't expect anything from me!")
                           .setTitle(
                                   "Okay Edward Snowden")
                           .setPositiveButton("Now you see me, now you don't",
                                              null).create().show();
                }
            }
        }
    }

    /**
     * Onclick for trying to save incident
     *
     * @param view
     */
    public void dispatchSaveIntent(View view){
        if(db == null){
            Toast.makeText(this, "Couldn't access database", Toast.LENGTH_LONG).show();
            return;
        }

        theIncident.setName(((TextView)findViewById(R.id.enter_incident_name_textview)).getText().toString());
        theIncident.setDescription(((TextView)findViewById(R.id.enter_incident_description_textview)).getText().toString());

        if(theIncident.getName().equals("")){
            Snackbar.make(findViewById(R.id.addincident), "Please enter a name of some sort",
                          Snackbar.LENGTH_INDEFINITE).show();
            return;
        }

        if(this.originalName == null){  //We're creating a new incident
            try{
                db.addIncident(theIncident);
                Toast.makeText(this, "New incident added", Toast.LENGTH_SHORT).show();
            }catch(IncidentAlreadyExistsException e){   //If the user uses a duplicate name
                Snackbar.make(findViewById(R.id.addincident), "Use a different name, this one already exists",
                              Snackbar.LENGTH_INDEFINITE).show();
                return;
            }catch(Exception e){
                Snackbar.make(findViewById(R.id.addincident), "Something went horribly wrong.", Snackbar.LENGTH_INDEFINITE).show();
                return;
            }
        }else{  //If this activity was started from pre-existing incident
            try{
                db.updateIncident(theIncident, this.originalName);
                Toast.makeText(this, "Incident updated", Toast.LENGTH_SHORT).show();
            }catch(SQLiteConstraintException ohNo){
                Snackbar.make(findViewById(R.id.addincident), "Use a different name, this name is taken", Snackbar.LENGTH_INDEFINITE)
                        .show();
                return;
            }catch(Exception e){    //More than likely incident doesn't already exist, so originalName is wrong
                try{
                    db.addIncident(theIncident);
                    Toast.makeText(this, "Incident added, this shouldn't have happened", Toast.LENGTH_SHORT).show();
                }catch(Exception ee){   //If incident can neither be added nor updated
                    Snackbar.make(findViewById(R.id.addincident), "Something went terribly wrong.", Snackbar.LENGTH_INDEFINITE).show();
                    return;
                }
            }
        }

        finish();   //Close this screen
    }

    /**
     * Onclick for trying to delete incident
     *
     * @param view
     */
    public void dispatchDeleteIntent(View view){
        if(db == null){
            Toast.makeText(this, "Couldn't access database", Toast.LENGTH_LONG).show();
        }

        try{
            db.removeIncident(this.originalName);
        }catch(Exception e){
            Toast.makeText(this, "Couldn't delete", Toast.LENGTH_LONG).show();
        }
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        GridLayout theImagesLayout = findViewById(R.id.uploaded_images_layout);
        ImageView theImage = new ImageView(this);
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){

            // Get the dimensions of the imageBitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;


            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;

            //Get the display size
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;

            Bitmap imageBitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
            try{
                if(imageBitmap.getHeight() == Math.max(imageBitmap.getHeight(), imageBitmap.getWidth())){

                    imageBitmap = Bitmap.createScaledBitmap(imageBitmap, width / 3 - 20,
                                                            imageBitmap.getHeight() / (imageBitmap.getWidth() / (width / 3 - 20)),
                                                            false);
                }else{
                    imageBitmap = Bitmap
                            .createScaledBitmap(imageBitmap, imageBitmap.getWidth() / (imageBitmap.getHeight() / (width / 3 - 20)),
                                                width / 3 - 20, false);
                }

                imageBitmap = Bitmap.createBitmap(imageBitmap, (imageBitmap.getWidth() - (width / 3 - 20)) / 2,
                                                  (imageBitmap.getHeight() - (width / 3 - 20)) / 2, width / 3 - 20, width / 3 - 20);
            }catch(Exception ignored){  //Just use the full images
            }

            theImage.setImageBitmap(imageBitmap);

            theImage.setMaxWidth(width / 3 - 20);    //Show images at 1/3rd the size for three columns
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                                             LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(15, 19, 0, 0);
            theImage.setLayoutParams(params);

            theImagesLayout.addView(theImage);

            //Update view
            theImage.setAdjustViewBounds(true);

            //Add an animation for the image to fade into the scene
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);
            theImage.startAnimation(animation);

            String photoPath = String.copyValueOf(currentPhotoPath.toCharArray());
            theIncident.addImage(photoPath);

            //TODO Make image full screen when clicked upon
            theImage.setOnClickListener(new OpenImageListener(currentPhotoPath, this));
        }
    }
}

class NetworkWeatherThread implements Runnable{

    private Request request;
    private OkHttpClient client;

    NetworkWeatherThread(Request request, OkHttpClient client){
        this.request = request;
        this.client = client;
    }

    @Override
    public void run(){
        try{
            AddIncidentActivity.response = client.newCall(request).execute();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
