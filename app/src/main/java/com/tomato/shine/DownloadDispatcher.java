package com.tomato.shine;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * @author yeshuxin on 17-1-10.
 */

public class DownloadDispatcher {

    public static final int THREAD_POOL_SIZE = 2;
    public static final int RETRY_TIMES = 2;

    private ExecutorService mExecutorService = Executors.newCachedThreadPool();

    private DownloadExecutor mDownloadClient;
    private Context mContext;
    //暂停的任务队列
    private PriorityBlockingQueue<DownloadRunnable> mPausedTasks = new PriorityBlockingQueue<>();
    //ready状态的任务队列
    private PriorityBlockingQueue<DownloadRunnable> mPendingTasks = new PriorityBlockingQueue<>();
    //正在执行的任务队列
    private PriorityBlockingQueue<DownloadRunnable> mRunningTasks = new PriorityBlockingQueue<>();

    private HashMap<String, Integer> mFailMap = new HashMap<>();

    public DownloadDispatcher(DownloadExecutor excutor) {
        mDownloadClient = excutor;
        mContext = excutor.getContext();
    }

    /**
     * 将runnable放入队列，如果runnable是wifi only，而当前环境没有wifi，则放入pause队列，
     * 否则放入ready队列
     *
     * @param runnable
     */
    public void enqueue(DownloadRunnable runnable) {
        if (runnable != null) {
            //如果是重复的任务，直接return
            if (isDuplicatedTask(runnable)) {
                return;
            }

            if (!isWifiOn() && runnable.isWifiOnly() && !mPausedTasks.contains(runnable)) {
                mPausedTasks.add(runnable);
                return;
            }
            runnable.enqueue();
            if (!mPendingTasks.contains(runnable)) {
                mPendingTasks.add(runnable);
            }
            dispatch();
        }

    }


    public void dispatch() {
        if (mRunningTasks.size() >= THREAD_POOL_SIZE) {
            return;
        }

        while (mPendingTasks.size() > 0) {
            DownloadRunnable runnable = mPendingTasks.poll();
            if (runnable != null) {
                if (!isWifiOn() && runnable.isWifiOnly()) {
                    mPausedTasks.add(runnable);
                } else {
                    runnable.enqueue();
                    mRunningTasks.add(runnable);
                    mExecutorService.execute(runnable);
                }
            } else {
                return;
            }
            if (mRunningTasks.size() >= THREAD_POOL_SIZE) {
                return;
            }
        }
    }

    public void finishTask(DownloadRunnable task) {
        mRunningTasks.remove(task);
        dispatch();
        //是否所有下载任务都完成
        if (mPendingTasks.size() == 0 && mRunningTasks.size() == 0) {
            if (mDownloadClient != null) {
                mDownloadClient.downloadTaskComplete();
            }
        }
    }


    public boolean failTask(DownloadRunnable task) {
        synchronized (mRunningTasks) {
            if (mRunningTasks.contains(task)) {
                mRunningTasks.remove(task);
            }
            dispatch();
            //超出重试次数，则不再继续重试，确保不要过度消耗电量与流量
            if (mFailMap.containsKey(task.getTaskInfo().getUrl())) {
                int failedTimes = mFailMap.get(task.getTaskInfo().getUrl()) + 1;
                if (failedTimes > RETRY_TIMES) {
                    return false;
                } else {
                    mFailMap.put(task.getTaskInfo().getUrl(), failedTimes);
                    enqueue(task);

                }
            } else {
                mFailMap.put(task.getTaskInfo().getUrl(), 1);
                enqueue(task);
            }

            if (mPendingTasks.size() == 0 && mRunningTasks.size() == 0) {
                if (mDownloadClient != null) {
                    mDownloadClient.downloadTaskComplete();
                }
            }
            return true;
        }
    }

    /**
     * 取消所有等待的或者已经下载中的请求,并把所有下载中的请求放入等待队列
     */
    public void cancelAllRequest() {
        for (DownloadRunnable task : mPendingTasks) {
            task.cancel();
        }

        for (DownloadRunnable task : mRunningTasks) {
            task.cancel();
        }

        mPendingTasks.addAll(mRunningTasks);
        mRunningTasks.clear();
    }

    /**
     * 取消所有wifi only的request，将其放入等待队列
     */
    public void pauseWifiOnlyRequest() {

        Iterator<DownloadRunnable> iterator = mPendingTasks.iterator();
        while (iterator.hasNext()) {
            DownloadRunnable task = iterator.next();
            if (task != null && task.isWifiOnly()) {
                iterator.remove();
                task.cancel();
                if (!mPausedTasks.contains(task)) {
                    mPausedTasks.add(task);
                }
            }
        }

        synchronized (mRunningTasks) {
            if (mRunningTasks.size() > 0) {
                Iterator<DownloadRunnable> iterator1 = mRunningTasks.iterator();
                while (iterator1.hasNext()) {
                    DownloadRunnable task = iterator1.next();
                    if (task != null && task.isWifiOnly()) {
                        iterator1.remove();
                        task.cancel();
                        if (!mPausedTasks.contains(task)) {
                            mPausedTasks.add(task);
                        }
                    }
                }
            }
        }
    }

    /**
     * 取消所有wifi only的request，将其放入等待队列
     */
    public boolean pauseRequest(String id, String url) {

        if (TextUtils.isEmpty(id) || TextUtils.isEmpty(url)) {
            return false;
        }

        Iterator<DownloadRunnable> iterator = mPendingTasks.iterator();
        while (iterator.hasNext()) {
            DownloadRunnable task = iterator.next();
            if (task != null && task.getTaskInfo() != null &&
                    id.equals(task.getTaskInfo().getFileId()) &&
                    url.equals(task.getTaskInfo().getUrl())) {
                iterator.remove();
                task.cancel();
                if (!mPausedTasks.contains(task)) {
                    mPausedTasks.add(task);
                }
                return true;
            }
        }

        synchronized (mRunningTasks) {
            if (mRunningTasks.size() > 0) {
                Iterator<DownloadRunnable> iterator1 = mRunningTasks.iterator();
                while (iterator1.hasNext()) {
                    DownloadRunnable task = iterator1.next();
                    if (task != null && task.getTaskInfo() != null &&
                            id.equals(task.getTaskInfo().getFileId()) &&
                            url.equals(task.getTaskInfo().getUrl())) {
                        iterator1.remove();
                        task.cancel();
                        if (!mPausedTasks.contains(task)) {
                            mPausedTasks.add(task);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 重启所有暂停的任务
     */
    public void resumeWifiOnlyRequest() {
        if (mPausedTasks != null && mPausedTasks.size() > 0) {
            mPendingTasks.addAll(mPausedTasks);
            mPausedTasks.clear();
            dispatch();
        }
    }

    /**
     * 重启单个下载的任务
     */
    public boolean resumeRequest(String id,String url) {

        if(TextUtils.isEmpty(id) || TextUtils.isEmpty(url)){
            return false;
        }

        if (mPausedTasks != null && mPausedTasks.size() > 0) {

            Iterator<DownloadRunnable> iterator = mPausedTasks.iterator();
            while (iterator.hasNext()) {
                DownloadRunnable task = iterator.next();
                if (task != null && task.getTaskInfo() != null &&
                        id.equals(task.getTaskInfo().getFileId()) &&
                        url.equals(task.getTaskInfo().getUrl())) {
                    iterator.remove();
                    task.cancel();
                    if (!mPendingTasks.contains(task)) {
                        mPendingTasks.add(task);
                        dispatch();
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isWifiOn() {
        try {
            boolean isWifi = false;
            ConnectivityManager cm = (ConnectivityManager)
                    mContext.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo info = cm.getActiveNetworkInfo();
                if (info != null) {
                    isWifi = info.getType() == ConnectivityManager.TYPE_WIFI;
                    boolean isConnected = info.isConnected();
                    return isWifi & isConnected;
                }
            }
            return isWifi;

        } catch (Throwable t) {
            return false;
        }

    }

    private boolean isDuplicatedTask(DownloadRunnable runnable) {
        boolean isPaused = isInQueue(runnable, mPausedTasks);
        if (isPaused) {
            return true;
        }

        boolean isPending = isInQueue(runnable, mPendingTasks);
        if (isPending) {
            return true;
        }

        boolean isRunning = isInQueue(runnable, mRunningTasks);
        if (isRunning) {
            return true;
        }
        return false;
    }

    private boolean isInQueue(DownloadRunnable runnable,
                              PriorityBlockingQueue<DownloadRunnable> queue) {
        if (runnable != null && queue != null) {
            String fileId = "";
            if (runnable.getTaskInfo() != null) {
                fileId = runnable.getTaskInfo().getFileId();
            }
            if (!TextUtils.isEmpty(fileId)) {
                Iterator<DownloadRunnable> iterator = queue.iterator();
                while (iterator.hasNext()) {
                    DownloadRunnable task = iterator.next();
                    if (task != null && task.getTaskInfo() != null) {
                        String fileId2 = task.getTaskInfo().getFileId();
                        if (fileId2 != null) {
                            if (fileId.equals(fileId2)) {
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }
        return false;

    }

}
