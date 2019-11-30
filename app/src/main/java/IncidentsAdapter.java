package com.akp.ceg4110.quickreports;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akp.ceg4110.quickreports.Incident;
import com.akp.ceg4110.quickreports.R;

import java.util.List;

public class IncidentsAdapter extends RecyclerView.Adapter<IncidentsAdapter.ViewHolder>{

    List<Incident> incidents;

    public IncidentsAdapter(List<Incident> incidents){
        this.incidents = incidents;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View incidentTile = inflater.inflate(R.layout.incident_tile, parent, false);

        // Return a new holder instance
        return new ViewHolder(incidentTile);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position){
        // Get the data model based on position
        Incident incident = incidents.get(position);

        // Set item views based on your views and data model
        holder.titleTextView.setText(incident.getName());
        if(incident.getDescription() == null){
            holder.descriptionTextView.setText(incident.getDescription());
        }else{
            holder.descriptionTextView.setText("");
        }

    }

    @Override
    public int getItemCount(){
        return incidents.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView titleTextView, descriptionTextView;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            this.titleTextView = (TextView)itemView.findViewById(R.id.incident_title_main);
            this.descriptionTextView = (TextView)itemView.findViewById(R.id.incident_description_main);
        }
    }
}
