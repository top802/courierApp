package wisehands.me.deliverty;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private final String TAG = "massageservice";

    public MyFirebaseMessagingService() {
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
                Log.d(TAG, "Message From Server: ");

        final String ADMIN_CHANNEL_ID = "0001";
        int notificationId = new Random().nextInt(60000);

        Intent resultIntent = new Intent(this, MassegeOrder.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                .setSmallIcon(R.drawable.main_logo)  //a resource for your custom small icon
                .setContentTitle(remoteMessage.getNotification().getTitle()) //the "title" value you sent in your notification
                .setContentText(remoteMessage.getNotification().getBody())
                .setContentIntent(resultPendingIntent)//ditto
                .setAutoCancel(true)  //dismisses the notification on click
                .setSound(defaultSoundUri);
        Log.d(TAG, "Message From Server: " + remoteMessage.getNotification().getTitle());
        Log.d(TAG, "Message From Server: " + remoteMessage.getNotification().getBody());
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Log.d(TAG, "Message From Server: notificationManager" + notificationManager);
        if (notificationManager != null) {
            notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build());
        }


//        super.onMessageReceived(remoteMessage);
//
//        Log.d(TAG, "From: " + remoteMessage.getFrom());
//        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
    }

}
