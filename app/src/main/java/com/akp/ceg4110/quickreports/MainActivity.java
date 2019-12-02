package com.akp.ceg4110.quickreports;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{

    ArrayList<Incident> theIncidents;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        final DatabaseAccessor db = new DatabaseAccessor(this.openOrCreateDatabase(DatabaseAccessor.DATABASE_NAME, MODE_PRIVATE, null));

        try{
            db.addIncident(new Incident("wefioajoij"));
        }catch(IncidentAlreadyExistsException e){
            e.printStackTrace();
        }

        FloatingActionButton fab = findViewById(R.id.add_incident);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, AddIncidentActivity.class);
                intent.putExtra("the_database", db);
                startActivity(intent);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.list_of_incidents);
        theIncidents = (ArrayList<Incident>)db.getAllIncidents();   //Fill list with incidents
        recyclerView.setAdapter(new com.akp.ceg4110.quickreports.IncidentsAdapter(theIncidents));   //Set adapter to created list
        recyclerView.setLayoutManager(new LinearLayoutManager(this));   //Create a layout
    }
}
