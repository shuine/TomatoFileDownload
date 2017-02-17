package com.tomato.shine;

/**
 * @author yeshuxin on 17-1-9.
 */

public class FileRequestConfig {

    public static final int PRIORITY_LOW = 1002;
    public static final int PRIORITY_NORMAL = 1001;
    public static final int PRIORRITY_HIGH = 1000;

    public boolean isWifiOnly;
    public int priority;

    public FileRequestConfig(boolean isWifiOnly, int priority) {
        this.isWifiOnly = isWifiOnly;
        this.priority = priority;
    }
}
