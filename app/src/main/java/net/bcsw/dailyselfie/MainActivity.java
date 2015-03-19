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
import android.widget.ListView;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

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

    private SelfieViewAdapter mAdapter;
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

        imageListView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                launchImageViewActivity(view);
            }
        });
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

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_camera)
        {
            return launchCameraApp();    // Launch Camera
        }
        return super.onOptionsItemSelected(item);
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

                Date imageDate = new Date(Calendar.getInstance().getTimeInMillis());
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

                // TODO: Also extract filename

                SelfieRecord item = new SelfieRecord(imageDate, fileName, thumbnail);

                mAdapter.add(item);

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
        // lastSelfieTime  <- ticks last one taken at
        // int MIN_SELFIE_TIMEOUT = 2 * 60 * 1000;  // Ask for selfie every two minutes
        // TODO: Schedule a notification to show a new activity

    }

    /**
     * Handle click of image List View item
     *
     * @param view
     */
    private void launchImageViewActivity(View view)
    {
        // TODO: Implement.  Should launch new activity.
        // TODO: this activity will show selected image, update title, and handle swipes
        // TODO: back button (no matter how many swipes) returns to this activity
    }
}
