package wisehands.me.deliverty;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;


public class MyFirebaseMessagingService extends FirebaseMessagingService {


    private static final String TAG = "Messaging" ;
//    private NotificationUtils notificationUtils;
    public MyFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        long[] pattern = {0,1000,500,1000};


        Map<String,String> data = remoteMessage.getData();
        String title = data.get("order");
        String body = data.get("address");
        Log.d(TAG, "Message From Server: notificationManager " + title + " " + body);

//        Intent messageIntent = new Intent(this, MessageOrder.class);
//        messageIntent.putExtra("order", title);
//        messageIntent.putExtra("address", body);
////        startActivity(messageIntent);


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "CHANNEL_ID";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            @SuppressLint("WrongConstant")
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Wise notification",
                    NotificationManager.IMPORTANCE_MAX);
            notificationChannel.setDescription("WiseHandsDelivery");
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(pattern);
            notificationChannel.setLightColor(Color.RED);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        long itemId = 12345678910L;
        int notificationId = ((Long)itemId).intValue();
        String EXTRA_ITEM_ID = "message";

        Intent resultIntent = new Intent(this, MessageOrder.class);
        resultIntent.putExtra(EXTRA_ITEM_ID, itemId);
        resultIntent.putExtra("order", title);
        resultIntent.putExtra("address", body);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MessageOrder.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.main_logo, options);

        Uri soundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setLargeIcon(bitmap)
                .setSmallIcon(R.drawable.main_logo_black)
                .setSound(soundUri)
                .setVibrate(pattern)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(resultPendingIntent);

        notificationManager.notify(notificationId, notificationBuilder.build());

//
//        @Override
//        protected void onMessage(Context arg0, Intent arg1) {
//
//            String Message = arg1.getStringExtra("payload");
//            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//
//            notificationManager.notify(
//                    new Random().nextInt(),
//                    new NotificationCompat.Builder(this).setContentTitle("Message")
//                            .setWhen(System.currentTimeMillis())
//                            .setDefaults(Notification.DEFAULT_ALL)
//                            .setContentText(Message).build());
//

//        Intent intent = new Intent(this, MessageOrder.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,0 /* request code */, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        long[] pattern = {500,500,500,500,500};
//
//        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.main_logo_black)
//                .setContentTitle(remoteMessage.getNotification().getTitle())
//                .setContentText(remoteMessage.getNotification().getBody())
//                .setVibrate(pattern)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
//
//
//    }

////        to build notifications object
//
//        final String ADMIN_CHANNEL_ID = "0001";
//        int notificationId = new Random().nextInt(60000);
//
//        Intent resultIntent = new Intent(this, MessageOrder.class);
//        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
//                .setContentTitle(remoteMessage.getNotification().getTitle())
//                .setContentText(remoteMessage.getNotification().getBody())
//                .setContentIntent(resultPendingIntent)
//                .setAutoCancel(true)
//                .setSmallIcon(main_logo_black)
//                .setSound(defaultSoundUri);
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        Log.d(TAG, "Message From Server: notificationManager" + notificationManager);
//        if (notificationManager != null) {
//            notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build());
//        }

    }

}
