package com.example.skillshop.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.skillshop.Models.Message;
import com.example.skillshop.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private List<Message> mMessages;
    private Context mContext;
    private String mUserId;

    public ChatAdapter(Context context, String userId, List<Message> messages) {
        mMessages = messages;
        this.mUserId = userId;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.item_chat, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message = mMessages.get(position);
        final boolean isMe = message.getUserId() != null && message.getUserId().equals(mUserId);



        if (isMe) {

            holder.imageMe.setVisibility(View.VISIBLE);


            if(message.getTeacher().equals(ParseUser.getCurrentUser().getObjectId()))
            {
                holder.ivTeacherMe.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.ivTeacherMe.setVisibility(View.GONE);
            }

            holder.imageOther.setVisibility(View.GONE);
            holder.ivTeacherOther.setVisibility(View.GONE);
            holder.body.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        } else {
            holder.imageOther.setVisibility(View.VISIBLE);

            if(message.getTeacher().equals(message.getUserId()))
            {
                holder.ivTeacherOther.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.ivTeacherOther.setVisibility(View.GONE);
            }

            holder.ivTeacherOther.setVisibility(View.VISIBLE);
            holder.imageMe.setVisibility(View.GONE);
            holder.ivTeacherMe.setVisibility(View.GONE);
            holder.body.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        }

        final ImageView profileView = isMe ? holder.imageMe : holder.imageOther;
        setProfileUrl(profileView,message.getString("userId"));



        holder.body.setText(message.getBody());
    }

    public void setProfileUrl(ImageView imageMe, String id)
    {

        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.whereEqualTo("objectId",id);
        // This is equivalent to a SELECT query with SQL
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null) {
                    String url = users.get(0).getString("profilePicUrl");
                    ParseUser user = users.get(0);
                    Glide.with(mContext)
                            .load(url)
                            .error(R.drawable.profile)
                            .placeholder(R.drawable.profile)
                            .apply(new RequestOptions().circleCrop())
                            .into(imageMe);
                } else {
                    Log.e("message", "Error Loading Messages" + e);
                }
            }
        });



    }


    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageOther;
        ImageView ivTeacherOther;
        ImageView imageMe;
        ImageView ivTeacherMe;
        TextView body;

        public ViewHolder(View itemView) {
            super(itemView);
            imageOther = (ImageView)itemView.findViewById(R.id.ivProfileOther);
            imageMe = (ImageView)itemView.findViewById(R.id.ivProfileMe);
            body = (TextView)itemView.findViewById(R.id.tvBody);

            ivTeacherMe = itemView.findViewById(R.id.ivTeacherMe);
            ivTeacherOther = itemView.findViewById(R.id.ivTeacherOther);
        }
    }
}