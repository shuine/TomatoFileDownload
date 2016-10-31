package com.tomato.download;

/**
 * @author yeshuxin on 16-10-22.
 */

public interface DownloadCallback {
    void onDownloadUpdate(FileInfo info, long currentLength, long totalLength);
    void onDownloadComplete(FileInfo info);
    void onDownloadFail(FileInfo info, int errorType);
    void onDownloadSuccess(FileInfo info);
}
