package com.tomato.downloader;

/**
 * @author yeshuxin on 16-10-26.
 */

public interface INetwork {
    NetworkResponse performFileDownload(FileDownloadRequest request, DownloadCallback listener);
}
