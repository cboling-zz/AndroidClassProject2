package net.bcsw.dailyselfie;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by cboling on 3/16/2015.
 */
public class SelfieRecord
{
    private static final String TAG       = "SelfieRecord";
    // Selfie thumbnail
    private              Bitmap thumbnail = null;

    // Date Taken (UTC)
    private Date dateTaken = new Date(Calendar.getInstance().getTimeInMillis());

    // Filename is UTC date & time formatted
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMDD_hhmmss");
    private String filename;

    public SelfieRecord()
    {
        Log.i(TAG, "default ctor: entered");
    }

    public SelfieRecord(Date utcDate, String imgFile, Bitmap picture)
    {
        Log.i(TAG, "picture and date ctor: entered");
        thumbnail = picture;
        dateTaken = utcDate;
        filename = imgFile;
    }

    public Bitmap getThumbnail()
    {
        return thumbnail;
    }

    public Date getDateTaken()
    {
        return dateTaken;
    }

    public String getImageFileName()
    {
        return filename;
    }

    @Override
    public String toString()
    {
        return "Selphie: Date Taken: " + getDateTaken().toString() +
               ", File: " + getImageFileName();
    }

    public void restore()
    {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File file = new File(path, filename);
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
}
