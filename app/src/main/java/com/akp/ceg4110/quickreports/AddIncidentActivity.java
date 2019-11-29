package com.akp.ceg4110.quickreports;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.akp.ceg4110.quickreports.ui.addincident.AddIncidentFragment;

public class AddIncidentActivity extends AppCompatActivity{

    static final int REQUEST_IMAGE_CAPTURE = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_incident_activity);
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                                       .replace(R.id.container, AddIncidentFragment.newInstance())
                                       .commitNow();
        }
    }

    /**
     * Onclick for trying to take a picture
     * @param view
     */
    public void dispatchTakePictureIntent(View view){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null){
            try{
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                   != PackageManager.PERMISSION_GRANTED){   //If permission is not granted
                    ActivityCompat.requestPermissions(this, //Request permission
                                                      new String[]{ Manifest.permission.CAMERA }, REQUEST_IMAGE_CAPTURE);
                }else{
                    //Take picture
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }catch(SecurityException e){    //This shouldn't occur, but just in case it does
//                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults){
        if(requestCode == REQUEST_IMAGE_CAPTURE){// If request is cancelled, the result arrays are empty.
            if(grantResults.length > 0
               && grantResults[ 0 ] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getApplicationContext(), "Now try taking your picture again!", Toast.LENGTH_LONG).show();
            }else{
                //If user temporarily denied
                if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    //Chain together a whole number because laziness, and show an alert
                    builder.setMessage(
                            "Look, you tried to take a picture, but then you didn't let me do that.\nYou are the epitome of " +
                            "oxyMORON.")
                           .setTitle("Why must you be so difficult?").setPositiveButton("Whatever", null).create().show();
                }else{  //If the permission was permanently denied
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(
                            "The camera cannot be used if the permissible permission permitting the usage of the camera, which " +
                            "is a camera and also so happens to be a camera, is denied in a method that creates a denial of " +
                            "such a permissible permission that permits the accessible accessing of the camera.")
                           .setTitle("Camera permission has been denied!").setPositiveButton("Yee", null).create().show();
                }
            }
        }
    }

    public void viewFullImage(View view){
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap)extras.get("data");
            LinearLayout theImages = findViewById(R.id.uploaded_images_layout);
            ImageView theImage = new ImageView(this);
            theImage.setImageBitmap(imageBitmap);
            theImages.addView(theImage);
            theImage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            theImage.setAdjustViewBounds(true);
            Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
            theImage.startAnimation(aniFade);

            theImage.setOnClickListener(new OpenImageListener(this, imageBitmap));
        }
    }
}

class OpenImageListener implements View.OnClickListener{

    AddIncidentActivity callingActivity;

    OpenImageListener(AddIncidentActivity callingActivity, Bitmap imageBitmap){
        this.callingActivity = callingActivity;
    }

    @Override
    public void onClick(View v){

//        Toast.makeText(callingActivity.getApplicationContext(), "It works", Toast.LENGTH_LONG).show();

    }
}
