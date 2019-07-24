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

import com.example.skillshop.NavigationFragments.FragmentHandler;
import com.example.skillshop.R;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.parse.ParseUser;

import java.time.LocalDateTime;

public class MyFirebaseInstanceService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "CHANNEL_ID";


    public MyFirebaseInstanceService() {
        super();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        sendNotification(remoteMessage.getNotification().getBody(), (int) remoteMessage.getSentTime());


    }
    private void sendNotification(String messageBody, int time) {
        Intent intent = new Intent(this, FragmentHandler.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId =CHANNEL_ID;
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_skillshop_notification)
                        .setContentTitle("Skillshop")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

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
