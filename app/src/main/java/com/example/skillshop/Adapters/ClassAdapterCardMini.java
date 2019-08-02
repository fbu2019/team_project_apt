package com.example.skillshop.Adapters;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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


public class ClassAdapterCardMini extends RecyclerView.Adapter<ClassAdapterCardMini.ViewHolder>  {


    private static List<Workshop> mWorkshops;
    private static Context context;

    //pass in the Posts array in the constructor
    public ClassAdapterCardMini(ArrayList<Workshop> workshops,Context context) {
        this.mWorkshops = workshops;
        this.context = context;
    }
    //for each row, inflate the layout and cache references into the ViewHolder
    //called when new rows are created
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class_card_mini, parent, false);
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

        private TextView tvClassName;

        public ViewHolder(View itemView) {
            super(itemView);
            findAllViews();

        }

        private void findAllViews() {
            tvClassName = itemView.findViewById(R.id.tvClassName);
        }


        public void bind(final Workshop tWorkshop) {

            setAllViews(tWorkshop);
        }

        private void setAllViews(Workshop tWorkshop) {
            tvClassName.setText(tWorkshop.getName());

        }


    }


}
