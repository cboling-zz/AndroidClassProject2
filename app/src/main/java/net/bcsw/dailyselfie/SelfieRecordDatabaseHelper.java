package net.bcsw.dailyselfie;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by cboling on 3/21/2015.
 */
public class SelfieRecordDatabaseHelper extends SQLiteOpenHelper
{
    final static String TAG         = "DbSelfieHelper";
    final static String TABLE_NAME  = "selfieRecords";
    final static String ID_COLUMN   = "_id";  // Always try and use ID_COLUMN as primary key
    final static String DATE_COLUMN = "dateTaken";
    final static String FILE_COLUMN = "imageFilename";

    final static String[] columns = {ID_COLUMN, DATE_COLUMN, FILE_COLUMN};

    final static int ID_COLUMN_INDEX   = 0;
    final static int DATE_COLUMN_INDEX = 1;
    final static int FILE_COLUMN_INDEX = 2;

    final private static String CREATE_CMD = "CREATE TABLE " + TABLE_NAME +
                                             "(" + ID_COLUMN +
                                             " INTEGER PRIMARY KEY AUTOINCREMENT" +
                                             "," + DATE_COLUMN + " INTEGER NOT NULL" +
                                             "," + FILE_COLUMN + " TEXT NOT NULL)";

    final private static Integer DB_VERSION = 1;

    // The Android's default system path of your application database.

    private final static String DB_NAME = "dailySelfie.db";
    private final Context context;
    private final String  databasePath;

    /**
     * Constructor Takes and keeps a reference of the passed context in order to access to the
     * application assets and resources.
     *
     * @param context
     */
    public SelfieRecordDatabaseHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
        Log.i(TAG, "ctor: DB Name: " + DB_NAME + ", Version: " + DB_VERSION);

        databasePath = context.getDatabasePath(DB_NAME).toString();
        this.context = context;
    }

    /**
     * Called by the framework if the database is accessed but not yet created
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Log.i(TAG, "onCreate: entered");

        try
        {
            db.execSQL(CREATE_CMD);
        }
        catch (SQLiteException e)
        {
            // Failed
            Log.w(TAG, "onCreate: DB Create failed: " + e);
            throw e;
        }
    }

    /**
     * Called if the database version is increased in our application code.
     * <p/>
     * This method allows you to update an existing database schema or to drop the existing database
     * and recreate it via the onCreate() method.
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Since this is a class project, we do not need to worry about old data.  Just drop and
        // recreate the DB table on schema upgrade

        Log.w(TAG, "Upgrading database from version " + oldVersion +
                   " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        onCreate(db);
    }
}
