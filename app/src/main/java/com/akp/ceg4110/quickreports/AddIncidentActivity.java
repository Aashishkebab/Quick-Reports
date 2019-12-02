package com.akp.ceg4110.quickreports;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProviders;

import com.akp.ceg4110.quickreports.ui.addincident.AddIncidentFragment;
import com.akp.ceg4110.quickreports.ui.addincident.AddIncidentViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.akp.ceg4110.quickreports.MainActivity.db;

public class AddIncidentActivity extends AppCompatActivity{

    //Unique identifier for these permissions to reference later
    static final int REQUEST_IMAGE_CAPTURE = 7;
    static final int REQUEST_WEATHER_PERMISSIONS = 9;
    private String currentPhotoPath;    //Global variable for image file
    private String originalName;
    private Incident thisIncident;

    @Override
    protected void onCreate(Bundle savedInstanceState){ //Auto-generated
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_incident_activity);
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                                       .replace(R.id.container, AddIncidentFragment.newInstance())
                                       .commitNow();
        }
        this.originalName = (String)getIntent().getExtras().getCharSequence("incident_name");
        AddIncidentViewModel loginViewModel = ViewModelProviders.of(this).get(AddIncidentViewModel.class);
        if(this.originalName != null){
            fillInPage();
        }
    }

    private void fillInPage(){
        this.thisIncident = db.getIncident(this.originalName);
        TextView hi = findViewById(R.id.enter_incident_name_textview);
        ((TextView)findViewById(R.id.enter_incident_name_textview)).setText(thisIncident.getName());

        try{
            ((TextView)findViewById(R.id.enter_incident_description_textview)).setText(thisIncident.getDescription());
        }catch(NullPointerException ignored){
        }  //If empty description
    }

    /**
     * Creates a uniquely named file to save image
     *
     * @return Image file
     * @throws IOException If something went wrong in creating this file
     */
    private File createImageFile() throws IOException{
        // Create an image file name based on time and date to prevent collisions
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
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
            ActivityCompat.requestPermissions(this, //Request permission
                                              new String[]{ Manifest.permission.CAMERA }, REQUEST_IMAGE_CAPTURE);
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
                    Snackbar.make(findViewById(R.id.addincident), "Error, storage full or something", Snackbar.LENGTH_INDEFINITE)
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
           != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
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
        String weather = "";
        thisIncident.setWeather(weather);
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
                           .setTitle("Why must you be so difficult?").setPositiveButton("Whatever", null).create().show();
                }else{  //If the permission was permanently denied
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage( //Can be changed if too silly
                                        "The camera cannot be used if the permissible permission permitting the usage of the camera, which " +
                                        "is a camera and also so happens to be a camera, is denied in a method that creates a denial of " +
                                        "such a permissible permission that permits the accessible accessing of the camera.")
                           .setTitle("Camera permission has been denied!").setPositiveButton("Yee", null).create().show();
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
                    builder.setMessage("Bruh, I need your location.").setTitle("Really, dude?").setPositiveButton(
                            "I'll consider it", null).create().show();
                }else{  //If the permission was permanently denied
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("You don't want to be tracked, that's cool. Just don't expect anything from me!").setTitle(
                            "Okay Edward Snowden").setPositiveButton("Now you see me, now you don't",
                                                                     null).create().show();
                }
            }
        }
    }

    public void viewFullImage(View view){
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

        if(this.originalName == null){  //We're creating a new incident
            try{
                MainActivity.db.addIncident(thisIncident);
            }catch(IncidentAlreadyExistsException e){   //If the user uses a duplicate name
                Snackbar.make(findViewById(R.id.addincident), "Use a different name, this one already exists",
                              Snackbar.LENGTH_INDEFINITE)
                        .show();
            }catch(Exception e){
                Snackbar.make(findViewById(R.id.addincident), "Something went horribly wrong.", Snackbar.LENGTH_INDEFINITE)
                        .show();
            }
        }else{  //If this activity was started from pre-existing incident
            try{
                db.updateIncident(thisIncident, this.originalName);
            }catch(Exception e){    //More than likely, incident doesn't already exist, so originalName is wrong
                try{
                    db.addIncident(thisIncident);
                }catch(Exception ee){   //If incident can neither be added nor updated
                    Snackbar.make(findViewById(R.id.addincident), "Something went horribly wrong.", Snackbar.LENGTH_INDEFINITE)
                            .show();
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
        if(MainActivity.db == null){
            Toast.makeText(this, "Couldn't access database", Toast.LENGTH_LONG).show();
            finish();
        }

        try{
            MainActivity.db.removeIncident(this.originalName);
        }catch(Exception e){
            Toast.makeText(this, "Couldn't delete", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        LinearLayout theImages = findViewById(R.id.uploaded_images_layout);
        ImageView theImage = new ImageView(this);
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
// Get the dimensions of the View
//            int targetW = theImage.getWidth();
//            int targetH = theImage.getHeight();

            // Get the dimensions of the imageBitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
//            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
//            bmOptions.inSampleSize = scaleFactor;

            Bitmap imageBitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
            theImage.setImageBitmap(imageBitmap);
            theImages.addView(theImage);
            //This will allow the image to fill the space allotted
            theImage.setLayoutParams(
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                  LinearLayout.LayoutParams.MATCH_PARENT));

//                    findViewById(R.id.uploaded_images).setMinimumHeight(imageBitmap.getHeight());
//                    findViewById(R.id.uploaded_images_layout).setMinimumHeight(imageBitmap.getHeight());
            //Update view
            theImage.setAdjustViewBounds(true);

            //Add an animation for the image to fade into the scene
            Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
            theImage.startAnimation(aniFade);

            thisIncident.addImage(imageBitmap);

            //TODO Make image full screen when clicked upon
            theImage.setOnClickListener(new OpenImageListener(this, imageBitmap));
        }
    }
}

/**
 * Corresponds to clicking on an image in the scroll view
 */
class OpenImageListener implements View.OnClickListener{

    private AddIncidentActivity callingActivity;

    OpenImageListener(AddIncidentActivity callingActivity, Bitmap imageBitmap){
        this.callingActivity = callingActivity;
    }

    @Override
    public void onClick(View v){
    }
//        Toast.makeText(callingActivity.getApplicationContext(), "It works", Toast.LENGTH_LONG).show();

}
