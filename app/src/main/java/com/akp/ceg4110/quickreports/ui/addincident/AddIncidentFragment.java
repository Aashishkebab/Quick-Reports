package com.akp.ceg4110.quickreports.ui.addincident;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.akp.ceg4110.quickreports.AddIncidentActivity;
import com.akp.ceg4110.quickreports.Incident;
import com.akp.ceg4110.quickreports.OpenImageListener;
import com.akp.ceg4110.quickreports.R;

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
        return view;
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

        if(!theIncident.getName().equals("")){
            view.findViewById(R.id.delete_incident_button).setVisibility(View.VISIBLE);
        }

        ((TextView)view.findViewById(R.id.weather_textview)).setText(theIncident.getWeather());

        ArrayList<String> theImages = (ArrayList<String>)theIncident.getImages();
        for(int i = 0; i < theImages.size(); i++){
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            bmOptions.inJustDecodeBounds = false;
            Bitmap imageBitmap = BitmapFactory.decodeFile(theImages.get(i), bmOptions);

            ImageView theImage = new ImageView(getActivity());

            theImage.setImageBitmap(imageBitmap);
            LinearLayout theImagesLayout = view.findViewById(R.id.uploaded_images_layout);
            theImagesLayout.addView(theImage);

            theImage.setLayoutParams(
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

            theImage.setAdjustViewBounds(true);

            theImage.setOnClickListener(new OpenImageListener((AddIncidentActivity)getActivity(), imageBitmap));
        }
    }

}
