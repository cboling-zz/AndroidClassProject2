package net.bcsw.dailyselfie;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

/**
 * Created by cboling on 3/21/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
    final static String   TABLE_NAME  = "selfies";
    final static String   _ID         = "_id";
    final static String   DATE_COLUMN = "date";
    final static String   FILE_COLUMN = "filename";
    final static String[] columns     = {_ID, DATE_COLUMN, FILE_COLUMN};

    final private static String CREATE_CMD =

            "CREATE TABLE artists (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + DATE_COLUMN +
            " INTEGER NOT NULL" + FILE_COLUMN + " TEXT NOT NULL)";

    final private static Integer VERSION = 1;
    final private Context context;

    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/dailyselfie/databases/";
    private static String DB_NAME = "dailySelfie_db";

    private SQLiteDatabase database;

    /**
     * Constructor Takes and keeps a reference of the passed context in order to access to the
     * application assets and resources.
     *
     * @param context
     */
    public DatabaseHelper(Context context)
    {
        super(context, DB_NAME, null, 1);
        this.context = context;
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     */
    public void createDataBase() throws IOException
    {
        boolean dbExist = checkDataBase();

        if (dbExist)
        {
            //do nothing - database already exist
        }
        else
        {
            // By calling this method and empty database will be created into the default system path of your application so we are gonna be able to overwrite that database with our database.

            this.getReadableDatabase();

            try
            {
                copyDataBase();
            }
            catch (IOException e)
            {
                throw new Error("Error copying database");
            }
        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the
     * application.
     *
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase()
    {
        SQLiteDatabase checkDB = null;

        try
        {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }
        catch (SQLiteException e)
        {
            //database doesn't exist yet.
        }
        if (checkDB != null)
        {
            checkDB.close();
        }
        return (checkDB != null);
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled. This is done by transfering
     * bytestream.
     */
    private void copyDataBase() throws IOException
    {
        //Open your local db as the input stream

        InputStream input = context.getAssets().open(DB_NAME);

        // Path to the just created empty db

        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream

        OutputStream output = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile

        byte[] buffer = new byte[1024];
        int length;
        while ((length = input.read(buffer)) > 0)
        {
            output.write(buffer, 0, length);
        }
        //Close the streams

        output.flush();
        output.close();
        input.close();
    }

    public void openDataBase() throws SQLException
    {
        // Open the database
        String myPath = DB_PATH + DB_NAME;
        database = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close()
    {
        if (database != null)
        {
            database.close();
        }

        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
    }

    // Add your public helper methods to access and get content from the database.
    // You could return cursors by doing "return database.query(....)" so it'd be easy
    // to you to create adapters for your views.

}
