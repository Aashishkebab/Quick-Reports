package com.akp.ceg4110.quickreports.ui.addincident;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.akp.ceg4110.quickreports.AddIncidentActivity;
import com.akp.ceg4110.quickreports.ImageProcessor;
import com.akp.ceg4110.quickreports.Incident;
import com.akp.ceg4110.quickreports.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class AddIncidentFragment extends Fragment{

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

        ((Button)view.findViewById(R.id.add_picture_layout_button)).setText(R.string.add_image);
        view.findViewById(R.id.add_picture_layout_button).setEnabled(true);

        fillImagesInPage(view);
    }

    private void fillImagesInPage(View view){
        GridLayout theImagesLayout = view.findViewById(R.id.uploaded_images_layout);
        theImagesLayout.removeAllViews();

        ArrayList<ImageView> imageViews = new ArrayList<>();
        for(int i = 0; i < theIncident.getImages().size(); i++){
            imageViews.add(new ImageView(getActivity()));
        }

        view.findViewById(R.id.add_picture_layout_button).setEnabled(false);
        ((Button)view.findViewById(R.id.add_picture_layout_button)).setText(R.string.loading_images);
        new ImageRenderer(getContext(), getActivity(), theIncident, imageViews, theImagesLayout).execute(theImagesLayout);
    }
}

class ImageRenderer extends AsyncTask{

    @SuppressLint("StaticFieldLeak")
    private final Context context;
    @SuppressLint("StaticFieldLeak")
    private final Activity activity;
    int height, width;
    private ArrayList<ImageView> theImageViews;
    private Incident theIncident;
    @SuppressLint("StaticFieldLeak")
    private GridLayout theImagesLayout;
    private short leftMargin;   // Ideally should be a multiple of all gridSizes used
    private short numberOfColumns; // The number of columns
    private short sizeOffset;   // Formula: leftMargin + leftMargin / numberOfColumns

    ImageRenderer(Context context, Activity activity, Incident theIncident, ArrayList<ImageView> theImageViews,
                  GridLayout theImagesLayout){
        this.context = context;
        this.activity = activity;
        this.theIncident = theIncident;
        this.theImageViews = theImageViews;
        this.theImagesLayout = theImagesLayout;
    }

    @Override
    protected void onPreExecute(){
        // Get the display size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;   // Height of screen
        width = displayMetrics.widthPixels; // Width of screen

        leftMargin = 15;

        if(width > height){ // In landscape
            numberOfColumns = 5;
        }else{  // In portrait, or square screen
            numberOfColumns = 3;
        }
        theImagesLayout.setColumnCount(numberOfColumns);

        sizeOffset = (short)(leftMargin + (leftMargin / numberOfColumns));
    }

    @Override
    protected ArrayList<Bitmap> doInBackground(Object[] objects){
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = false;

        ArrayList<String> theImages = (ArrayList<String>)theIncident.getImages();
        if((!((AddIncidentActivity)activity).verticalImagesHaveBeenRendered && height >= width) ||
           (!((AddIncidentActivity)activity).horizontalImagesHaveBeenRendered) &&
           width > height){    // If the images have not already been rendered
            for(int i = 0; i < theImages.size(); i++){
                Bitmap imageBitmap = BitmapFactory.decodeFile(theImages.get(i), bmOptions);

                try{
                    if(width > height){
                        ((AddIncidentActivity)activity).theHorizontalBitmaps.add(ImageProcessor.scaleImage(height, width, sizeOffset,
                                                                                                           leftMargin,
                                                                                                           numberOfColumns,
                                                                                                           imageBitmap,
                                                                                                           (GridLayout)objects[ 0 ],
                                                                                                           theImageViews.get(i),
                                                                                                           activity));
                        ((AddIncidentActivity)activity).horizontalImagesHaveBeenRendered = true;
                    }else{
                        ((AddIncidentActivity)activity).theVerticalBitmaps.add(ImageProcessor.scaleImage(height, width, sizeOffset,
                                                                                                         leftMargin,
                                                                                                         numberOfColumns,
                                                                                                         imageBitmap,
                                                                                                         (GridLayout)objects[ 0 ],
                                                                                                         theImageViews.get(i),
                                                                                                         activity));
                        ((AddIncidentActivity)activity).verticalImagesHaveBeenRendered = true;
                    }
                }catch(Exception e){
                    if(!AddIncidentActivity.warnLag){
                        Snackbar.make(activity.findViewById(R.id.addincident), "Images can't be resized, phone may lag",
                                      Snackbar.LENGTH_LONG).show();
                        AddIncidentActivity.warnLag = true;
                    }
                }
            }
        }

        if(width > height){
            return ((AddIncidentActivity)activity).theHorizontalBitmaps;
        }else{
            return ((AddIncidentActivity)activity).theVerticalBitmaps;
        }
    }

    @Override
    protected void onPostExecute(Object imageBitmaps){
        for(int i = 0; i < ((ArrayList<Bitmap>)imageBitmaps).size(); i++){
            theImageViews.get(i).setImageBitmap(((ArrayList<Bitmap>)imageBitmaps).get(i));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                                                             LinearLayout.LayoutParams.WRAP_CONTENT);
//            // We are using a left-margin of 15 separate items in the grid
//            // Our grid holds three items per row (three columns), so we must divide the width of the screen by 3
//            // "width" is declared above as the screen width in pixels
//            // However, to compensate for the margin of 15 and prevent overflowing off the screen,
//            // we must subtract that margin from each image width
//            // However, this means the last image would end at the screen border, which would be uneven.
//            // So we add 15 / 3 to that number, since there are 3 items
//            // This way, since each image is 5 pixels smaller, it is overall 15 pixels for the entire row of 3 images
//            // This will thus leave a gap of 15 at the end, which is the same as the margin, creating a uniform appearance
            params.setMargins(leftMargin, 0, 0, 19);
//            params.height = width / numberOfColumns - sizeOffset;
//            params.width = width / numberOfColumns - sizeOffset;
            theImageViews.get(i).setLayoutParams(params);

            theImageViews.get(i).setMaxWidth(width / numberOfColumns - sizeOffset);    // Show images at 1/3rd the size for three columns
            // Note: The height should automatically be the same as the width, so no need to set it

            // Note: This MUST come *after* setLayoutParams, or bad things will happen!
            theImagesLayout.addView(theImageViews.get(i));

            //Update view
            theImageViews.get(i).setAdjustViewBounds(true);

            theImageViews.get(i)
                         .setOnClickListener(new ImageProcessor(theIncident.getImages().get(i), (AddIncidentActivity)activity));

            Animation animation = AnimationUtils.loadAnimation(context, R.anim.fade_in);
            theImageViews.get(i).startAnimation(animation);
        }
        try{
            ((Button)activity.findViewById(R.id.add_picture_layout_button)).setText(R.string.add_image);
            activity.findViewById(R.id.add_picture_layout_button).setEnabled(true);
        }catch(NullPointerException ignored){}
    }
}
