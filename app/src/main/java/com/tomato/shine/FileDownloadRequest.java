package com.tomato.shine;

import static com.tomato.shine.FileRequestConfig.PRIORITY_NORMAL;

/**
 * @author yeshuxin on 17-1-9.
 */

public class FileDownloadRequest implements Comparable<FileDownloadRequest>{

    private DownloadTaskInfo mTaskInfo;
    private FileRequestConfig mConfig;
    private DownloadCallback mCallback;

    public FileDownloadRequest(DownloadTaskInfo info) {
        this(info, new FileRequestConfig(true, PRIORITY_NORMAL));
    }

    public FileDownloadRequest(DownloadTaskInfo info, FileRequestConfig config) {
        this(info, config, null);
    }

    public FileDownloadRequest(DownloadTaskInfo info, FileRequestConfig config, DownloadCallback callback) {
        this.mTaskInfo = info;
        this.mConfig = config;
        this.mCallback = callback;
    }


    public void setCallback(DownloadCallback callback) {
        this.mCallback = callback;
    }

    public DownloadCallback getCallback() {
        return this.mCallback;
    }

    public DownloadTaskInfo getTaskInfo() {
        return mTaskInfo;
    }

    public int getPriority() {
        if (mConfig != null) {
            return mConfig.priority;
        }
        return PRIORITY_NORMAL;
    }

    public boolean isWifiOnly() {
        if (mConfig != null) {
            return mConfig.isWifiOnly;
        }
        return false;
    }

    @Override
    public int compareTo(FileDownloadRequest another) {

        //用于DowloadExecutor中,加入Queue中进行优先级判断
        //// TODO: 17-1-11 优先级判断
        return 0;
    }
}
