package com.tomato.shine;

import android.text.TextUtils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yeshuxin on 17-1-10.
 */

public class DownloadTaskInfo {

    public static final int TASK_UNCOMPLETE = 0;
    public static final int TASK_COMPLETE = 1;

    private String mFileId;
    private String mUrl;
    private String mFilePath;
    private int mIsCompleted;
    private String mMd5;
    private static AtomicInteger mNumber = new AtomicInteger();
    private final String PREFIX = "mi_shop_";

    public DownloadTaskInfo(String url, String savedFile) {
        this(url, savedFile, TASK_UNCOMPLETE);
    }

    public DownloadTaskInfo(String url, String savedFile, String md5) {
        this(url, savedFile, md5, TASK_UNCOMPLETE);
    }

    public DownloadTaskInfo(String url, String savedFile, int isCompleted) {
        this(url, savedFile, "", isCompleted);
    }

    public DownloadTaskInfo(String url, String fileName, String md5,
                            int isComplete) {
        this.mUrl = url;
        this.mFilePath = fileName;
        this.mMd5 = md5;
        this.mIsCompleted = isComplete;
        this.mFileId = PREFIX + mNumber.getAndIncrement();

    }

    public String getFileId() {
        if (TextUtils.isEmpty(mFileId)) {
            mFileId = "";
        }
        return mFileId;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public int getIsCompleted() {
        return mIsCompleted;
    }

    public String getMd5() {
        if (TextUtils.isEmpty(mMd5)) {
            mMd5 = "";
        }
        return mMd5;
    }

}
