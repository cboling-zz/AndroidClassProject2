package net.bcsw.dailyselfie;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;


public class SelfieImageActivity extends Activity
{
    private static final String TAG = "SelfieImageActivity";

    private SelfieRecord record;
    private ImageView    imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate: entered");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selfie_image);

        // Recover the record and set up the image

        record = getIntent().getParcelableExtra(MainActivity.PARCELABLE_RECORD);
        imageView = (ImageView) findViewById(R.id.imageView);

        Bitmap bitmap = BitmapFactory.decodeFile(record.getImageFileName());
        imageView.setImageBitmap(bitmap);
    }
    //
    //    @Override
    //    public boolean onCreateOptionsMenu(Menu menu)
    //    {
    //        // Inflate the menu; this adds items to the action bar if it is present.
    //        getMenuInflater().inflate(R.menu.menu_selfie_image, menu);
    //        return true;
    //    }

    //    @Override
    //    public boolean onOptionsItemSelected(MenuItem item)
    //    {
    //        // Handle action bar item clicks here. The action bar will
    //        // automatically handle clicks on the Home/Up button, so long
    //        // as you specify a parent activity in AndroidManifest.xml.
    //        int id = item.getItemId();
    //
    //        //noinspection SimplifiableIfStatement
    //        if (id == R.id.action_settings)
    //        {
    //            return true;
    //        }
    //        return super.onOptionsItemSelected(item);
    //    }
}
