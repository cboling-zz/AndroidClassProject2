package net.bcsw.dailyselfie;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by cboling on 3/22/2015.
 */
public class SelfieRecordDataSource
{
    private final static String TAG = "SelfieDataSource";

    // Database fields
    private SQLiteDatabase             database;
    private SelfieRecordDatabaseHelper dbHelper;
    private String[] allColumns = {SelfieRecordDatabaseHelper.ID_COLUMN, SelfieRecordDatabaseHelper.DATE_COLUMN, SelfieRecordDatabaseHelper.FILE_COLUMN};

    public SelfieRecordDataSource(Context context)
    {
        dbHelper = new SelfieRecordDatabaseHelper(context);
    }

    public void open() throws SQLException
    {
        if (database == null)
        {
            database = dbHelper.getWritableDatabase();
        }
    }

    public void close()
    {
        dbHelper.close();
        database = null;
    }

    public SelfieRecord add(Date date, String imageFileName)
    {
        Log.d(TAG, "add: entered");
        ContentValues values = new ContentValues();

        values.put(SelfieRecordDatabaseHelper.DATE_COLUMN, date.getTime());
        values.put(SelfieRecordDatabaseHelper.FILE_COLUMN, imageFileName);

        long insertId = database.insert(SelfieRecordDatabaseHelper.TABLE_NAME, null, values);
        Cursor cursor = database.query(SelfieRecordDatabaseHelper.TABLE_NAME, allColumns,
                                       SelfieRecordDatabaseHelper.ID_COLUMN + " = " + insertId,
                                       null, null, null, null);
        cursor.moveToFirst();

        SelfieRecord newRecord = cursorToRecord(cursor);

        cursor.close();
        return newRecord;
    }

    public long add(SelfieRecord record)
    {
        Log.d(TAG, "add: entered");

        return add(new Date(record.getDateTaken()), record.getImageFileName()).getId();
    }

    public void remove(SelfieRecord record)
    {
        Log.d(TAG, "remove: entered");
        long id = record.getId();
        System.out.println("SelfieRecord deleted with id: " + id);
        database.delete(SelfieRecordDatabaseHelper.TABLE_NAME,
                        SelfieRecordDatabaseHelper.ID_COLUMN + " = " + id, null);
    }

    public List<SelfieRecord> getList()
    {
        Log.d(TAG, "getList: entered");
        List<SelfieRecord> records = new ArrayList<SelfieRecord>();

        Cursor cursor = database.query(SelfieRecordDatabaseHelper.TABLE_NAME, allColumns, null,
                                       null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            SelfieRecord record = cursorToRecord(cursor);
            records.add(record);
            cursor.moveToNext();
        }
        cursor.close();

        return records;
    }

    private SelfieRecord cursorToRecord(Cursor cursor)
    {
        long index = cursor.getLong(SelfieRecordDatabaseHelper.ID_COLUMN_INDEX);
        Date dateTaken = new Date(cursor.getLong(SelfieRecordDatabaseHelper.DATE_COLUMN_INDEX));
        String imageFile = cursor.getString(SelfieRecordDatabaseHelper.FILE_COLUMN_INDEX);

        SelfieRecord comment = new SelfieRecord(dateTaken, imageFile);
        comment.setId(index);

        return comment;
    }
}
