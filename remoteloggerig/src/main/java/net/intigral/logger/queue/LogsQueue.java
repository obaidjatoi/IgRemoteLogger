package net.intigral.logger.queue;

import java.util.List;

/**
 * Created by Simon Gerges on 9/22/16.
 * <p>
 */

public interface LogsQueue {

    /**
     * Is the queue is empty
     * @return is queue empty or not
     */
    boolean isEmpty();

    /**
     * retrieve the size of the logs queue
     * @return the number of items inside the queue
     */
    int size();

    /**
     * Add specific log record with a time stamp
     * @param timeStamp
     * @param logRecord
     */
    void add(long timeStamp, String logRecord);

    /**
     * This method is to fill the existing records to an existing records, and return the last record time stamp
     * @param listToFillIn - pass by ref param to fill data on it
     * @param maxBatchSize - limit the results to a specific number, to avoid send to the server a big bunch of data at once, or -1 to not limit
     * @return the last record time stamp that will be used to flush after success
     */
    long loadExistingRecordsToList(List<String> listToFillIn, int maxBatchSize);

    /**
     * Remove all log records that before an existing time stamp
     * @param timeStamp to remove all records before it
     * @return number of removed ros
     */
    int removeLogsBefore(long timeStamp);
}
