package com.tomato.plugindownloader;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * @author yeshuxin on 16-11-29.
 */

public class DownloaderExecutor implements DownloadListener {

    private final int EXECUTOR_SIZE = 2;
    private ExecutorService mExecutor = Executors.newFixedThreadPool(EXECUTOR_SIZE);
    private static DownloaderExecutor mInstance;
    private WeakHashMap<FileRequest, FutureTask> mRunningTask;
    private WeakHashMap<FileRequest, FutureTask> mPauseTask;
    private DeliveryListener mDelivery;

    public static DownloaderExecutor getInstance() {
        if (mInstance == null) {
            synchronized (DownloaderExecutor.class) {
                if (mInstance == null) {
                    mInstance = new DownloaderExecutor();
                }
            }
        }
        return mInstance;
    }

    private DownloaderExecutor() {
        mRunningTask = new WeakHashMap<>();
        mPauseTask = new WeakHashMap<>();
        mDelivery = new DeliveryListener(new Handler(Looper.getMainLooper()));
    }

    public void downloadFile(FileRequest request, DownloadListener listener) {
        if (request == null || TextUtils.isEmpty(request.url)) {
            return;
        }
        if (mRunningTask.containsKey(request)) {
            return;
        }
        DownloadCallable callable = new DownloadCallable(request);
        if (listener != null) {
            request.mCallable = listener;
        }
        callable.setOnDownloadListener(this);
        FutureTask task = new FutureTask(callable);
        if (mExecutor != null) {
            mExecutor.submit(task);
        }
        mRunningTask.put(request, task);
    }

    public boolean cancelRequest(FileRequest request) {
        boolean result = false;
        if (mRunningTask.containsKey(request)) {
            FutureTask task = mRunningTask.get(request);
            result = task.cancel(true);
            if (task.isCancelled()) {
                FileUtils.deleteFile(request.path, request.fileName);
                mRunningTask.remove(request);
            }
            onCancelDownload(request);
        }

        return result;
    }

    public boolean pauseRequest(FileRequest request) {
        boolean result = false;
        if (mRunningTask.containsKey(request)) {
            FutureTask task = mRunningTask.get(request);
            result = task.cancel(true);
            onPauseDownload(request);
            mPauseTask.put(request, task);
            mRunningTask.remove(request);
        }
        return result;
    }

    public boolean resumeRequest(FileRequest request) {
        boolean result = false;
        if (mPauseTask.containsKey(request)) {
            FutureTask task = mPauseTask.get(request);
            if (task.isCancelled()) {
                mPauseTask.remove(request);
            }
            downloadFile(request,request.mCallable);
        }
        return result;
    }


    @Override
    public void onStartDownload(final FileRequest request) {

        if(request == null ||request.mCallable ==null){
            return;
        }
        if(mDelivery != null){
            mDelivery.deliveryCallback(request,DeliveryListener.TYPE_START);
        }
        Log.e("shine", "onStartDownload：" + request.fileName);
    }

    @Override
    public void onSuccessDownload(FileRequest request) {
        if(request == null ||request.mCallable ==null){
            return;
        }
        if(mDelivery != null){
            mDelivery.deliveryCallback(request,DeliveryListener.TYPE_SUCCESS);
        }
        Log.e("shine", "onSuccessDownload：" + request.fileName);
    }

    @Override
    public void onPauseDownload(FileRequest request) {
        if(request == null ||request.mCallable ==null){
            return;
        }
        if(mDelivery != null){
            mDelivery.deliveryCallback(request,DeliveryListener.TYPE_PAUSE);
        }
        Log.e("shine", "onPauseDownload：" + request.fileName);
    }

    @Override
    public void onFinishDownload(FileRequest request) {
        if(request == null ||request.mCallable ==null){
            return;
        }
        if(mDelivery != null){
            mDelivery.deliveryCallback(request,DeliveryListener.TYPE_FINISH);
        }
        Log.e("shine", "onFinishDownload：" + request.fileName);
    }

    @Override
    public void onCancelDownload(FileRequest request) {
        if(request == null ||request.mCallable ==null){
            return;
        }
        if(mDelivery != null){
            mDelivery.deliveryCallback(request,DeliveryListener.TYPE_CANCEL);
        }
        Log.e("shine", "onCancelDownload：" + request.fileName);
    }

    @Override
    public void onFailDownload(FileRequest request, int code, String msg) {
        if(request == null ||request.mCallable ==null){
            return;
        }
        if(mDelivery != null){
            mDelivery.deliveryFail(request,code,msg);
        }
        Log.e("shine", "onFailDownload：" + request.fileName);
    }

    @Override
    public void onUpdateProgress(FileRequest request, long current, long total) {
        if(request == null ||request.mCallable ==null){
            return;
        }
        if(mDelivery != null){
            mDelivery.deliveryUpdate(request,current,total);
        }
        Log.e("shine", "onUpdateProgress：" + request.fileName + current + "===" + total);
    }

}
