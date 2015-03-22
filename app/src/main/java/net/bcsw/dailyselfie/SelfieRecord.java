package net.bcsw.dailyselfie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateFormat;
import android.util.Log;

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
public class SelfieRecord implements Parcelable
{
    private static final String           TAG        = "SelfieRecord";
    private static final SimpleDateFormat fileFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
    private static final String           dateString = DateFormat.getBestDateTimePattern(
            Locale.getDefault().getDefault(), "MMddyyyy hmm a");
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(dateString);

    // Thumbnail cache

    private Bitmap thumbnail   = null;
    private int    thumbWidth  = 0;
    private int    thumbHeight = 0;

    // Default thumbnail if image not found

    private static Bitmap stubBitmap = null;

    ///////////////////////////////////////////////////////////////////////////
    // Non-volatile data saved to SQLite database

    // Database unique index (ID_COLUMN).  The index is always updated with whatever the
    // database provides when the record is entered
    private long index = 0;

    // Date Taken (UTC)
    private Date dateTaken;

    // Filename is UTC date & time formatted
    private String filename;

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
     */
    public SelfieRecord(Date utcDate, String imgFile)
    {
        Log.d(TAG, "picture and date ctor: entered");
        dateTaken = utcDate;
        filename = imgFile;
    }

    public long getId()
    {
        return index;
    }

    public void setId(long id)
    {
        index = id;
    }

    public long getDateTaken()
    {
        return dateTaken.getTime();
    }

    public void setDateTaken(long time)
    {
        dateTaken = new Date(time);
    }

    public String getImageFileName()
    {
        return filename;
    }

    public void setImageFileName(String name)
    {
        filename = name;
    }

    /**
     * Get a nicely formatted date containing Month, Day, Year, Hour, Minutes with AM/PM indicator.
     */
    public String getDateTakenString()
    {
        // Always adjust displayed string to current timezone where device was on start or
        // new selfie creation/refresh.

        dateFormat.setTimeZone(Calendar.getInstance().getTimeZone());
        return dateFormat.format(dateTaken);
    }

    @Override
    public String toString()
    {
        return "Date: " + dateTaken.toString() +
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
     * @return Resulting scaled bitmap
     */
    public Bitmap getThumbnail(Context context, int height, int width)
    {
        Log.d(TAG, "getThumbnail: entered");

        // Get the dimensions of the View

        if (thumbnail == null || thumbWidth != width || thumbHeight != height)
        {
            thumbnail = null;

            if (width > 0 && height > 0)
            {
                // Get path to the file (throws exception if not found)

                String filePath = getImageFileName();

                // Get the dimensions of the bitmap
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;

                BitmapFactory.decodeFile(filePath, bmOptions);

                int photoW = bmOptions.outWidth;
                int photoH = bmOptions.outHeight;

                // Determine how much to scale down the image

                int scaleFactor = Math.min(photoW / width, photoH / height);

                // Decode the image file into a Bitmap sized to fill the View

                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;
                bmOptions.inPurgeable = true;           // Ignored in level 21+ (Lollipop)

                thumbnail = BitmapFactory.decodeFile(filePath, bmOptions);
            }
            if (thumbnail == null)
            {
                Log.w(TAG,
                      "getThumbnail: Bitmap decode error on file '" + getImageFileName() + "'");

                if (stubBitmap == null)
                {
                    stubBitmap = BitmapFactory.decodeResource(context.getResources(),
                                                              R.drawable.stub);
                    thumbWidth = stubBitmap.getWidth();
                    thumbHeight = stubBitmap.getHeight();
                }
                thumbnail = stubBitmap;
            }
            else
            {
                thumbWidth = width;
                thumbHeight = height;
            }
        }
        return thumbnail;
    }

    public static boolean HasStorage(boolean needWriteAccess)
    {
        String state = Environment.getExternalStorageState();
        Log.d(TAG, "storage state is " + state);

        if (Environment.MEDIA_MOUNTED.equals(state))
        {
            if (needWriteAccess)
            {
                File fileDir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES);
                fileDir.mkdirs();
                return fileDir.canWrite();
            }
            else
            {
                return true;
            }
        }
        else if (!needWriteAccess && Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
        {
            return true;
        }
        return false;
    }

    public static File CreateImageFile(Date date) throws IOException
    {
        // Create an image file name

        String imageFileName = "JPEG_" + fileFormat.format(date);
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

    ///////////////////////////////////////////////////////////////////////////
    // Parcelable methods

    private SelfieRecord(Parcel in)
    {
        dateTaken = new Date(in.readLong());
        filename = in.readString();
    }

    public int describeContents()
    {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags)
    {
        out.writeLong(dateTaken.getTime());
        out.writeString(filename);
    }

    public static final Parcelable.Creator<SelfieRecord> CREATOR = new Parcelable.Creator<SelfieRecord>()
    {
        public SelfieRecord createFromParcel(Parcel in)
        {
            return new SelfieRecord(in);
        }

        public SelfieRecord[] newArray(int size)
        {
            return new SelfieRecord[size];
        }
    };


}
