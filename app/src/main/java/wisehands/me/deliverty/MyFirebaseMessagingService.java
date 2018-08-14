package wisehands.me.deliverty;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseMessagingService extends FirebaseMessagingService {


    private static final String TAG = "Messaging" ;
//    private NotificationUtils notificationUtils;
    public MyFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

//        Map<String, String> data = remoteMessage.getData();
//        String order = data.get("order");
//        String address = data.get("address");
//        Log.d(TAG, "Message From Server data: " + order);
//        Log.d(TAG, "Message From Server data: " + address);

    }

//    private void sendNotification(String messageTitle,String messageBody) {
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
//                .setContentTitle(messageTitle)
//                .setContentText(messageBody)
//                .setAutoCancel(true)
//                .setVibrate(pattern)
//                .setLights(Color.BLUE,1,1)
//                .setSound(defaultSoundUri);
////                .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
//
////        Log.d(TAG, "Message From Server: ");
////        Log.d(TAG, "Message From Server: " + remoteMessage.getNotification().getTitle());
////        Log.d(TAG, "Message From Server: " + remoteMessage.getNotification().getBody());
////
////        Map<String, String> data = remoteMessage.getData();
////        String order = data.get("order");
////        String address = data.get("address");
////        Log.d(TAG, "Message From Server data: " + order);
////        Log.d(TAG, "Message From Server data: " + address);
////
//////        NotificationCompat.Builder builder =
////                new NotificationCompat.Builder(this)
////                        .setSmallIcon(R.mipmap.main_logo_black)
////                        .setContentTitle(remoteMessage.getNotification().getTitle())
////                        .setContentText(remoteMessage.getNotification().getBody());
////        Notification notification = builder.build();
////
////        NotificationManager notificationManager =
////                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
////        notificationManager.notify(1, notification);
//
////        Notification notification = new NotificationCompat.Builder(this, "001")
////                .setSmallIcon(R.drawable.main_logo_black)
////                .setContentTitle(remoteMessage.getNotification().getTitle())
////                .setContentText(remoteMessage.getNotification().getBody())
////                .setPriority(NotificationCompat.PRIORITY_HIGH)
////                .build();
////
////
////        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
////        notificationManager.notify(1, notification);
//
////        to build notifications object
////
////        final String ADMIN_CHANNEL_ID = "0001";
////        int notificationId = new Random().nextInt(60000);
////
////        Intent resultIntent = new Intent(this, MessageOrder.class);
////        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
////                PendingIntent.FLAG_UPDATE_CURRENT);
////
////        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
////        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
////                .setContentTitle(remoteMessage.getNotification().getTitle())
////                .setContentText(remoteMessage.getNotification().getBody())
////                .setContentIntent(resultPendingIntent)
////                .setAutoCancel(true)
////                .setSmallIcon(R.mipmap.main_logo_black)
////                .setSound(defaultSoundUri);
////
////        NotificationManager notificationManager =
////                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
////
////        Log.d(TAG, "Message From Server: notificationManager" + notificationManager);
////        if (notificationManager != null) {
////            notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build());
////        }
//
//    }

}
