package com.akp.ceg4110.quickreports.ui.addincident;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
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
            GridLayout theImagesLayout = view.findViewById(R.id.uploaded_images_layout);

            //Get the display size
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;

            theImage.setMaxWidth(width / 3 - 20);    //Show images at 1/3rd the size for three columns
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                                             LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(15, 19, 0, 0);
            theImage.setLayoutParams(params);

            theImagesLayout.addView(theImage);

            theImage.setAdjustViewBounds(true);
            theImage.setOnClickListener(new OpenImageListener(theImages.get(i), (AddIncidentActivity)getActivity()));
        }
    }

}
