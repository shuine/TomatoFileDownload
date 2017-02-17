package com.tomato.shine;

/**
 * @author yeshuxin on 17-1-9.
 */

public interface DownloadCallback {

    //更新下载进度
    void onDownloadUpdate(String fileId,float percent,long current,long total);

    //下载完成
    void onDownloadComplete(String fileId, String filePath);

    //下载失败
    void onDownloadFail(String fileId,ErrorType error);

    void onDownloadStart(String fileId,String filePath);

    void onDownloadPause(String fileId,String filePaht);
}
