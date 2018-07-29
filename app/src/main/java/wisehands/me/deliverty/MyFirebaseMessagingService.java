package wisehands.me.deliverty;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private final String TAG = "firebase";
    public MyFirebaseMessagingService() {
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

    }
//    private final String TAG = "JSA-FCM";
//
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//
//        if (remoteMessage.getNotification() != null) {
//            Log.e(TAG, "Title: " + remoteMessage.getNotification().getTitle());
//            Log.e(TAG, "Body: " + remoteMessage.getNotification().getBody());
//        }
//
//        if (remoteMessage.getData().size() > 0) {
//            Log.e(TAG, "Data: " + remoteMessage.getData());
//        }
//    }
}
