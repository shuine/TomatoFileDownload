package com.tomato.download;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yeshuxin on 16-10-26.
 */

public class FileDownloadQueue {

    private AtomicInteger mSequenceGenerator = new AtomicInteger();
    private final Set<FileDownloadRequest> mCurrentRequests = new HashSet<FileDownloadRequest>();
    private final PriorityBlockingQueue<FileDownloadRequest> mNetworkQueue =
            new PriorityBlockingQueue<FileDownloadRequest>();
    private static final int DEFAULT_NETWORK_THREAD_POOL_SIZE = 4;
    private final INetwork mNetwork;
    private final ResponseDelivery mDelivery;
    private NetworkThread[] mNetworkThread;
    private List<DownloadFinishListener> mFinishListener = new ArrayList<>();
    private boolean mIsStart = false;

    public static interface DownloadFinishListener{
        void onDownloadFinish(FileDownloadRequest request);
    }
    public FileDownloadQueue(INetwork network, ResponseDelivery delivery){
        this(network,delivery,DEFAULT_NETWORK_THREAD_POOL_SIZE);
    }

    public FileDownloadQueue(INetwork network, ResponseDelivery delivery, int threadSize){
        mNetwork = network;
        mDelivery = delivery;
        mNetworkThread = new NetworkThread[threadSize];
    }

    public void start(){
        stop();
        for (int i=0;i<mNetworkThread.length;i++){
            NetworkThread thread = new NetworkThread(mNetworkQueue,mNetwork,mDelivery);
            mNetworkThread[i] = thread;
            thread.start();
        }
        mIsStart = true;
    }

    public void stop(){

        for (int i = 0;i < mNetworkThread.length;i++){
            if(mNetworkThread[i] != null){
                mNetworkThread[i].quit();
            }
        }
        mIsStart = false;
    }

    public boolean isStart(){
        return mIsStart;
    }

    public int getSequenceNumber(){
        return mSequenceGenerator.decrementAndGet();
    }

    public FileDownloadRequest add(FileDownloadRequest request){
        request.setRequestQueue(this);
        synchronized (mCurrentRequests){
            mCurrentRequests.add(request);
        }

        request.setSequence(getSequenceNumber());
        mNetworkQueue.add(request);
        return request;
    }

    <T> void finish(FileDownloadRequest request){
        synchronized (mCurrentRequests){
            mCurrentRequests.remove(request);
        }

        synchronized (mFinishListener){
            for(DownloadFinishListener listener:mFinishListener){
                listener.onDownloadFinish(request);
            }
        }

    }
    public void addFinishListener(DownloadFinishListener listener){
        synchronized (mFinishListener){
            mFinishListener.add(listener);
        }
    }

    public void removeFinishListener(DownloadFinishListener listener){
        synchronized (mFinishListener){
            mFinishListener.remove(listener);
        }
    }
}
