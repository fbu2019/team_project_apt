package com.example.skillshop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.skillshop.Adapters.ChatAdapter;
import com.example.skillshop.Models.Message;
import com.example.skillshop.Models.Workshop;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import static com.example.skillshop.Models.Message.WORKSHOP_KEY;

public class ChatActivity extends AppCompatActivity {

    EditText etMessage;
    Button btSend;

    RecyclerView rvChat;
    ArrayList<Message> mMessages;
    ChatAdapter mAdapter;
    // Keep track of initial load to scroll to the bottom of the ListView
    boolean mFirstLoad;

    Workshop detailedWorkshop;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setupMessagePosting();
        refreshMessages();

        detailedWorkshop = Parcels.unwrap(getIntent().getParcelableExtra(Workshop.class.getSimpleName()));

    }





    // Setup message field and posting
    void setupMessagePosting() {


        // Find the text field and button
        etMessage = (EditText) findViewById(R.id.etMessage);
        btSend = (Button) findViewById(R.id.btSend);
        rvChat = (RecyclerView) findViewById(R.id.rvChat);
        mMessages = new ArrayList<>();
        mFirstLoad = true;
        final String userId = ParseUser.getCurrentUser().getObjectId();
        mAdapter = new ChatAdapter(ChatActivity.this, userId, mMessages);
        rvChat.setAdapter(mAdapter);

        // associate the LayoutManager with the RecylcerView
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        linearLayoutManager.setReverseLayout(true);
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

                    message.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {

                            refreshMessages();
                            etMessage.setText(null);

                        }
                    });
                }
            }
        });
    }

    // Query messages from Parse so we can load them into the chat adapter
    void refreshMessages() {
        // Construct query to execute
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        // Configure limit and sort order
        query.setLimit(100);

        // get the latest messages, order will show up newest to oldest of this group
        query.orderByDescending("createdAt");


        // This is equivalent to a SELECT query with SQL
        query.findInBackground(new FindCallback<Message>() {
            public void done(List<Message> messages, ParseException e) {
                if (e == null) {
                    mMessages.clear();
                    for(int i = 0 ; i < messages.size();i++)
                    {
                        Log.e("message", messages.get(i).getWorkshop());
                        if(detailedWorkshop.getObjectId().equals(messages.get(i).getWorkshop())) {
                            mMessages.add(messages.get(i));
                        }
                    }
                    mAdapter.notifyDataSetChanged(); // update adapter
                    // Scroll to the bottom of the list on initial load
                    if (mFirstLoad) {
                        rvChat.scrollToPosition(0);
                        mFirstLoad = false;
                    }

                    rvChat.scrollToPosition(0);
                } else {
                    Log.e("message", "Error Loading Messages" + e);
                }
            }
        });
    }




}
