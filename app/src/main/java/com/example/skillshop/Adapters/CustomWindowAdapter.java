package com.example.skillshop.Adapters;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.skillshop.Models.Query;
import com.example.skillshop.Models.Ratings;
import com.example.skillshop.Models.Workshop;
import com.example.skillshop.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CustomWindowAdapter implements GoogleMap.InfoWindowAdapter {

    TextView tvClassname;
    ImageView ivClassImage;
    TextView tvInstructorName;
    RatingBar rbInstructorAverage;
    TextView tvClassDate;

    LayoutInflater mInflater;

    public CustomWindowAdapter(LayoutInflater i){
        mInflater = i;
    }

    // This defines the contents within the info window based on the marker
    @Override
    public View getInfoContents(Marker marker) {

        // Getting view from the layout file
        View view = mInflater.inflate(R.layout.custom_info_window, null);
        findViewsById(view);
        Query workshopQuery = new Query();
        // query add all classes with all data and sort by time of class and only show new classes
        workshopQuery.getAllClasses().withItems().getClassById(marker.getTitle());

        try {
            List<Workshop> singletonWorkshop = workshopQuery.find();
            Workshop workshop = singletonWorkshop.get(0);
            populateViews(workshop);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Return info window contents
        return view;
    }

    private void findViewsById(View view) {
        tvClassname = view.findViewById(R.id.tvClassNameMV);
        tvInstructorName = view.findViewById(R.id.tvInstructorNameMV);
        rbInstructorAverage = view.findViewById(R.id.rbInstructorAverage);
        tvClassDate = view.findViewById(R.id.tvClassDateMV);
        ivClassImage = view.findViewById(R.id.ivClassImage);
    }

    private void populateViews(Workshop workshop) {
        tvClassname.setText(workshop.getName());
        ParseUser teacher = workshop.getTeacher();
        Ratings.Query ratingParseQuery = new Ratings.Query();
        ratingParseQuery.getAllRatings().whereEqualTo("user", teacher);

        try {
            List<Ratings> ratingsList = ratingParseQuery.find();
            Ratings userRating = ratingsList.get(0);
            rbInstructorAverage.setRating((int) userRating.getAverageRating());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(teacher != null && teacher.getString("firstName")!=null && teacher.getString("lastName")!=null){
             tvInstructorName.setText(teacher.getString("firstName")+" "+teacher.getString("lastName"));
        }

        switch (workshop.getCategory()) {

            case "Culinary":
                ivClassImage.setImageResource(R.drawable.cooking);
                break;

            case "Education":
                ivClassImage.setImageResource(R.drawable.education);
                break;
            case "Fitness":
                ivClassImage.setImageResource(R.drawable.fitness);
                break;
            case "Arts/Crafts":
                ivClassImage.setImageResource(R.drawable.arts);
                break;

            case "Other":
                ivClassImage.setImageResource(R.drawable.misc);
                break;

            default:
                break;
        }
        tvClassDate.setText(getRelativeTimeAgo(workshop.getDate()));


    }

    // This changes the frame of the info window; returning null uses the default frame.
    // This is just the border and arrow surrounding the contents specified above
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}