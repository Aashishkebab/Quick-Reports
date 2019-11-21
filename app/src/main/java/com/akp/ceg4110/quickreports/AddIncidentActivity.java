package com.akp.ceg4110.quickreports;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.akp.ceg4110.quickreports.ui.addincident.AddIncidentFragment;

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
}
