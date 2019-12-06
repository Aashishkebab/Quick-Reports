package com.akp.ceg4110.quickreports;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;

/**
 * Corresponds to clicking on an image in the scroll view
 */
public class ImageLayoutManager implements View.OnClickListener{

    private String path;
    private AddIncidentActivity activity;

    public ImageLayoutManager(String path, AddIncidentActivity activity){
        this.path = path;
        this.activity = activity;
    }

    public static Bitmap addImageToLayout(int height, int width, short sizeOffset, short leftMargin,
                                          short numberOfColumns, Bitmap imageBitmap, GridLayout theImagesLayout, ImageView theImage,
                                          Activity activity){

        // Figure out if taller or wider
        if(imageBitmap.getHeight() == Math.max(imageBitmap.getHeight(), imageBitmap.getWidth())){   //If image taller than wide

            // Scale width to be size we need when displaying (which just so happens to be width / 3 - 20)
            // Then, scale down height by the same factor/ratio so that image isn't stretched.
            // To do this, we figure out how by how much we divided the width, then divide height by that amount
            // This gives a really good approximation, but is off due to integer arithmetic being lossy
            // We could cast to doubles before doing the math, but that wouldn't serve any practical purpose
            imageBitmap = Bitmap.createScaledBitmap(imageBitmap, width / numberOfColumns - sizeOffset, imageBitmap.getHeight() /
                                                                                                       (imageBitmap.getWidth() /
                                                                                                        (width / numberOfColumns -
                                                                                                         sizeOffset)), false);
        }else{  // If wider than tall
            // Same process as above, but using height as the reference and scaling width accordingly
            imageBitmap = Bitmap
                    .createScaledBitmap(imageBitmap,
                                        imageBitmap.getWidth() / (imageBitmap.getHeight() / (width / numberOfColumns - sizeOffset)),
                                        width / numberOfColumns - sizeOffset, false);
        }

        // Crop the newly resized image
        // Whichever was the smaller of the height and width will end up not having any crop because math
        // We're doing the width and height each minus the same width / 3 - 20 factor as above, which is why
        // The third and fourth parameters in this method call are the crop resolution/size
        // For the other dimension, we wish to crop so that the *middle* part of that dimension is displayed
        // We need to calculate the starting position, so we do that dimension (width or height) - (width / 3 - 20)
        // Basically, we subtract off the amount we are cropping from the total width and height
        // That gives us the *remaining* area, which we then divide by 2 to center it
        // Dividing by 2 will shift the crop so that there's an equal amount of leftover before and after
        imageBitmap = Bitmap.createBitmap(imageBitmap, (imageBitmap.getWidth() - (width / numberOfColumns - sizeOffset)) / 2,
                                          (imageBitmap.getHeight() - (width / numberOfColumns - sizeOffset)) / 2,
                                          width / numberOfColumns - sizeOffset, width / numberOfColumns - sizeOffset);
        return imageBitmap;
    }

    @Override
    public void onClick(View v){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(this.path), "image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        Toast.makeText(activity.getApplicationContext(), "Please choose Google Photos if available", Toast.LENGTH_LONG).show();
        try{
            activity.getPackageManager().getPackageInfo("com.google.android.apps.photos", 0);
            intent.setPackage("com.google.android.apps.photos");
            activity.startActivity(intent);

        }catch(PackageManager.NameNotFoundException e){
            activity.startActivity(Intent.createChooser(intent, "Choose the appropriate photos app (some apps may not work)"));
        }
    }
}
