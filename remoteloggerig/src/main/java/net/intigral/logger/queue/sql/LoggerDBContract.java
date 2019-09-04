package net.intigral.logger.queue.sql;

/**
 * Created by Simon Gerges on 9/25/16.
 * <p>
 */

public interface LoggerDBContract {

    String TEXT_TYPE = " TEXT";
    String INTEGER_TYPE = " INTEGER";
    String COMMA_SEP = ",";
    String SQL_CREATE_LOGS =
            "CREATE TABLE " + LogEntry.TABLE_NAME  + " (" +

                    LogEntry.COLUMN_NAME_TIME_STAMP     + INTEGER_TYPE + " PRIMARY KEY" + COMMA_SEP +
                    LogEntry.COLUMN_NAME_LOG_RECORD     + TEXT_TYPE +

                    " )";

    String SQL_DELETE_LOGS =
            "DROP TABLE IF EXISTS " + LogEntry.TABLE_NAME;

    interface LogEntry {

        String TABLE_NAME                   = "log_records";
        String COLUMN_NAME_TIME_STAMP       = "time_stamp";
        String COLUMN_NAME_LOG_RECORD       = "log_record";
    }
}
