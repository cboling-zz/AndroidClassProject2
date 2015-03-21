package net.bcsw.dailyselfie;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RemoteViews;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Main Activity  for Coursera Android Programming #2 Peer Reviews Project
 * <p/>
 * Requirement #1: If the user clicks on the camera icon on the ActionBar, the app will open up
 * a picture taking app already installed on the device.
 * <p/>
 * Requirement #2: If the user now snaps a picture and accepts it, the picture is returned to
 * the DailySelfie app and then displayed in some way to the user along with other selfies the user
 * may have already taken.  (Show bitmap on left portion of new listView item along with name (ie
 * date & time)
 * <p/>
 * Requirement #3: TODO If the user clicks on the small view, then a large view will open up,
 * showing the selfie in a larger format.
 * <p/>
 * Requirement #4: TODO If the user exits the app and then reopens it, they should have access to
 * all the selfies saved on their device.
 * <p/>
 * Requirement #5: TODO Because the user wants to take selfies periodically over a long period of
 * time, the app should create and set an Alarm that fires roughly once evey two minutes. TODO When
 * one Pulling down on the notification drawer should expose a notification view. TODO Clicking on
 * this notification view should bring the user back to the application.
 */
public class MainActivity extends ActionBarActivity
{
    private static final String TAG                 = "MainActivity";
    static final         int    REQUEST_TAKE_PHOTO  = 1;
    static final         String EXTRA_DATE_TAKEN    = "DateTaken";
    static final         String EXTRA_DATE_FILENAME = "ImageFileName";
    private static final int MIN_SELFIE_TIMEOUT = 2 * 60 * 1000;
    private static final int SELPHIE_NOTIFY_ID  = 1;

    // Graphic and UI elements
    private SelfieViewAdapter imageListAdapter;
    private ListView          imageListView;

    // Alarm Elements

    private AlarmManager  alarmManager;
    private Intent        notificationReceiverIntent;
    private PendingIntent notificationReceiverPendingIntent;

    // Notification Action Elements
    private Intent        notifyIntent;
    private PendingIntent notifyContentIntent;
    private RemoteViews notifyContentView = new RemoteViews("net.bcsw.dailyselfie.MainActivity",
                                                            R.layout.custom_notification);

    // Photo elements

    private String currentImageFilename;

    //////////////////////////////////////////////////////////////////////
    // Saved preferences
    private SharedPreferences prefs;

    // Last time a selphie was taken.  Millisecond Ticks since Jan. 1, 1970, midnight GMT
    private static String LAST_SELFIE_TICKS = "last_selfie_ticks";

    //////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate: entered");

        // Get reference to our saved preferences (if any)

        prefs = getPreferences(MODE_PRIVATE);

        // Expand our view now and obtain a reference to the image ListView

        setContentView(R.layout.activity_main);
        imageListView = (ListView) findViewById(R.id.listView);

        // Load last time a selfie was made from shared preference storage

        imageListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                launchImageViewActivity(parent, view, position, id);
            }
        });
        registerForContextMenu(imageListView);

        // Create a listview adapter to handle the selfie records and register it with the
        // main listview

        imageListAdapter = new SelfieViewAdapter(getApplicationContext());
        imageListView.setAdapter(imageListAdapter);

        // Setup alarm and notification elements

        notificationSetup();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        if (v.getId() == imageListView.getId())
        {
            ListView view = (ListView) v;
            MenuItem delItem = menu.add("Delete (TODO: Not yet fully supported)");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        //        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        //        final int deletePosition = position;
        //
        //        builder.setMessage(R.string.delete_image_message)
        //                .setTitle(R.string.delete_image_title)
        //                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
        //                {
        //                    @Override
        //                    public void onClick(DialogInterface d, int w)
        //                    {
        //                        imageListAdapter.remove(deletePosition);
        //                    }
        //                })
        //                .setNegativeButton(android.R.string.cancel,
        //                                   new DialogInterface.OnClickListener()
        //                                   {
        //                                       @Override
        //                                       public void onClick(DialogInterface d, int w)
        //                                       {
        //                                       }
        //                                   })
        //                .setIcon(android.R.drawable.ic_dialog_alert)
        //                .show();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Log.i(TAG, "onOptionsItemSelected: entered: id: " + item.getItemId());

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        if (id == R.id.action_camera)
        {
            // Cancel any previous alarm to take a selfie

            alarmManager.cancel(notificationReceiverPendingIntent);

            // Launch Camera

            return launchCameraApp();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume()
    {
        Log.i(TAG, "onResume: entered");

        super.onResume();

        // TODO: restore selfie records from persistent storage

    }

    @Override
    public void onPause()
    {
        Log.i(TAG, "onPause: entered");

        super.onPause();

        // TODO: save off selfie records to persistent storage

    }

    private void notificationSetup()
    {
        // Get the AlarmManager Service
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Create an Intent to broadcast to the AlarmNotificationReceiver
        notificationReceiverIntent = new Intent(MainActivity.this, AlarmNotificationReceiver.class);

        // Create an PendingIntent that holds the NotificationReceiverIntent

        notificationReceiverPendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0,
                                                                       notificationReceiverIntent,
                                                                       0);

        // Prepare notification intent that will start this application classs
        // and then schedule the first 'take selfie notification'

        notifyIntent = new Intent(getApplicationContext(), MainActivity.class);

        notifyContentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notifyIntent,
                                                        PendingIntent.FLAG_UPDATE_CURRENT);
        scheduleSelfieNotification();
    }

    /**
     * Launch an already installed camera application to take a selfie for us.  The Intent will save
     * the photo to a file we supply and return a bitmap image
     *
     * @return true if successfully launched
     */
    private boolean launchCameraApp()
    {
        Log.i(TAG, "launchCameraApp: entered");

        // Make sure we have a camera and media to write picture to

        if (!getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.no_camera_message).setTitle(
                    R.string.no_camera_title).setPositiveButton(android.R.string.ok,
                                                                new DialogInterface.OnClickListener()
                                                                {
                                                                    @Override
                                                                    public void onClick(
                                                                            DialogInterface d,
                                                                            int w)
                                                                    {
                                                                    }
                                                                }).setIcon(
                    android.R.drawable.ic_dialog_alert).show();
            return false;
        }
        else if (!SelfieRecord.HasStorage(true))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.no_media_message).setTitle(
                    R.string.no_media_title).setPositiveButton(android.R.string.ok,
                                                               new DialogInterface.OnClickListener()
                                                               {
                                                                   @Override
                                                                   public void onClick(
                                                                           DialogInterface d, int w)
                                                                   {
                                                                   }
                                                               }).setIcon(
                    android.R.drawable.ic_dialog_alert).show();
            return false;
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            try
            {
                // Create the file that should hold the new photo
                //
                // Change 3rd parameter to 'CreateImageFile' to store it in external public
                // picture directory

                File imageFile = SelfieRecord.CreateImageFile(SelfieRecord.GetCurrentTime());
                currentImageFilename = imageFile.toString();

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));

                Log.i(TAG, "launchCameraApp: starting camera activity for result");

                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

                return true;
            }
            catch (IOException e)
            {
                Log.w(TAG, "launchCameraApp: IO Exception: " + e);
            }
        }
        return false;
    }

    /**
     * Handle the response from any activities that return an result
     *
     * @param requestCode Request code for activity
     * @param resultCode  The result
     * @param data        associated data with result
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.d(TAG, "onActivityResult: entered, request: " + requestCode + ", result: " + resultCode);

        if (requestCode == REQUEST_TAKE_PHOTO)
        {
            if (resultCode == RESULT_OK)
            {
                Date dateTaken = new Date(Calendar.getInstance().getTimeInMillis());
                String fileName = currentImageFilename;

                SelfieRecord item = new SelfieRecord(dateTaken, fileName);

                imageListAdapter.add(item);

                // Update last selfie time

                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong(LAST_SELFIE_TICKS, dateTaken.getTime());
                editor.apply();
            }
            // Reschedule notify update.  Do it here in case the did not successfully take one

            scheduleSelfieNotification();
        }
    }

    /**
     * Schedule the next 'take a selfie' notification
     * <p/>
     * We implement a repeating inexact alarm to 'nag' the user into taking a picture.  Once he
     * starts the camera application, we cancel that alarm and then re-arm it.  If he accepted the
     * picture, then he will not be nagged for the minimum interval, otherwise we start nagging
     * right away
     */
    private void scheduleSelfieNotification()
    {
        Log.d(TAG, "scheduleSelfieNotification: entered");

        // Get the last time a selfie was taken

        long last = prefs.getLong(LAST_SELFIE_TICKS, 0);
        long sinceLast = SelfieRecord.GetCurrentTime().getTime() - last;

        long delta = (sinceLast >= MIN_SELFIE_TIMEOUT) ? 0 : MIN_SELFIE_TIMEOUT - sinceLast;

        // Use elapsed realtime so that it counts time during sleep but does not wake the
        // device when it goes off (to save power) but it will notify user after the
        // next device wakeup (other app or user intervention)

        //        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
        //                                         delta, MIN_SELFIE_TIMEOUT,
        //                                         notificationReceiverPendingIntent);
    }

    /**
     * Generate a 'take a selfie' notification
     */
    private void performNotification()
    {
        Log.i(TAG, "performNotification: entered");

        // Build the Notification

        Notification.Builder notificationBuilder = new Notification.Builder(
                getApplicationContext()).setTicker(
                getResources().getString(R.string.notify_text)).setSmallIcon(
                R.drawable.ic_menu_camera).setAutoCancel(true).setContentIntent(
                notifyContentIntent).setContent(notifyContentView);

        // Pass the Notification to the NotificationManager:

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(SELPHIE_NOTIFY_ID, notificationBuilder.build());
    }

    /**
     * Handle click of image List View item by showing new activity with that image
     */
    private void launchImageViewActivity(AdapterView<?> parent, View view, int position, long id)
    {
        Log.i(TAG, "launchImageViewActivity: entered, position: " + position + ", id: " + id);

        Object item = imageListAdapter.getItem(position);

        // TODO: Implement.  Should launch new activity.
        // TODO: this activity will show selected image, update title, and handle swipes
        // TODO: back button (no matter how many swipes) returns to this activity
    }

    /**
     * Handle one-shot alarm to display a selfie.
     */
    private class AlarmNotificationReceiver extends BroadcastReceiver
    {
        private static final String TAG = "AlarmNotifyRcvr";

        @Override
        public void onReceive(Context context, Intent intent)
        {
            // Log occurrence of notify() call

            Log.i(TAG,
                  "Sending notification at:" + DateFormat.getDateTimeInstance().format(new Date()));

            performNotification();
        }
    }
}
