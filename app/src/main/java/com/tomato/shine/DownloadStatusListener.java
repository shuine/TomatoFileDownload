package com.tomato.shine;

/**
 * @author yeshuxin on 17-1-9.
 */

public interface DownloadStatusListener {

    void onDownloadSuccess(DownloadRunnable task);

    void onDownloadFail(DownloadRunnable task, int error, String msg);

    void onDownloadUpdate(DownloadRunnable task, long length, long totalLength);

    void onDownloadPause(DownloadRunnable task);

    void onDownloadStart(DownloadRunnable task);
}
