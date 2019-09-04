package net.intigral.logger.base;

import java.util.List;

/**
 * Created by Simon Gerges on 10/3/16.
 * <p>
 */

public interface RemoteLoggerClient {

    /**
     * Set the server URL that will send logs to it
     * @param serverURL logs server url
     */
    void setServerURL(String serverURL);

    /**
     * Any header need to be sent to server with each flush request, Ec. user identifier
     * @param headerKey header key
     * @param headerValue header value
     */
    void addHeader(String headerKey, String headerValue);

    /**
     * send a list of logs records to the server, and notify the caller through {@link LogsFlushListener}
     * @param logRecords list of log records to be send
     * @param flushListener a call back interface to update the caller with the status
     */
    void flushLogs(List<String> logRecords, final LogsFlushListener flushListener);

    interface LogsFlushListener {

        /**
         * Will be called after flush attempted was happened, and will pass the status.
         * @param isSuccess true if logs submitted successfully
         */
        void onFlushFinished(boolean isSuccess);
    }
}
