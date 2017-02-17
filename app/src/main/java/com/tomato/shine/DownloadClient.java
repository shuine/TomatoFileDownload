package com.tomato.shine;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

/**
 * @author yeshuxin on 17-1-10.
 *         下载接口类,提供外部调用接口
 */

public class DownloadClient {

    private Context mContext;
    private FileRequestConfig mConfig;//用于设置网络下载的WiFi下载和优先级
    private String PATH = "";
    private String DEFAULT_PATH = Environment.getExternalStorageDirectory().getPath();

    public DownloadClient(Context context) {
        mContext = context;
        initDefaultConfig();
//        PATH = mContext.getApplicationContext().getFilesDir().getPath();
    }

    private void initDefaultConfig() {
        mConfig = new FileRequestConfig(true, FileRequestConfig.PRIORITY_NORMAL);
    }

    public void getFile(String url, DownloadCallback callback) {
        String path = DEFAULT_PATH;
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (!TextUtils.isEmpty(PATH)) {
            path = PATH;
        }
        this.getFile(url, path, callback);
    }

    public void getFile(String url, String path, DownloadCallback callback) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        DownloadTaskInfo info = new DownloadTaskInfo(url, path);
        this.getFile(info, callback);
    }

    public void getFile(final DownloadTaskInfo info, DownloadCallback callback) {
        this.getFile(info, mConfig, callback);
    }

    public void getFile(final DownloadTaskInfo info, final FileRequestConfig config, final DownloadCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Downloader.getInstance(mContext).startFileDownload(info, config, callback);
            }
        }).start();
    }

    public boolean pauseFileDownlaod(DownloadTaskInfo info) {
        if (info == null || TextUtils.isEmpty(info.getUrl()) && TextUtils.isEmpty(info.getFileId())) {
            return false;
        }
        return Downloader.getInstance(mContext).pauseDownload(info);
    }

    public boolean resumeFileDownload(DownloadTaskInfo info) {
        if (info == null || TextUtils.isEmpty(info.getUrl()) && TextUtils.isEmpty(info.getFileId())) {
            return false;
        }
        return Downloader.getInstance(mContext).resumeDownload(info);
    }
}
