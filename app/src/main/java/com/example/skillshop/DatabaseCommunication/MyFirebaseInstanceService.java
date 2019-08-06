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

import com.example.skillshop.LoginActivities.LoginActivity;
import com.example.skillshop.Models.Query;
import com.example.skillshop.Models.Workshop;
import com.example.skillshop.R;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

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
                    sendNotification(remoteMessage.getNotification().getBody(), (int) remoteMessage.getSentTime(),editedClass);
                }
            }
        });










    }
    private void sendNotification(String messageBody, int time, Workshop editedClass) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        intent.putExtra(Workshop.class.getSimpleName(), Parcels.wrap(editedClass));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, time, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId =CHANNEL_ID;
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_skillshop_notification)
                        .setContentTitle("Skillshop")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setSound(defaultSoundUri);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(time, notificationBuilder.build());
    }


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.put("firebaseToken",FirebaseInstanceId.getInstance().getToken());
        currentUser.saveInBackground();
    }


}
