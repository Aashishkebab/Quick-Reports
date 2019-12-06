package com.akp.ceg4110.quickreports.ui.addincident;

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
                // Figure out if taller or wider
                if(imageBitmap.getHeight() == Math.max(imageBitmap.getHeight(), imageBitmap.getWidth())){   //If taller

                    // Scale width to be size we need when displaying (which just so happens to be width / 3 - 20)
                    // Then, scale down height by the same factor/ratio so that image isn't stretched.
                    // To do this, we figure out how by how much we divided the width, then divide height by that amount
                    // This gives a really good approximation, but is off due to integer arithmetic being lossy
                    // We could cast to doubles before doing the math, but that wouldn't serve any practical purpose
                    imageBitmap = Bitmap.createScaledBitmap(imageBitmap, width / 3 - 20,
                                                            imageBitmap.getHeight() / (imageBitmap.getWidth() / (width / 3 - 20)),
                                                            false);
                }else{  // If wider than tall
                    // Same process as above, but using height as the reference and scaling width accordingly
                    imageBitmap = Bitmap
                            .createScaledBitmap(imageBitmap, imageBitmap.getWidth() / (imageBitmap.getHeight() / (width / 3 - 20)),
                                                width / 3 - 20, false);
                }

                // Crop the newly resized image
                // Whichever was the smaller of the height and width will end up not having any crop because math
                // We're doing the width and height each minus the same width / 3 - 20 factor as above, which is why
                // The third and fourth parameters in this method call are the crop resolution/size
                // For the other dimension, we wish to crop so that the *middle* part of that dimension is displayed
                // We need to calculate the starting position, so we do that dimension (width or height) - (width / 3 - 20)
                // Basically, we subtract off the amount we are cropping from the total width and height
                // That gives us the *remaining* area, which we then divide by 2 to center it
                // Dividing by 2 will shift the crop so that there's an equal amount of leftover before and after
                imageBitmap = Bitmap.createBitmap(imageBitmap, (imageBitmap.getWidth() - (width / 3 - 20)) / 2,
                                                  (imageBitmap.getHeight() - (width / 3 - 20)) / 2, width / 3 - 20, width / 3 - 20);

            }catch(Exception e){  //Just use the full images
                if(!AddIncidentActivity.warnLag){
                    Snackbar.make(view.findViewById(R.id.addincident), "Images can't be resized, phone may stutter",
                                  Snackbar.LENGTH_LONG)
                            .show();
                    AddIncidentActivity.warnLag = true;
                }
            }

            theImage.setImageBitmap(imageBitmap);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                                             LinearLayout.LayoutParams.WRAP_CONTENT);
            // We are using a left-margin of 15 separate items in the grid
            // Our grid holds three items per row (three columns), so we must divide the width of the screen by 3
            // "width" is declared above as the screen width in pixels
            // However, to compensate for the margin of 15 and prevent overflowing off the screen,
            // we must subtract that margin from each image width
            // However, this means the last image would end at the screen border, which would be uneven.
            // So we add 15 / 3 to that number, since there are 3 items
            // This way, since each image is 5 pixels smaller, it is overall 15 pixels for the entire row of 3 images
            // This will thus leave a gap of 15 at the end, which is the same as the margin, creating a uniform appearance
            params.setMargins(15, 19, 0, 0);
            theImage.setLayoutParams(params);

            theImage.setMaxWidth(width / 3 - 20);    // Show images at 1/3rd the size for three columns - see above

            params.setMargins(15, 19, 0, 0);
            theImage.setLayoutParams(params);

            theImagesLayout.addView(theImage);

            theImage.setAdjustViewBounds(true);
            theImage.setOnClickListener(new OpenImageListener(theImages.get(i), (AddIncidentActivity)getActivity()));
        }
    }

}
