package com.tomato.download;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

/**
 * @author yeshuxin on 16-10-26.
 */

public class FileDownloadClient{

    private static FileDownloadClient mClient;
    private Context mContext;
    private FileDownloadQueue mQueue;

    private FileDownloadClient(Context context){
        mContext = context;
        initFileDownload();
    }
    public static FileDownloadClient getInstance(){

        if(mClient != null){
            return mClient;
        }
        return null;
    }

    public static void initDownloadClient(Context context){
        if(mClient == null){
            mClient = new FileDownloadClient(context);
        }
    }

    private void initFileDownload(){

        INetwork network = new FileDownloadNetwork();
        ResponseDelivery delivery = new FileResponseDelivery(new Handler(Looper.getMainLooper()));
        mQueue = new FileDownloadQueue(network,delivery);
    }

    public void getDownloadFile(FileDownloadRequest request){

        if(mQueue != null){
            if(!mQueue.isStart()){
                mQueue.start();
            }
            mQueue.add(request);
        }
    }
}
