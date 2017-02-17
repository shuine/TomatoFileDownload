package com.tomato.shine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import static android.R.attr.priority;

/**
 * @author yeshuxin on 17-1-10.
 *         下载入口类,网络监听和下载控制
 */

public class Downloader {

    private static Downloader mInstance;
    private DownloadExecutor mExecutor;
    private Context mContext;
    private NetReceiver mReceiver;
    private boolean mIsFirstReceive = true;

    public static Downloader getInstance(Context context) {
        if (mInstance == null) {
            synchronized (Downloader.class) {
                if (mInstance == null) {
                    mInstance = new Downloader(context);
                }
            }
        }
        return mInstance;
    }

    private Downloader(Context context) {
        mContext = context;
        mExecutor = new DownloadExecutor(context);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mReceiver = new NetReceiver();
        mContext.getApplicationContext().registerReceiver(mReceiver, intentFilter);
    }

    public void desotry() {
        mContext.getApplicationContext().unregisterReceiver(mReceiver);
    }

    public void startFileDownload(DownloadTaskInfo task, FileRequestConfig config, DownloadCallback callback) {

        if (task == null) {
            return;
        }
        FileDownloadRequest request = new FileDownloadRequest(task, config, callback);
        mExecutor.addRequest(request);

    }

    private void resumeDownload() {
        mExecutor.resumeWifiOnlyAllRequest();
    }

    public boolean resumeDownload(DownloadTaskInfo info) {
        if(info == null || TextUtils.isEmpty(info.getUrl()) && TextUtils.isEmpty(info.getFileId())){
            return false;
        }
        return mExecutor.resumeRequest(info);
    }

    private void pauseDownload() {
        mExecutor.pauseWifiOnlyRequest();
    }

    public boolean pauseDownload(DownloadTaskInfo info) {
        if(info == null || TextUtils.isEmpty(info.getUrl()) && TextUtils.isEmpty(info.getFileId())){
            return false;
        }
        return mExecutor.pauseRequest(info);
    }

    private void networkStateChanged(boolean isWifiOn) {
        //处于Wifi连接状态
        if (isWifiOn) {
            resumeDownload();
        } else {
            pauseDownload();
        }
    }

    /**
     * 监控网络状态，确保只有在wifi条件下才进行下载
     */
    private class NetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                if (!TextUtils.isEmpty(action)) {
                    if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                        NetworkInfo networkInfo =
                                intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                        if (networkInfo != null) {
                            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI &&
                                    !networkInfo.isConnected()) {
                                networkStateChanged(false);
                            } else if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI &&
                                    networkInfo.isConnected()) {
                                if (mIsFirstReceive) {
                                    mIsFirstReceive = false;
                                    return;
                                }
                                networkStateChanged(true);
                            }
                        }

                    }
                }
            }
        }
    }
}
