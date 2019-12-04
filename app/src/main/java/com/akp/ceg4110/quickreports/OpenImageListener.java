package com.akp.ceg4110.quickreports;

import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

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
        activity.startActivity(intent);
    }
}
