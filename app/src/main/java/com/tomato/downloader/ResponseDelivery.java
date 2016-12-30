package com.tomato.downloader;

/**
 * @author yeshuxin on 16-10-26.
 */

public interface ResponseDelivery {

    public void postResponse(FileDownloadRequest request, FileResponse response);
    public void postResponse(FileDownloadRequest request, FileResponse response, Runnable runnable);
    public void postError(FileDownloadRequest request, String error);
    public void postCallBack(FileDownloadRequest request,DownloadCallback callback);
}
