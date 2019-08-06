package com.example.skillshop.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.skillshop.FollowingListActivity;
import com.example.skillshop.InstructorDetailsActivity;
import com.example.skillshop.Models.Ratings;
import com.example.skillshop.Models.User;
import com.example.skillshop.Models.Workshop;
import com.example.skillshop.NavigationFragments.HomeFragment;
import com.example.skillshop.NavigationFragments.UserProfileFragment;
import com.example.skillshop.R;
import com.example.skillshop.UserProfileActivity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_LONG;

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
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView tvName;
        public TextView tvPreferences;
        public ImageView ivProfilePic;
        public Button btnFollow;
        public RatingBar rbInstructorRating;


        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPreferences = itemView.findViewById(R.id.tvPreferences);
            ivProfilePic = (ImageView) itemView.findViewById(R.id.ivProfilePic);
            btnFollow = (Button) itemView.findViewById(R.id.btnFollow);
            rbInstructorRating = (RatingBar) itemView.findViewById(R.id.rbInstructorRating);
            itemView.setOnClickListener(this);
        }

        public void bind(ParseUser user) {
            setAllViews(user);
            setupFollowButton(user);
        }

        private void setAllViews(ParseUser user) {
            tvName.setText(user.get("firstName").toString() + " " + user.get("lastName").toString());
            JSONArray preferences = user.getJSONArray("preferences");
            String preferenceList = getPreferences(preferences, user);
            tvPreferences.setText(preferenceList);
            Glide.with(context).load(user.getString("profilePicUrl")).apply(new RequestOptions().circleCrop()).into(ivProfilePic);
            setInstructorRating(user);
        }

        private void setInstructorRating(ParseUser user) {
            rbInstructorRating.setIsIndicator(true);

            Ratings.Query ratingParseQuery = new Ratings.Query();
            ratingParseQuery.getAllRatings().whereEqualTo("user", user);

            ratingParseQuery.findInBackground(new FindCallback<Ratings>() {

                @Override
                public void done(List<Ratings> objects, ParseException e) {
                    if (e == null) {

                        if(objects.size()>0) {
                            Ratings currentRating = objects.get(0);
                            rbInstructorRating.setRating(currentRating.getAverageRating());
                        }

                    } else {
                        e.printStackTrace();
                    }
                }
            });

        }

        private String getPreferences(JSONArray preferences, ParseUser user) {
            String preferenceString = "";
            if (preferences != null) {
                for (int i = 0; i < preferences.length(); i++) {
                    try {
                        if(i==preferences.length()-1) {
                            preferenceString += preferences.getString(i);
                        } else {
                            preferenceString += preferences.getString(i) + " | ";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                preferenceString += user.get("firstName").toString() + " has no preferences set";
            }
            return preferenceString;
        }

        private void setupFollowButton(ParseUser fellowAttendee) {

            //Checks if the current user is already following their fellow attendee and sets up the 
            //following button appropriately
            ParseUser currentUser = ParseUser.getCurrentUser();
            String fellowAttendeeId = fellowAttendee.getObjectId();

            //Checks if the current attendee is the current user
            if (currentUser.getObjectId().equals(fellowAttendeeId)) {
                btnFollow.setVisibility(View.GONE);
            } else {
                ArrayList<String> myFollowing = (ArrayList<String>) currentUser.get("friends");
                Boolean isFollowing = myFollowing.contains(fellowAttendeeId);
                if (isFollowing) {
                    btnFollow.setText("UNFOLLOW USER");
                }

                btnFollow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Gets the list of users being followed by the current user. This has to be done
                        //each time the follow button is clicked because the list may change if the
                        //current user clicks multiple times.
                        ArrayList<String> currentlyFollowing = (ArrayList<String>) currentUser.get("friends");
                        Boolean isCurrentlyFollowing = currentlyFollowing.contains(fellowAttendeeId);

                        if (!isCurrentlyFollowing) {
                            followAttendee(currentlyFollowing, fellowAttendeeId, fellowAttendee, currentUser);

                        } else {
                            unfollowAttendee(currentlyFollowing, fellowAttendeeId, fellowAttendee, currentUser);
                        }
                    }
                });
            }
        }

        private void unfollowAttendee(ArrayList<String> currentlyFollowing, String fellowAttendeeId, ParseUser fellowAttendee, ParseUser currentUser) {
            //Removes the attendee from the current user's following list and saves it to parse
            currentlyFollowing.remove(fellowAttendeeId);

            currentUser.put("friends", currentlyFollowing);
            currentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(context, "You are no longer following " + fellowAttendee.get("firstName"), LENGTH_LONG).show();
                        //todo - make invisible?

                    } else {
                        e.printStackTrace();
                    }
                }
            });
            //Resets the following button
            btnFollow.setText("FOLLOW USER");
        }

        private void followAttendee(ArrayList<String> currentlyFollowing, String fellowAttendeeId, ParseUser fellowAttendee, ParseUser currentUser) {

            //Adds the attendee to the current user's following list and saves it to parse
            currentlyFollowing.add(fellowAttendeeId);
            ParseUser.getCurrentUser().put("friends", currentlyFollowing);
            ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(context, "You are now following " + fellowAttendee.get("firstName"), LENGTH_LONG).show();
                    } else {
                        e.printStackTrace();
                    }
                }
            });
            //Resets the following button
            btnFollow.setText("UNFOLLOW USER");
        }

        @Override
        public void onClick(View v) {

            //gets item position
            int position = getAdapterPosition();
            //make sure the position is valid (that it exists in the view)
            if (position != RecyclerView.NO_POSITION) {
                //get the post at the position (will not work if the class is static)
                ParseUser user = mUsers.get(position);

                //if user clicks on themselves, continues to UserProfileFragment
                if (user.getUsername().equals(ParseUser.getCurrentUser().getUsername())) {


                } else {
                    //create intent for the new activity
                    Intent openUserProfileIntent = new Intent(context, UserProfileActivity.class);
                    Log.i("UserAdapter", "created intent");
                    openUserProfileIntent.putExtra(User.class.getSimpleName(), Parcels.wrap(user));
                    //serialize the movie using parceler, uses the short name of the movie as a key
                    openUserProfileIntent.putExtra(ParseUser.class.getSimpleName(), Parcels.wrap(user));
                    context.startActivity(openUserProfileIntent);
                }
            }

        }
    }

}
