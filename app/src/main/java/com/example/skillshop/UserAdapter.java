package com.example.skillshop;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.skillshop.Models.Workshop;
import com.parse.Parse;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<ParseUser> mUsers;
    private Context context;

    //pass in the Posts array in the constructor
    public UserAdapter(ArrayList<ParseUser> users, Context context) {
        this.mUsers = users;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View userView = inflater.inflate(R.layout.item_user, viewGroup, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(userView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        //get the data according to position
        final ParseUser user = mUsers.get(position);
        //populate the views according to this data
        viewHolder.bind(user);


    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView tvName;
        public TextView tvPreferences;
        public ImageView ivProfilePic;


        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPreferences = itemView.findViewById(R.id.tvPreferences);
            ivProfilePic = (ImageView) itemView.findViewById(R.id.ivProfilePic);


        }

        public void bind(ParseUser user) {
            setAllViews(user);
        }

        private void setAllViews(ParseUser user) {
            tvName.setText(user.get("firstName").toString() + " " + user.get("firstName").toString());
        }
    }

}
