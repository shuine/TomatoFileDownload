package com.tomato.plugindownloader;

/**
 * @author yeshuxin on 16-11-29.
 */

public interface DownloadListener {

    abstract void onStartDownload(FileRequest request);

    abstract void onPauseDownload(FileRequest request);

    abstract void onSuccessDownload(FileRequest request);

    abstract void onFinishDownload(FileRequest request);

    abstract void onCancelDownload(FileRequest request);

    abstract void onFailDownload(FileRequest request, int code, String msg);

    abstract void onUpdateProgress(FileRequest request, long current, long total);

}
