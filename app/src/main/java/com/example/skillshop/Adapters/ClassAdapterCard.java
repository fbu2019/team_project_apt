package com.example.skillshop.Adapters;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.skillshop.ClassManipulationActivities.ClassDetailsActivity;
import com.example.skillshop.ClassManipulationActivities.EditClassActivity;
import com.example.skillshop.Models.Workshop;
import com.example.skillshop.R;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.text.DateFormat;
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
        LayoutInflater inflater = LayoutInflater.from(context);
        View classView = inflater.inflate(R.layout.item_class_card, parent, false);
        ViewHolder viewHolder = new ViewHolder(classView);
        return viewHolder;
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
        private TextView tvInstructor;
        private TextView tvDate;
        private TextView tvTime;
        private TextView tvLocation;
        private TextView tvCost;
        private ImageView ivTeacherBadge;

        public ViewHolder(View itemView) {
            super(itemView);
            findAllViews();
        }

        private void findAllViews() {
            //perform findViewById lookups by id in the xml file
            ivClassIcon = itemView.findViewById(R.id.ivClassIcon);
            tvClassName = itemView.findViewById(R.id.tvClassName);
            tvInstructor = itemView.findViewById(R.id.tvInstructor);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime =itemView.findViewById(R.id.tvTime);
            tvLocation =  itemView.findViewById(R.id.etLocation);
            tvCost =  itemView.findViewById(R.id.tvCost);
            ivTeacherBadge = itemView.findViewById(R.id.ivTeacherBadge);
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
               tvInstructor.setText(teacher.getString("firstName")+" "+teacher.getString("lastName"));
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

            Date date = new Date(tWorkshop.getDate());
            DateFormat dateFormat = new SimpleDateFormat("E MMM dd");
            DateFormat timeFormat = new SimpleDateFormat("HH:mm");
            tvDate.setText(dateFormat.format(date));
            tvTime.setText(timeFormat.format(date));
            tvLocation.setText(tWorkshop.getLocationName());

            Double cost = tWorkshop.getCost();

            if(cost == 0)
            {
                tvCost.setText("Free");
                tvCost.setBackground(new ColorDrawable(Color.parseColor("#00DD00")));
            }
            else
            {
                tvCost.setText("$ "+cost);
            }




            int res = 0 ;

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

                default: break;
            }

            // Define a listener for image loading
            SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    // TODO 1. Insert the bitmap into the profile image view
                    ivClassIcon.setImageBitmap(resource);


                    // TODO 2. Use generate() method from the Palette API to get the vibrant color from the bitmap
                    // Set the result as the background color for `R.id.vPalette` view containing the contact's name.
                    Palette palette = Palette.from(resource).generate();
                    Palette.Swatch vibrantPalette = palette.getVibrantSwatch();
                    int vibrant = vibrantPalette.getRgb();
                    int dominant = palette.getDominantColor(Color.WHITE);
                    ivClassIcon.setBackgroundColor(vibrant);

                }

            };


            ivClassIcon.setTag(target);


            Drawable draw = context.getResources().getDrawable(res);
            Glide.with(context).asBitmap().load(res).centerCrop().into(target);


        }


    }


}
