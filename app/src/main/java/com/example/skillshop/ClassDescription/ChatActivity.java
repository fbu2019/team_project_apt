package com.example.skillshop.ClassDescription;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.skillshop.Adapters.ChatAdapter;
import com.example.skillshop.Models.Message;
import com.example.skillshop.Models.Workshop;
import com.example.skillshop.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    EditText etMessage;
    Button btSend;
    RecyclerView rvChat;
    ArrayList<Message> mMessages;
    ChatAdapter mAdapter;
    // Keep track of initial load to scroll to the bottom of the ListView
    boolean mFirstLoad;
    Workshop detailedWorkshop;
    int maxMessages;
    Long lastRefresh;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);
        detailedWorkshop = Parcels.unwrap(getIntent().getParcelableExtra(Workshop.class.getSimpleName()));
        setupMessagePosting();
        refreshMessages(true);
        maxMessages = 20;
        etMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvChat.scrollToPosition(mMessages.size()-1);
            }
        });

        refreshInBackground();
        lastRefresh = Calendar.getInstance().getTimeInMillis();

    }

    public  void refreshInBackground()
    {

        MessageRefresher refresher = new MessageRefresher();
        refresher.start();


    }

    class MessageRefresher extends Thread{

        MessageRefresher(){
        }

        @Override
        public void run() {
            while(true) {
                getNewMessages();

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }



    // Setup message field and posting
    void setupMessagePosting() {


        // Find the text field and button
        etMessage = findViewById(R.id.etMessage);
        btSend = findViewById(R.id.btSend);
        rvChat = findViewById(R.id.rvChat);
        mMessages = new ArrayList<>();
        mFirstLoad = true;
        final String userId = ParseUser.getCurrentUser().getObjectId();
        mAdapter = new ChatAdapter(ChatActivity.this, userId, mMessages);
        rvChat.setAdapter(mAdapter);

        // associate the LayoutManager with the RecylcerView
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        linearLayoutManager.setReverseLayout(false);
        rvChat.setLayoutManager(linearLayoutManager);


        // When send button is clicked, create message object on Parse
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String data = etMessage.getText().toString();
                if(data.length()>0) {

                    // Using new `Message` Parse-backed model now
                    Message message = new Message();
                    message.setBody(data);
                    message.setUserId(ParseUser.getCurrentUser().getObjectId());
                    message.setName(ParseUser.getCurrentUser().get("firstName").toString());
                    message.setWorkshop(detailedWorkshop.getObjectId());
                    message.setTeacher(detailedWorkshop.getTeacher().getObjectId());

                    message.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            refreshMessages(true);
                            etMessage.setText(null);
                            rvChat.scrollToPosition(mMessages.size()-1);

                            if(e != null)
                            {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }


    void getNewMessages()
    {
        // Construct query to execute
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        // get the latest messages, order will show up newest to oldest of this group
        query.orderByDescending("createdAt");
        query.whereEqualTo("workshop",detailedWorkshop.getObjectId());
        // This is equivalent to a SELECT query with SQL

        query.findInBackground(new FindCallback<Message>() {
            public void done(List<Message> messages, ParseException e) {
                if (e == null) {

                    mMessages.clear();

                    for(Message m : messages)
                    {
                        if(!mMessages.contains(m)) {
                            mMessages.add(0, m);
                            mAdapter.notifyItemChanged(mMessages.size() - 1);
                        }

                    }
                    rvChat.scrollToPosition(mMessages.size() - 1);


                } else {
                    Log.e("message", "Error Loading Messages" + e);
                }
            }
        });
    }


    // Query messages from Parse so we can load them into the chat adapter
    void refreshMessages(boolean scroll) {
        // Construct query to execute
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        // get the latest messages, order will show up newest to oldest of this group
        query.orderByDescending("createdAt");
        query.whereEqualTo("workshop",detailedWorkshop.getObjectId());
        // This is equivalent to a SELECT query with SQL
        query.findInBackground(new FindCallback<Message>() {
            public void done(List<Message> messages, ParseException e) {
                if (e == null) {
                    mMessages.clear();
                    for(int i = 0 ; i < messages.size();i++)
                    {
                        mMessages.add(0,messages.get(i));
                        mAdapter.notifyItemChanged(mMessages.size()-1);
                    }

                    if(scroll) {
                        rvChat.scrollToPosition(mMessages.size() - 1);
                    }

                } else {
                    Log.e("message", "Error Loading Messages" + e);
                }
            }
        });
    }



}
