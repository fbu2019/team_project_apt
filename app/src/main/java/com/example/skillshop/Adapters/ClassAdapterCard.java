package com.example.skillshop.Adapters;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.example.skillshop.Models.Ratings;
import com.example.skillshop.ClassDescription.ClassDetailsActivity;
import com.example.skillshop.ClassDescription.EditClassActivity;
import com.example.skillshop.Models.Workshop;
import com.example.skillshop.R;
import com.google.android.gms.maps.GoogleMap;
import com.parse.ParseGeoPoint;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ClassAdapterCard extends RecyclerView.Adapter<ClassAdapterCard.ViewHolder>  {


    private static List<Workshop> mWorkshops;
    private Context context;

    //pass in the Posts array in the constructor
    public ClassAdapterCard(ArrayList<Workshop> workshops, Context context) {
        this.mWorkshops = workshops;
        this.context = context;
    }
    //for each row, inflate the layout and cache references into the ViewHolder
    //called when new rows are created
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class_card, parent, false);
        return new ViewHolder(itemView);
    }

    //bind the values based on the position of the element
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //get the data according to position
        final Workshop tWorkshop = mWorkshops.get(position);
        //populate the views according to this data
        holder.bind(tWorkshop);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Intent profileDetailsIntent = new Intent(context, ClassDetailsActivity.class);
                //pass in class that was selected
                profileDetailsIntent.putExtra(Workshop.class.getSimpleName(), Parcels.wrap(tWorkshop));

                if (tWorkshop.isTeacher()) {
                    profileDetailsIntent.putExtra("IsTeacher", Parcels.wrap(true));
                }else{
                    profileDetailsIntent.putExtra("IsTeacher", Parcels.wrap(false));
                }
                context.startActivity(profileDetailsIntent);
            }
        });
    }

    //gets the number of items
    @Override
    public int getItemCount() {
        return mWorkshops.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView ivClassIcon;
        private TextView tvClassName;
        private TextView tvDescription;
        private TextView tvDate;
        private TextView tvTime;
        private TextView tvCost;
        private ImageView ivTeacherBadge;
        private ImageButton ibDirections;
        private RatingBar rbInstructorRating;

        public ViewHolder(View itemView) {
            super(itemView);
            findAllViews();
        }

        private void findAllViews() {
            //perform findViewById lookups by id in the xml file
            ivClassIcon = itemView.findViewById(R.id.ivClassIcon);
            tvClassName = itemView.findViewById(R.id.tvClassName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvCost =  itemView.findViewById(R.id.tvCost);
            ivTeacherBadge = itemView.findViewById(R.id.ivTeacherBadge);
            ibDirections = itemView.findViewById(R.id.ibDirections);
            rbInstructorRating = itemView.findViewById(R.id.ratingBar);
        }


        public void bind(final Workshop tWorkshop) {

            setAllViews(tWorkshop);
        }

        private void setAllViews(Workshop tWorkshop) {
            Log.e("ERROR MESSAGE ABOVE", tWorkshop.getName()+" K");
            tvClassName.setText(tWorkshop.getName());
            Log.e("ERROR MESSAGE HERE", tWorkshop.getName());


            ParseUser teacher = tWorkshop.getTeacher();
            if(teacher.getString("firstName")!=null && teacher.getString("lastName")!=null){
                tvDescription.setText(tWorkshop.getDescription());
            }

            if(tWorkshop.isTeacher())
            {
                ivTeacherBadge.setVisibility(View.VISIBLE);
                ivTeacherBadge.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Intent editClassIntent = new Intent(context, EditClassActivity.class);
                        //pass in class that was selected
                        editClassIntent.putExtra(Workshop.class.getSimpleName(), Parcels.wrap(tWorkshop));
                        context.startActivity(editClassIntent);
                        
                    }
                });
            }
            else
            {
                ivTeacherBadge.setVisibility(View.INVISIBLE);
                ivTeacherBadge.setEnabled(false);
            }

            Date date = new Date(tWorkshop.getDate());
            DateFormat dateFormat = new SimpleDateFormat("E MMM dd");
            DateFormat timeFormat = new SimpleDateFormat("HH:mm");
            tvDate.setText(dateFormat.format(date));
            tvTime.setText(timeFormat.format(date));
            setRating(tWorkshop.getTeacher());

            Double cost = tWorkshop.getCost();

            if(cost == 0)
            {
                tvCost.setText("Free");
            }
            else
            {
                tvCost.setText("$ "+ String.format("%.2f", cost) +" / hr");
            }



            if(tWorkshop.getImage() != null)
            {


                // load in profile image to holder
                Glide.with(context)
                        .load(tWorkshop.getImage().getUrl())
                        .centerCrop()
                        .into(ivClassIcon);
            }
            else {

                int res = 0;

                switch (tWorkshop.getCategory()) {

                    case "Culinary":
                        res = R.drawable.cooking;
                        break;

                    case "Education":
                        res = R.drawable.education;
                        break;
                    case "Fitness":
                        res = R.drawable.fitness;
                        break;
                    case "Arts/Crafts":
                        res = R.drawable.arts;
                        break;

                    case "Other":
                        res = R.drawable.misc;
                        break;

                    default:
                        break;
                }

                // load in profile image to holder
                Glide.with(context)
                        .load(res)
                        .centerCrop()
                        .into(ivClassIcon);

            }

            ibDirections.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParseGeoPoint userLocation = ParseUser.getCurrentUser().getParseGeoPoint("userLocation");

                    String locationRequest = "http://maps.google.com/maps?saddr=" + userLocation.getLatitude() + "," + userLocation.getLongitude() +
                            "&daddr=" + tWorkshop.getLocation().getLatitude() + "," + tWorkshop.getLocation().getLongitude();

                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse(locationRequest));
                    context.startActivity(intent);
                }
            });


        }


        private void setRating(ParseUser instructor){

            Ratings.Query ratingParseQuery = new Ratings.Query();
            ratingParseQuery.getAllRatings().whereEqualTo("user", instructor);

            ratingParseQuery.findInBackground(new FindCallback<Ratings>() {

                @Override
                public void done(List<Ratings> objects, ParseException e) {
                    if (e == null) {

                        if (objects.size() > 0) {
                            Ratings currentRating = objects.get(0);
                            float currentRatingAverage = currentRating.getAverageRating();
                            if(currentRatingAverage==0){
                                rbInstructorRating.setEnabled(false);
                            } else {
                                rbInstructorRating.setRating(currentRatingAverage);
                            }

                        } else {
                            rbInstructorRating.setEnabled(false);
                        }

                    } else {
                        e.printStackTrace();
                    }
                }
            });




        }


    }

}
