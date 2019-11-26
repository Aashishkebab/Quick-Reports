package com.akp.ceg4110.quickreports;

import androidx.appcompat.app.AppCompatActivity;

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

import com.akp.ceg4110.quickreports.ui.addincident.AddIncidentFragment;

import java.util.ArrayList;

public class AddIncidentActivity extends AppCompatActivity{

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

    public void pickFromGallery(View view){
        //Create an Intent with action as ACTION_PICK
        Intent intent = new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = { "image/jpeg", "image/png" };
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        // Launching the Intent
        startActivityForResult(intent, 123);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        // Result code is RESULT_OK only if the user selects an Image
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == 123){//data.getData return the content URI for the selected Image
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();
                //Get the column index of MediaStore.Images.Media.DATA
                int columnIndex = cursor.getColumnIndex(filePathColumn[ 0 ]);
                //Gets the String value in the column
                String imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                // Set the Image in ImageView after decoding the String
                LinearLayout theImages = findViewById(R.id.uploaded_images_layout);
                ImageView anImage = new ImageView(this);
                anImage.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
                theImages.addView(anImage);

            }
        }
    }
}
