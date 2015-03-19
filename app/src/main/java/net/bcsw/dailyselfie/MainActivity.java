package net.bcsw.dailyselfie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Main Activity  for Coursera Android Programming #2 Peer Reviews Project
 * <p/>
 * Requirement #1: If the user clicks on the camera icon on the ActionBar, the app will open up a
 * picture-­‐ taking app already installed on the device.
 * <p/>
 * Requirement #2: If the user now snaps a picture and accepts it, the picture is returned to the
 * DailySelfie app and then displayed in some way to the user along with other selfies the user may
 * have already taken.  (Show bitmap on left portion of new listView item anlong with name (ie date
 * & time)
 * <p/>
 * Requirement #3: If the user clicks on the small view, then a large view will open up, showing the
 * selfie in a larger format.
 */
public class MainActivity extends ActionBarActivity
{
    private static final String TAG                 = "MainActivity";
    static final         int    REQUEST_TAKE_PHOTO  = 1;
    static final         String EXTRA_DATE_TAKEN    = "DateTaken";
    static final         String EXTRA_DATE_FILENAME = "ImageFileName";

    private SelfieViewAdapter imageListAdapter;
    private ListView          imageListView;

    private SharedPreferences prefs;
    private        long   lastSelfieTime    = 0;                       // Last time a selphie was taken
    private static String LAST_SELFIE_TICKS = "last_selfie_ticks";

    static final int MIN_SELFIE_TIMEOUT = 2 * 60 * 1000;  // Ask for selfie every two minutes

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Expand our view now and obtain a reference to the image ListView

        setContentView(R.layout.activity_main);
        imageListView = (ListView) findViewById(R.id.listView);

        // Load last time a selfie was made from shared preference storage

        prefs = getPreferences(MODE_PRIVATE);

        lastSelfieTime = prefs.getLong(LAST_SELFIE_TICKS, 0);

        imageListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                launchImageViewActivity(parent, view, position, id);
            }
        });

        imageListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                // TODO: [OPTIONAL] Present dialog asking if this item should be deleted

                return false;
            }
        });
        // Create a listview adapter to handle the selfie records and register it with the
        // main listview

        imageListAdapter = new SelfieViewAdapter(getApplicationContext());
        imageListView.setAdapter(imageListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            return launchCameraApp();    // Launch Camera
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        // TODO: restore selfie records from persistent storage

    }

    @Override
    public void onPause()
    {
        super.onPause();

        // TODO: save off selfie records to persistent storage

    }

    /**
     * Launch an already installed camera application to take a selfie for us.  The
     * Intent will save the photo to a file we supply and return a bitmap image
     *
     * @return true if successfully launched
     */
    private boolean launchCameraApp()
    {
        Log.i(TAG, "launchCameraApp: entered");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            try
            {
                // Create the file that should hold the new photo

                Date imageDate = Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime();
                File imageFile = SelfieRecord.CreateImageFile(imageDate);

                takePictureIntent.putExtra(EXTRA_DATE_TAKEN, imageDate);
                takePictureIntent.putExtra(EXTRA_DATE_FILENAME, imageFile.toString());
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
        Log.i(TAG,
              "onActivityResult: entered, request: " + requestCode + ", result: " + resultCode);

        if (requestCode == REQUEST_TAKE_PHOTO)
        {
            if (resultCode == RESULT_OK)
            {
                Bundle extras = data.getExtras();
                Bitmap thumbnail = (Bitmap) extras.get("data");
                Date imageDate = (Date) extras.get(EXTRA_DATE_TAKEN);
                String fileName = extras.getString(EXTRA_DATE_FILENAME);

                SelfieRecord item = new SelfieRecord(imageDate, fileName, thumbnail);

                imageListAdapter.add(item);

                // Update last selfie time

                lastSelfieTime = imageDate.getTime();

                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong(LAST_SELFIE_TICKS, lastSelfieTime);
                editor.commit();
            }
        }
        // Reschedule notify update

        scheduleSelfieNotification();
    }

    private void scheduleSelfieNotification()
    {
        Log.i(TAG, "scheduleSelfieNotification: entered");

        // lastSelfieTime  <- ticks last one taken at
        // int MIN_SELFIE_TIMEOUT = 2 * 60 * 1000;  // Ask for selfie every two minutes
        // TODO: Schedule a notification to show a new activity

    }

    /**
     * Handle click of image List View item
     *
     * @param view
     */
    private void launchImageViewActivity(AdapterView<?> parent, View view, int position, long id)
    {
        Log.i(TAG, "launchImageViewActivity: entered, position: " + position + ", id: " + id);

        Object item = imageListAdapter.getItem(position);

        // TODO: Implement.  Should launch new activity.
        // TODO: this activity will show selected image, update title, and handle swipes
        // TODO: back button (no matter how many swipes) returns to this activity
    }
}
