package com.example.skillshop.Adapters;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.skillshop.ClassDescription.ClassDetailsActivity;
import com.example.skillshop.Models.Workshop;
import com.example.skillshop.R;

import org.parceler.Parcels;

import java.util.ArrayList;
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
        private ImageView ivClassImage;
        private ConstraintLayout clLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            findAllViews();

        }

        private void findAllViews() {
            tvClassName = itemView.findViewById(R.id.tvClassName);
            ivClassImage = itemView.findViewById(R.id.ivClassImage);

        }


        public void bind(final Workshop tWorkshop) {
            setAllViews(tWorkshop);


            if(tWorkshop.getImage() != null)
            {

                // load in profile image to holder
                Glide.with(context)
                        .load(tWorkshop.getImage().getUrl())
                        .centerCrop()
                        .into(ivClassImage);
            }
            else {

                switch (tWorkshop.getCategory()) {

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
            }
            switch (tWorkshop.getCategory()) {

                case "Culinary":
                    ivClassImage.setColorFilter(Color.argb(100, 255, 87, 255));
                    break;

                case "Education":
                    ivClassImage.setColorFilter(Color.argb(100, 247, 146, 59));
                    break;
                case "Fitness":
                    ivClassImage.setColorFilter(Color.argb(100, 213, 204, 238));
                    break;
                case "Arts/Crafts":
                    ivClassImage.setColorFilter(Color.argb(100, 255, 236, 181));
                    break;
                case "Other":
                    ivClassImage.setColorFilter(Color.argb(100, 107, 206, 187));
                    break;

                default:
                    break;
            }

        }

        private void setAllViews(Workshop tWorkshop) {
            tvClassName.setText(tWorkshop.getName());

        }


    }


}
