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

import com.bumptech.glide.Glide;
import com.example.skillshop.Models.Workshop;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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

        private void setupAddFriendBtn(ParseUser user) {

            btnAddFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
/*                    ParseUser currentUser = ParseUser.getCurrentUser();


                    String lastname = user.getString("lastName");


                    //  ParseQuery friendsQuery = ParseQuery.
                    ParseQuery<ParseObject> friendsQuery = new ParseQuery<ParseObject>(ParseObject.class);

                    try {
                        friendsQuery.get("friends");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    friendsQuery.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if (e == null) {
                                Log.i("arraycheck", objects.toString());

                            } else {
                                e.printStackTrace();
                            }
                        }
                    });*/


                }
            });
        }

        public void bind(ParseUser user) {
            setAllViews(user);
        }

        private void setAllViews(ParseUser user) {
            tvName.setText(user.get("firstName").toString() + " " + user.get("lastName").toString());
           /* JSONArray preferences = user.getJSONArray("preferences");
            if (!preferences.isNull(1)){
                for (int i = 0; i < preferences.length(); i++){
                    try {
                        preferenceString += preferences.getString(i) + " | ";
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }*/
            String preferenceString = "";

            tvPreferences.setText(preferenceString);
            Glide.with(context).load(user.getString("profilePicUrl")).into(ivProfilePic);
            setupAddFriendBtn(user);
        }
    }

}
