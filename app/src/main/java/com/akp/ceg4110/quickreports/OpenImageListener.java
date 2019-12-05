package com.akp.ceg4110.quickreports;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;

/**
 * Corresponds to clicking on an image in the scroll view
 */
public class OpenImageListener implements View.OnClickListener{

    private String path;
    private AddIncidentActivity activity;

    public OpenImageListener(String path, AddIncidentActivity activity){
        this.path = path;
        this.activity = activity;
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
