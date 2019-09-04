package net.intigral.logger.queue.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import net.intigral.logger.queue.LogsQueue;

import java.util.List;

import static net.intigral.logger.queue.sql.LoggerDBContract.LogEntry.COLUMN_NAME_LOG_RECORD;
import static net.intigral.logger.queue.sql.LoggerDBContract.LogEntry.COLUMN_NAME_TIME_STAMP;
import static net.intigral.logger.queue.sql.LoggerDBContract.LogEntry.TABLE_NAME;

/**
 * Created by Simon Gerges on 9/22/16.
 * <p>
 *     A {@link LogsQueue} implementation that save log records to SQLite database
 */

public class SQLiteLogsQueue implements LogsQueue {

    private LoggerDBOpenHelper mDbHelper;

    public SQLiteLogsQueue(Context context) {

        mDbHelper = LoggerDBOpenHelper.getInstance(context);
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public int size() {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
    }

    @Override
    public void add(long timeStamp, String logRecord) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME_TIME_STAMP, timeStamp);
        contentValues.put(COLUMN_NAME_LOG_RECORD, logRecord);

        db.insertWithOnConflict(TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    @Override
    public long loadExistingRecordsToList(List<String> listToFillIn, int maxBatchSize) {

        String limit = null;
        if(maxBatchSize > 0)
            limit = String.valueOf(maxBatchSize);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, COLUMN_NAME_TIME_STAMP, limit);

        long lastTimeStamp = 0;
        if (cursor.moveToFirst()) {
            do {

                listToFillIn.add(cursor.getString(cursor.getColumnIndex(COLUMN_NAME_LOG_RECORD)));
                lastTimeStamp = cursor.getLong(cursor.getColumnIndex(COLUMN_NAME_TIME_STAMP));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return lastTimeStamp;
    }


    @Override
    public int removeLogsBefore(long timeStamp) {

        String whereClause = COLUMN_NAME_TIME_STAMP + " <= " + timeStamp;
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int deletedRowsCount = db.delete(TABLE_NAME, whereClause, null);
        return deletedRowsCount;
    }
}
