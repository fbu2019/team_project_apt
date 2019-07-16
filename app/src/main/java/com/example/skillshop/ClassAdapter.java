package com.example.skillshop;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.skillshop.Models.Class;

import java.util.ArrayList;
import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ViewHolder>  {


    private static List<Class> mClasses;
    private Context context;

    //pass in the Posts array in the constructor
    public ClassAdapter(ArrayList<Class> classes, Context context) {
        this.mClasses = classes;
        this.context = context;
    }
    //for each row, inflate the layout and cache references into the ViewHolder
    //called when new rows are created
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View classView = inflater.inflate(R.layout.item_class, parent, false);
        ViewHolder viewHolder = new ViewHolder(classView);
        return viewHolder;
    }

    //bind the values based on the position of the element
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //get the data according to position
        Class tClass = mClasses.get(position);
        //populate the views according to this data
        holder.bind(tClass);
    }


    //gets the number of items
    @Override
    public int getItemCount() {
        return mClasses.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView ivClassIcon;
        private TextView tvClassName;
        private TextView tvInstructor;
        private TextView tvDate;
        private TextView tvTime;
        private TextView tvLocation;
        private TextView tvCost;

        public ViewHolder(View itemView) {
            super(itemView);
            findAllViews();
          //  TODO itemView.setOnClickListener(this);
        }

        private void findAllViews() {
            //perform findViewById lookups by id in the xml file
            ivClassIcon = (ImageView) itemView.findViewById(R.id.ivClassIcon);
            tvClassName = (TextView) itemView.findViewById(R.id.tvClassName);
            tvInstructor = (TextView) itemView.findViewById(R.id.tvInstructor);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            tvLocation = (TextView) itemView.findViewById(R.id.tvLocation); //TODO fix et versus tv
            tvCost = (TextView) itemView.findViewById(R.id.tvCost);
        }


        public void bind(final Class tClass) {

            setAllViews(tClass);
        }

        private void setAllViews(Class tClass) {

            tvClassName.setText("Name;");
            tvInstructor.setText("Instructor");
            tvDate.setText("Date");
            tvTime.setText("Time");
            tvLocation.setText("Location");
            tvCost.setText("Cost");


        }
    }


}
