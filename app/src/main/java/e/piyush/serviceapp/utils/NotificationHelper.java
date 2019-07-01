package e.piyush.serviceapp.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;

import e.piyush.serviceapp.R;

public class NotificationHelper extends ContextWrapper {
    private static final  String SERVICE_CHANNEL_ID="e.piyush.serviceapp";
    private static final  String SERVICE_CHANNEL_NAME="ServiceTime";

    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel serviceChannel=new NotificationChannel(SERVICE_CHANNEL_ID,SERVICE_CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT);
        serviceChannel.enableLights(false);
        serviceChannel.enableVibration(true);
        serviceChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(serviceChannel);
    }

    public NotificationManager getManager() {
        if (manager ==null)
            manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getRealtimeTrackingNotification(String title, String content, Uri defaultsound) {
        return new Notification.Builder(getApplicationContext(),SERVICE_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(content)
                .setSound(defaultsound)
                .setAutoCancel(false);
    }
}
