package com.example.skillshop;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.skillshop.Models.Class;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import static com.example.skillshop.LoginActivity.userId;
import static com.example.skillshop.LoginActivity.userName;


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
        final Class tClass = mClasses.get(position);
        //populate the views according to this data
        holder.bind(tClass);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Intent profileDetailsIntent = new Intent(context, ClassDetailsActivity.class);
                //pass in class that was selected
                profileDetailsIntent.putExtra(Class.class.getSimpleName(), Parcels.wrap(tClass));
                context.startActivity(profileDetailsIntent);
            }
        });


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
            ivClassIcon = itemView.findViewById(R.id.ivClassIcon);
            tvClassName = itemView.findViewById(R.id.tvClassName);
            tvInstructor = itemView.findViewById(R.id.tvInstructor);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime =itemView.findViewById(R.id.tvTime);
            tvLocation =  itemView.findViewById(R.id.tvLocation);
            tvCost =  itemView.findViewById(R.id.tvCost);
        }


        public void bind(final Class tClass) {

            setAllViews(tClass);
        }

        private void setAllViews(Class tClass) {

            tvClassName.setText(tClass.getName());

            tvInstructor.setText(userName );
            tvDate.setText(tClass.getDate());
            tvTime.setText(userId);
            tvLocation.setText("Location");
            tvCost.setText("Cost");

            tvInstructor.setText(tClass.getTeacher().getUsername());


            String date = tClass.getDate();

            tvDate.setText(date.substring(0,11));
            tvTime.setText(date.substring(11,16));
            tvLocation.setText("Location");

            Double cost = tClass.getCost();
            if(cost == 0)
            {
                tvCost.setText("Free");
                tvCost.setBackground(new ColorDrawable(Color.parseColor("#00FF00")));
            }
            else
            {
                tvCost.setText("$"+Double.toHexString(cost));
            }

        }


    }


}
