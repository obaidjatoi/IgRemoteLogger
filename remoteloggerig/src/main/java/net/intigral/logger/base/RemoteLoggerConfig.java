package net.intigral.logger.base;

import java.io.Serializable;

/**
 * Created by Simon Gerges on 9/22/16.
 * <p>
 */

public class RemoteLoggerConfig implements Serializable {

    private boolean isAPIEnabled;
    private boolean isConsoleEnabled;
    private int batchSize;
    private int flushIntervalSeconds;
    private String loggerServerURL;


    public RemoteLoggerConfig() {

    }

    public boolean isAPIEnabled() {
        return isAPIEnabled;
    }

    public void setAPIEnabled(boolean isAPIEnabled) {
        this.isAPIEnabled = isAPIEnabled;
    }

    public boolean isConsoleEnabled() {
        return isConsoleEnabled;
    }

    public void setConsoleEnabled(boolean consoleEnabled) {
        isConsoleEnabled = consoleEnabled;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getFlushIntervalSeconds() {
        return flushIntervalSeconds;
    }

    public void setFlushIntervalSeconds(int flushIntervalSeconds) {
        this.flushIntervalSeconds = flushIntervalSeconds;
    }

    public String getLoggerServerURL() {
        return loggerServerURL;
    }

    public void setLoggerServerURL(String loggerServerURL) {
        this.loggerServerURL = loggerServerURL;
    }

    public boolean isFullyDisabled() {
        return !isAPIEnabled && !isConsoleEnabled;
    }
}
