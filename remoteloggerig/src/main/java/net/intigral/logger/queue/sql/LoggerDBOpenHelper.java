package net.intigral.logger.queue.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Simon Gerges on 6/8/16.
 * <p/>
 */
public class LoggerDBOpenHelper extends SQLiteOpenHelper implements LoggerDBContract {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "logs.db";

    private static LoggerDBOpenHelper instance;

    public static LoggerDBOpenHelper getInstance(Context context) {

        if (instance == null)
            instance = new LoggerDBOpenHelper(context);
        return instance;
    }

    private LoggerDBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(SQL_CREATE_LOGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // This database is only a cache for programs data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_LOGS);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
