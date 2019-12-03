package com.akp.ceg4110.quickreports.ui.addincident;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.akp.ceg4110.quickreports.Incident;
import com.akp.ceg4110.quickreports.R;

public class AddIncidentFragment extends Fragment{

    private AddIncidentViewModel mViewModel;
    private Incident theIncident;

    public static AddIncidentFragment newInstance(Incident theIncident){
        AddIncidentFragment fragment = new AddIncidentFragment();
        fragment.theIncident = theIncident;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.add_incident_fragment, container, false);

        if(this.theIncident.getName() != null){
            fillPage(view);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(AddIncidentViewModel.class);
        // TODO: Use the ViewModel
    }

    private void fillPage(View view){  //The View is only in there for the onClick I created for testing purposes
        ((TextView)view.findViewById(R.id.enter_incident_name_textview)).setText(theIncident.getName());

        theIncident.setDescription("");
        ((TextView)view.findViewById(R.id.enter_incident_description_textview)).setText(theIncident.getDescription());

        if(!theIncident.getName().equals("")){
            view.findViewById(R.id.delete_incident).setVisibility(View.VISIBLE);
        }

    }

}
