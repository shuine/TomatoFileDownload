package com.tomato.download;

/**
 * @author yeshuxin on 16-10-22.
 */

public class FileDownloadRequest implements Comparable<FileDownloadRequest>{

    public static final int PRIORITY_LOW = 1000;
    public static final int PRIORITY_NORMAL = 1001;
    public static final int PRIORITY_HIGH = 1002;

    private FileInfo mFileInfo;
    private DownloadCallback mDownloadCallback;
    private boolean mCancel =false;
    private FileDownloadQueue mQueue;
    private Integer mSequence;

    public FileDownloadRequest(FileInfo fileInfo){
        this(fileInfo,null);
    }

    public FileDownloadRequest(FileInfo fileInfo,DownloadCallback callback){
        this.mFileInfo = fileInfo;
        this.mDownloadCallback = callback;
    }

    public void setCallback(DownloadCallback callback){
        this.mDownloadCallback = callback;
    }

    public DownloadCallback getDownloadCallback(){
        return this.mDownloadCallback;
    }

    public FileInfo getFileInfo(){
        return this.mFileInfo;
    }

    public boolean isCancel(){
        return mCancel;
    }

    public FileDownloadRequest setRequestQueue(FileDownloadQueue queue){
        mQueue = queue;
        return this;
    }

    public FileDownloadRequest setSequence(Integer sequence){
        mSequence = sequence;
        return this;
    }

    public void finish(){
        mQueue.finish(this);
    }

    @Override
    public int compareTo(FileDownloadRequest others) {
        return this.mSequence - others.mSequence;
    }
}
