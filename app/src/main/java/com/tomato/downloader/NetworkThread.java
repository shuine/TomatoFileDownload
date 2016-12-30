package com.tomato.downloader;

import android.os.Process;

import java.util.concurrent.BlockingQueue;

/**
 * @author yeshuxin on 16-10-25.
 */

public class NetworkThread extends Thread implements DownloadCallback{

    private BlockingQueue<FileDownloadRequest> mQueue;
    private INetwork mDownload;
    private ResponseDelivery mDelivery;
    private volatile boolean mQuit = false;

    public NetworkThread(BlockingQueue<FileDownloadRequest> request,INetwork download,
                         ResponseDelivery delivery){
        this.mQueue = request;
        this.mDownload = download;
        this.mDelivery = delivery;
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        while (true){
            FileDownloadRequest request;
            try {

                request = mQueue.take();
            }catch (InterruptedException e){
                if(mQuit){
                    return;
                }
                continue;
            }

            if (request.isCancel()){
                continue;
            }

            NetworkResponse networkResponse = mDownload.performFileDownload(request,this);
            mDelivery.postResponse(request,new FileResponse());
        }
    }

    public void quit(){
        mQuit = true;
        interrupt();
    }

    @Override
    public void onDownloadUpdate(FileInfo info, long currentLength, long totalLength) {

    }

    @Override
    public void onDownloadComplete(FileInfo info) {

    }

    @Override
    public void onDownloadFail(FileInfo info, int errorType) {

    }

    @Override
    public void onDownloadSuccess(FileInfo info) {

    }
}
