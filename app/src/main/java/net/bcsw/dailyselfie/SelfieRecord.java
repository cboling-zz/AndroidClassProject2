package net.bcsw.dailyselfie;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by cboling on 3/16/2015.
 */
public class SelfieRecord
{
    private static final String           TAG        = "SelfieRecord";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMDD_hhmmss");

    // Selfie thumbnail
    //private Bitmap thumbnail = null;

    // Date Taken (UTC)
    private Date dateTaken;

    // Filename is UTC date & time formatted
    private String filename;

    // Default thumbnail if image not found

    private static Bitmap stubBitmap = null;

    public SelfieRecord()
    {
        Log.i(TAG, "default ctor: entered");
    }

    /**
     * Recreate a selfie record
     * <p/>
     * This constructor is often called whenever we recreate a selfie record that was saved to
     * persistent storage (between application restarts)
     *
     * @param utcDate
     * @param imgFile
     * @param picture
     */
    public SelfieRecord(Date utcDate, String imgFile, Bitmap picture)
    {
        Log.i(TAG, "picture and date ctor: entered");
        //thumbnail = picture;
        dateTaken = utcDate;
        filename = imgFile;
    }

    //    public Bitmap getThumbnail()
    //    {
    //        return thumbnail;
    //    }

    public String getImageFileName()
    {
        return filename;
    }

    public Date getDateTaken()
    {
        return dateTaken;
    }

    /**
     * Get a nicely formated date containing Month, Day, Year, Hour, Minutes with AM/PM indicator.
     */
    public String getDateTakenString()
    {
        // Print in local form closest to Month Day Year <space> Hour Minutes AM/PM

        String formatString = DateFormat.getBestDateTimePattern(Locale.getDefault().getDefault(),
                                                                "MMddyyyy hmm a");
        SimpleDateFormat format = new SimpleDateFormat(formatString);
        format.setTimeZone(Calendar.getInstance().getTimeZone());

        return format.format(dateTaken);
    }

    @Override
    public String toString()
    {
        return "Selphie: Date Taken: " + getDateTaken().toString() +
               ", File: " + getImageFileName();
    }

    public void restore() throws IOException
    {
        File file = getFilePath();

        // TODO: Implement
    }

    private File getFilePath() throws IOException
    {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        return new File(path, filename);
    }

    /**
     * Get a custom bitmap of this record scaled for a particular image view
     *
     * @param view Image View to scale picture to
     * @return Resulting bitmap
     */
    public Bitmap getThumbnail(ImageView view)
    {
        // Get path to the file (throws exception if not found)

        String filePath = getImageFileName();

        // Get the dimensions of the View

        int targetW = view.getWidth();
        int targetH = view.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(filePath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;           // Ignored in level 21+ (Lollipop)

        Bitmap bitmap = BitmapFactory.decodeFile(filePath, bmOptions);

        if (bitmap == null)
        {
            Log.w(TAG, "getThumbnail: Bitmap decode error on file '" + getImageFileName() + "'");

            if (stubBitmap == null)
            {
                stubBitmap = BitmapFactory.decodeResource(view.getResources(), R.drawable.stub);
            }
            bitmap = stubBitmap;
        }
        return bitmap;
    }

    public static File CreateImageFile(Date date) throws IOException
    {
        // Create an image file name

        String imageFileName = dateFormat.format(date);
        File fileDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        // Make sure the directory exists for writing

        fileDir.mkdirs();

        return File.createTempFile(imageFileName, ".jpg", fileDir);
    }

    /**
     * Provide a common 'get current time' method for selfie records so time comparisons are more
     * uniform.
     *
     * @return
     */
    public static Date GetCurrentTime()
    {
        return Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime();
    }
}
