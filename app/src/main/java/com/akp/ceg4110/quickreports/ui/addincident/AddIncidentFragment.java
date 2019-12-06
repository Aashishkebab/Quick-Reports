package com.akp.ceg4110.quickreports.ui.addincident;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.akp.ceg4110.quickreports.AddIncidentActivity;
import com.akp.ceg4110.quickreports.ImageLayoutManager;
import com.akp.ceg4110.quickreports.Incident;
import com.akp.ceg4110.quickreports.ImageLayoutManager;
import com.akp.ceg4110.quickreports.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

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

        this.setRetainInstance(true);
        return view;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        fillImagesInPage(getView());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(AddIncidentViewModel.class);
        // TODO: Use the ViewModel
    }

    private void fillPage(View view){

        ((TextView)view.findViewById(R.id.enter_incident_name_textview)).setText(theIncident.getName());

        ((TextView)view.findViewById(R.id.enter_incident_description_textview)).setText(theIncident.getDescription());

        if(!theIncident.getName().equals("")){  //If editing old incident
            view.findViewById(R.id.delete_incident_button).setVisibility(View.VISIBLE);
            view.findViewById(R.id.get_weather_button).setEnabled(false);
            if(theIncident.getWeather().equals("")){
                ((Button)view.findViewById(R.id.get_weather_button)).setText(R.string.weather_cannot_be_mod);
            }else{
                ((Button)view.findViewById(R.id.get_weather_button)).setText(R.string.weather_already_set);
                view.findViewById(R.id.weather_textview).setVisibility(View.VISIBLE);
            }
        }

        ((TextView)view.findViewById(R.id.weather_textview)).setText(theIncident.getWeather());

        fillImagesInPage(view);
    }

    private void fillImagesInPage(View view){
        ((GridLayout)view.findViewById(R.id.uploaded_images_layout)).removeAllViews();

        ArrayList<String> theImages = (ArrayList<String>)theIncident.getImages();
        for(int i = 0; i < theImages.size(); i++){
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();

            bmOptions.inJustDecodeBounds = false;
            Bitmap imageBitmap = BitmapFactory.decodeFile(theImages.get(i), bmOptions);

            ImageView theImage = new ImageView(getActivity());

            theImage.setImageBitmap(imageBitmap);
            GridLayout theImagesLayout = view.findViewById(R.id.uploaded_images_layout);

            // Get the display size
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;   // Height of screen
            int width = displayMetrics.widthPixels; // Width of screen
            try{
                ImageLayoutManager.addImageToLayout(height, width, imageBitmap, theImagesLayout, theImage);
            }catch(Exception e){  //Just use the full images
                if(!AddIncidentActivity.warnLag){
                    Snackbar.make(view.findViewById(R.id.addincident), "Images can't be resized, phone may lag",
                                  Snackbar.LENGTH_LONG)
                            .show();
                    AddIncidentActivity.warnLag = true;
                }
            }

            theImage.setOnClickListener(new ImageLayoutManager(theImages.get(i), (AddIncidentActivity)getActivity()));
        }
    }

}
