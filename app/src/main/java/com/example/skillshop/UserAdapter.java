package com.example.skillshop;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.skillshop.Models.Workshop;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
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
        final ParseUser fellowAttendee = mUsers.get(position);
        //populate the views according to this data
        viewHolder.bind(fellowAttendee);


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
        public Button btnAddFriend;


        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPreferences = itemView.findViewById(R.id.tvPreferences);
            ivProfilePic = (ImageView) itemView.findViewById(R.id.ivProfilePic);
            btnAddFriend = (Button) itemView.findViewById(R.id.btnAddFriend);
        }

        private void setupAddFriendBtn(ParseUser fellowAttendee) {

            btnAddFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ParseUser currentUser = ParseUser.getCurrentUser();

                    ArrayList<String> myFriends = (ArrayList<String>) currentUser.get("friends");
                    String fellowAttendeeId = fellowAttendee.getObjectId().toString();
                    Boolean isFollowing = myFriends.contains(fellowAttendeeId);
                    if (isFollowing) {
                        btnAddFriend.setText("UNFOLLOW USER");
                    }

                    if (!isFollowing) {
                        myFriends.add(fellowAttendeeId);
                        currentUser.put("friends", myFriends);
                        currentUser.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast.makeText(context, "You are now following " + fellowAttendee.get("firstName"), Toast.LENGTH_LONG).show();
                                } else {
                                    e.printStackTrace();
                                }
                            }
                        });

                        btnAddFriend.setText("UNFOLLOW USER");


                    }else{
                        myFriends.remove(fellowAttendeeId);
                        currentUser.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast.makeText(context, "You are no longer following " + fellowAttendee.get("firstName"), Toast.LENGTH_LONG).show();
                                } else {
                                    e.printStackTrace();
                                }
                            }
                        });
                        btnAddFriend.setText("FOLLOW USER");
                    }
                }
            });
        }


        public void bind(ParseUser user) {
            setAllViews(user);
        }

        private void setAllViews(ParseUser user) {
            tvName.setText(user.get("firstName").toString() + " " + user.get("lastName").toString());
            JSONArray preferences = user.getJSONArray("preferences");
            String preferenceString = "";

            if (preferences != null){
                for (int i = 0; i < preferences.length(); i++){
                    try {
                        preferenceString += preferences.getString(i) + " | ";
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }else{
                preferenceString += user.get("firstName").toString() + " has no preferences set";
            }

            tvPreferences.setText(preferenceString);
            Glide.with(context).load(user.getString("profilePicUrl")).into(ivProfilePic);
            setupAddFriendBtn(user);
        }
    }

}
