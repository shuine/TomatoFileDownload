package com.tomato.downloader;

/**
 * @author yeshuxin on 16-10-22.
 */

public interface DownloadStatusListener {
    void onDownloadSuccess(FileDownloadRequest task);
    void onDownloadFail(FileDownloadRequest task, int error, String msg);
    void onDownloadUpdate(FileDownloadRequest task, long length, long totalLength);
}
