package com.akp.ceg4110.quickreports;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{

    static final int INCIDENT_MODIFIED = 5;
    public static DatabaseAccessor db;
    ArrayList<Incident> theIncidents;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        MainActivity.db = new DatabaseAccessor(this.openOrCreateDatabase(DatabaseAccessor.DATABASE_NAME, MODE_PRIVATE, null));

//        try{
//            Incident testy = new Incident("theTestyTest");
//            testy.setDescription("fweoijfew");
//            testy.setName("Hello");
//            db.addIncident(testy);
//        }catch(IncidentAlreadyExistsException e){
//            e.printStackTrace();
//        }

        FloatingActionButton fab = findViewById(R.id.add_incident);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){ //Onclick for the add button
                Intent intent = new Intent(MainActivity.this, AddIncidentActivity.class);
                startActivityForResult(intent, INCIDENT_MODIFIED);
            }
        });

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);   //Set the app to change theme based on time

        refreshIncidentsRecycler();
    }

    public void openIncident(View view){
        Intent intent = new Intent(MainActivity.this, AddIncidentActivity.class);
        intent.putExtra("incident_name", ((TextView)((LinearLayout)view).getChildAt(0)).getText());
        try{
            startActivityForResult(intent, INCIDENT_MODIFIED);
        }catch(Exception e){
            Toast.makeText(this, "couldn't open incident", Toast.LENGTH_LONG).show();
        }
    }

    private void refreshIncidentsRecycler(){
        RecyclerView recyclerView = findViewById(R.id.list_of_incidents);
        theIncidents = (ArrayList<Incident>)db.getAllIncidents();   //Fill list with incidents
        recyclerView.setAdapter(new com.akp.ceg4110.quickreports.IncidentsAdapter(theIncidents));   //Set adapter to created list
        recyclerView.setLayoutManager(new LinearLayoutManager(this));   //Create a layout
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == INCIDENT_MODIFIED){
            try{
                refreshIncidentsRecycler();
            }catch(Exception e){
                Toast.makeText(this, "Failed to refresh", Toast.LENGTH_LONG).show();
            }
        }
    }
}
