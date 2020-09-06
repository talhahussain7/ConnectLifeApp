package com.example.connectlife.services;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.connectlife.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
            //showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
        showNotification(remoteMessage.getData().get("Title"),remoteMessage.getData().get("Message"));
        //Log.i("Message",remoteMessage.getData().toString());
    }

    public  void showNotification(String title,String message){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"MyNotifications")
                .setContentTitle(title)
                .setSmallIcon(R.drawable.alerter_ic_notifications)
                .setAutoCancel(true)
                .setContentText(message);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(999,builder.build());
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageSent(@NonNull String s) {
        super.onMessageSent(s);

    }
}
