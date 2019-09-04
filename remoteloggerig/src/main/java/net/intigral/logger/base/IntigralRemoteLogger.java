package net.intigral.logger.base;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

import net.intigral.logger.queue.LogsQueue;
import net.intigral.logger.queue.sql.SQLiteLogsQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Simon Gerges on 9/22/16.
 * <p>
 */

public class IntigralRemoteLogger {

    public static final String LOG_TAG = "RemoteLogger";
    /*
     * Gets the number of available cores
     */
    private static int NUMBER_OF_CORES =
            Runtime.getRuntime().availableProcessors();

    ///////////////////////////////////////////////////////////////////////////
    // Singleton
    ///////////////////////////////////////////////////////////////////////////

    private static IntigralRemoteLogger instance;
    private final ThreadPoolExecutor mLoggerThreadPool;
    private RemoteLoggerClient loggerClient;

    public static IntigralRemoteLogger getInstance() {

        if (instance == null)
            instance = new IntigralRemoteLogger();
        return instance;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Class Impl
    ///////////////////////////////////////////////////////////////////////////

    private LogsQueue queuedEvents;
    private long lastFlushAttempt;
    private Timer flushTimer;
    private RemoteLoggerConfig loggerConfig;

    private IntigralRemoteLogger() {

        // A queue of Runnable
        // Instantiates the queue of Runnable as a LinkedBlockingQueue
        BlockingQueue<Runnable> runablesQueue = new LinkedBlockingQueue<Runnable>();

        mLoggerThreadPool = new ThreadPoolExecutor(
                NUMBER_OF_CORES,       // Initial pool size
                NUMBER_OF_CORES,       // Max pool size
                1,
                TimeUnit.SECONDS,
                runablesQueue);
    }

    public void init(Context context, RemoteLoggerConfig config) {

        queuedEvents = new SQLiteLogsQueue(context.getApplicationContext());
        loggerClient = RemoteLoggerClientImpl.getInstance();
        restartWithConfig(config);
    }

    private synchronized void maybeFlushQueue() {

        if (queuedEvents.isEmpty())
            return;

        //Flush the queue in the background
        mLoggerThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                flushQueue();
            }
        });
    }

    private void flushQueue() {

        final long now = System.currentTimeMillis();
        //load pending event
        List<String> logRecords = new ArrayList<>();
        final long lastItemTimeStamp = queuedEvents.loadExistingRecordsToList(logRecords, loggerConfig.getBatchSize());

        if (!logRecords.isEmpty()) {
            Log.d(LOG_TAG, "Flushing: " + logRecords.size() + " log events.");

            //flush those records
            if (loggerClient != null) {

                loggerClient.flushLogs(logRecords, new RemoteLoggerClient.LogsFlushListener() {
                    @Override
                    public void onFlushFinished(boolean isSuccess) {

                        if (isSuccess) {
                            Log.d(LOG_TAG, "Flushing done successfully");
                            Log.d(LOG_TAG, "Going to remove flushed records");
                            int removeCount = queuedEvents.removeLogsBefore(lastItemTimeStamp);
                            Log.d(LOG_TAG, removeCount + " records removed");
                            Log.d(LOG_TAG, "Flushing logs done successfully");
                        } else {
                            Log.e(LOG_TAG, "Failed to flush log records [" + (System.currentTimeMillis() - now) + "]");
                        }
                    }
                });
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Public methods
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Add the log event to the logging queue in background thread
     *
     * @param timeStamp the timestamp
     * @param logRecord the the log record string to be added
     */
    public void addLogEvent(final long timeStamp, final String logRecord) {

        if (loggerConfig != null && loggerConfig.isFullyDisabled())
            return;

        mLoggerThreadPool.execute(new Runnable() {
            @Override
            public void run() {

                long now = System.currentTimeMillis();
                queuedEvents.add(timeStamp, logRecord);
                Log.d(LOG_TAG, "Log Event added [" + (System.currentTimeMillis() - now) + " ms]");
            }
        });
    }

    /**
     * Restart the timer and the client with specific configurations
     *
     * @param config new values need to be applied
     */
    public void restartWithConfig(RemoteLoggerConfig config) {

        if (flushTimer != null) {
            flushTimer.cancel();
            flushTimer.purge();
        }

        if (config != null)
            loggerConfig = config;

        if (loggerConfig == null || loggerConfig.isFullyDisabled())
            return;

        if (loggerClient != null)
            loggerClient.setServerURL(loggerConfig.getLoggerServerURL());

        long flushInterval = loggerConfig.getFlushIntervalSeconds() * DateUtils.SECOND_IN_MILLIS;

        flushTimer = new Timer();
        flushTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    maybeFlushQueue();
                } catch (RuntimeException ex) {
                    ex.printStackTrace();
                }
            }
        }, flushInterval, flushInterval);
    }

    public void addHeaders(String headerKey, String headerValue) {

        if (loggerClient != null)
            loggerClient.addHeader(headerKey, headerValue);
    }
}
