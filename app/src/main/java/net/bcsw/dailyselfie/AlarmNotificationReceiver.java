package net.bcsw.dailyselfie;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Created by cboling on 3/22/2015.
 */
public class AlarmNotificationReceiver extends BroadcastReceiver
{
    private static final String TAG = "AlarmNotifyRcvr";

    //////////////////////////////////////////////////////////////////////
    // Notification Action Elements
    private Intent        notifyIntent;
    private PendingIntent notifyContentIntent;
    private RemoteViews notifyContentView = new RemoteViews("net.bcsw.dailyselfie",
                                                            R.layout.custom_notification);

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.i(TAG, "onReceive: entered");

        // Prepare notification intent that will start this application class and then
        // schedule the first 'take selfie notification'.  Use Cancel Current so it really
        // nags the user with a new ticker every alarm interval.

        notifyIntent = new Intent(context, MainActivity.class);
        notifyContentIntent = PendingIntent.getActivity(context, 0, notifyIntent,
                                                        PendingIntent.FLAG_UPDATE_CURRENT);
        String notifyText = context.getResources().getString(R.string.notify_text);
        notifyContentView.setTextViewText(R.id.text, notifyText);

        // Build the Notification

        Notification.Builder notificationBuilder = new Notification.Builder(context);

        notificationBuilder.setTicker(context.getResources().getString(R.string.notify_text));
        notificationBuilder.setSmallIcon(R.drawable.ic_menu_camera);
        notificationBuilder.setContentIntent(notifyContentIntent);
        notificationBuilder.setContent(notifyContentView);
        notificationBuilder.setAutoCancel(true);

        // Pass the Notification to the NotificationManager:

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);

        notificationManager.notify(MainActivity.SELPHIE_NOTIFY_ID, notificationBuilder.build());
    }
}
