package com.example.skillshop.DatabaseCommunication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.example.skillshop.ClassDescription.ClassDetailsActivity;
import com.example.skillshop.LoginActivities.LoginActivity;
import com.example.skillshop.Models.Query;
import com.example.skillshop.Models.Workshop;
import com.example.skillshop.R;
import com.facebook.login.Login;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;
import java.util.Random;

public class MyFirebaseInstanceService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "CHANNEL_ID";


    public MyFirebaseInstanceService() {
        super();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String classId = remoteMessage.getNotification().getTitle();

        Query getEditedClass = new Query();
        getEditedClass.whereEqualTo("objectId",classId);

        getEditedClass.findInBackground(new FindCallback<Workshop>() {
            @Override
            public void done(List<Workshop> objects, ParseException e) {
                if(e == null && objects.size()>0)
                {
                    Workshop editedClass = objects.get(0);
                    sendNotification(remoteMessage.getNotification().getBody(),editedClass);
                }
            }
        });


    }


    private void sendNotification(String messageBody, Workshop editedClass) {

        createNotificationChannel();

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_skillshop_notification)
                .setContentTitle("Skillshop")
                .setContentText(messageBody)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(new Random().nextInt(), builder.build());

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel";
            String description = "description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.put("firebaseToken",FirebaseInstanceId.getInstance().getToken());
        currentUser.saveInBackground();
    }


}
