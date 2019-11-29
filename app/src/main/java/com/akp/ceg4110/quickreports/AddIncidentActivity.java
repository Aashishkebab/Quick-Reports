package com.akp.ceg4110.quickreports;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.akp.ceg4110.quickreports.ui.addincident.AddIncidentFragment;

import java.util.ArrayList;

public class AddIncidentActivity extends AppCompatActivity{

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_incident_activity);
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                                       .replace(R.id.container, AddIncidentFragment.newInstance())
                                       .commitNow();
            ActivityCompat.requestPermissions(this,
                                              new String[]{ Manifest.permission.CAMERA}, 2);
        }
    }

    public void dispatchTakePictureIntent(View view){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null){
            try{
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }catch(SecurityException e){
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
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
        }
    }
}
