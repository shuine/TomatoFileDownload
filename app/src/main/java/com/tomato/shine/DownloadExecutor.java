package com.tomato.shine;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import java.util.concurrent.PriorityBlockingQueue;

/**
 * @author yeshuxin on 17-1-10.
 */

public class DownloadExecutor implements DownloadStatusListener{

    private Context mContext;
    private DownloadDispatcher mDispatcher;
    private Handler mHandler;
    //任务存储队列,线程安全
    private PriorityBlockingQueue<FileDownloadRequest> mRequestQueue;

    public DownloadExecutor(Context context){
        mContext = context;
        mRequestQueue = new PriorityBlockingQueue<>();
        mDispatcher = new DownloadDispatcher(this);
        mHandler = new Handler(Looper.getMainLooper());
    }

    public void addRequest(FileDownloadRequest request) {
        if (request != null && mRequestQueue != null) {
            mRequestQueue.add(request);
            performRequest();
        }
    }

    private synchronized void performRequest() {

        if (mRequestQueue != null && mRequestQueue.size() > 0) {
            try {
                FileDownloadRequest request = mRequestQueue.take();
                DownloadRunnable runnable = new DownloadRunnable(request, this);
                mDispatcher.enqueue(runnable);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 暂停所有的请求
     */
    public void pauseWifiOnlyRequest() {
        mDispatcher.pauseWifiOnlyRequest();
    }

    /**
     * 暂停单个下载任务
     * @param info
     * @return
     */
    public boolean pauseRequest(DownloadTaskInfo info){
        if(info == null || TextUtils.isEmpty(info.getUrl()) && TextUtils.isEmpty(info.getFileId())){
            return false;
        }
        return mDispatcher.pauseRequest(info.getFileId(),info.getUrl());
    }

    /**
     * 重新开始所有请求
     */
    public void resumeWifiOnlyAllRequest() {
        if (mRequestQueue != null && mRequestQueue.size() > 0) {
            performRequest();
        } else {
            mDispatcher.dispatch();
        }

        mDispatcher.resumeWifiOnlyRequest();
    }

    public boolean resumeRequest(DownloadTaskInfo info){
        if(info == null || TextUtils.isEmpty(info.getUrl()) && TextUtils.isEmpty(info.getFileId())){
            return false;
        }
        return mDispatcher.resumeRequest(info.getFileId(),info.getUrl());
    }

    @Override
    public void onDownloadSuccess(DownloadRunnable task) {
        if (task == null) {
            return;
        }
        if (task.getTaskInfo() != null ) {
//            TaskDataBase.getInstance(mCtx).updateTaskToComplete(task.getFileInfo().getUrl());
        }
        mDispatcher.finishTask(task);
        performRequest();

        //通知业务层
        if (task.getRequest() != null) {
            final DownloadCallback callback = task.getRequest().getCallback();
            final DownloadTaskInfo taskInfo = task.getTaskInfo();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (callback!= null && taskInfo != null) {
                        callback.onDownloadComplete(taskInfo.getFileId(),
                                taskInfo.getFilePath());
                    }
                }
            });
        }
    }

    @Override
    public void onDownloadFail(DownloadRunnable task, int error, String msg) {

        if (task == null) {
            return;
        }
        boolean willRetry = mDispatcher.failTask(task);
        if (!willRetry) {
            final ErrorType errorType;
            switch (error) {
                case DownloadRunnable.ERROR_CREATE_FILE:
                    errorType = ErrorFactory.getOtherType("文件创建失败 " + msg);
                    break;
                case DownloadRunnable.ERROR_INVALID_PARAM:
                    errorType = ErrorFactory.getOtherType("参数错误 " + msg);
                    break;
                case DownloadRunnable.ERROR_NETWORK:
                    errorType = ErrorFactory.getDownloadFailError("网络错误 " + msg);
                    break;
                case DownloadRunnable.ERROR_CHK_MD5:
                    errorType = ErrorFactory.getMd5CheckFailError("md5校验错误 " + msg);
                    break;
                default:
                    errorType = ErrorFactory.getOtherType("未知错误 " + msg);
                    break;
            }
            if (task.getRequest() != null) {
                final DownloadCallback callback = task.getRequest().getCallback();
                final DownloadTaskInfo taskInfo = task.getTaskInfo();
                if (callback != null && taskInfo != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDownloadFail(taskInfo.getFileId(), errorType);

                        }
                    });
                }
            }

        }
    }

    @Override
    public void onDownloadUpdate(DownloadRunnable task,final long length,final long totalLength) {
        if (task.getRequest() != null) {
            final DownloadCallback callback = task.getRequest().getCallback();
            final DownloadTaskInfo taskInfo = task.getTaskInfo();
            if (callback != null && taskInfo != null) {
                final float percent = length * 1.0f / totalLength;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onDownloadUpdate(taskInfo.getFileId(), percent, length, totalLength);
                    }
                });
            }

        }
    }

    @Override
    public void onDownloadPause(DownloadRunnable task) {

    }

    @Override
    public void onDownloadStart(DownloadRunnable task) {

    }

    public Context getContext(){
        return mContext;
    }

    public void downloadTaskComplete(){}
}
