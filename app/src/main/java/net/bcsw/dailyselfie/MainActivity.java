package net.bcsw.dailyselfie;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity
{
    private static final String TAG                   = "MainActivity";
    static final         int    REQUEST_IMAGE_CAPTURE = 1;

    private SelphieViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
     * Launch an already installed camera application to take a selfie for us
     *
     * @return true if successfully launched
     */
    private boolean launchCameraApp()
    {
        Log.i(TAG, "launchCameraApp: entered");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            Log.i(TAG, "launchCameraApp: starting camera activity for result");
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            return true;
        }
        return false;
    }

    /**
     * Handle the response from any activities that return an result
     *
     * @param requestCode Request code for activity0
     * @param resultCode  The result
     * @param data        associated data with result
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i(TAG,
              "onActivityResult: entered, request: " + requestCode + ", result: " + resultCode);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
            Bitmap thumbnail = (Bitmap) extras.get("data");

            SelfieRecord item = new SelfieRecord(thumbnail);

            mAdapter.add(item);
        }
    }
}
