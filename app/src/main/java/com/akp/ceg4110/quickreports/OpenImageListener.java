package com.akp.ceg4110.quickreports;

import android.graphics.Bitmap;
import android.view.View;

/**
 * Corresponds to clicking on an image in the scroll view
 */
public class OpenImageListener implements View.OnClickListener{

    private AddIncidentActivity callingActivity;

    public OpenImageListener(AddIncidentActivity callingActivity, Bitmap imageBitmap){
        this.callingActivity = callingActivity;
    }

    @Override
    public void onClick(View v){
    }
//        Toast.makeText(callingActivity.getApplicationContext(), "It works", Toast.LENGTH_LONG).show();

}
