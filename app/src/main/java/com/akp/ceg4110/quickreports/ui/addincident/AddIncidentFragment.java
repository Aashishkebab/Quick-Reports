package com.akp.ceg4110.quickreports.ui.addincident;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.akp.ceg4110.quickreports.R;

public class AddIncidentFragment extends Fragment{

    private AddIncidentViewModel mViewModel;

    public static AddIncidentFragment newInstance(){
        return new AddIncidentFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        return inflater.inflate(R.layout.add_incident_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(AddIncidentViewModel.class);
        // TODO: Use the ViewModel
    }

}
