package com.tomato.plugindownloader;

import android.os.Handler;

/**
 * @author yeshuxin on 16-12-1.
 */

public class DeliveryListener {

    public static final int TYPE_START = 0;
    public static final int TYPE_PAUSE = 1;
    public static final int TYPE_FINISH = 2;
    public static final int TYPE_SUCCESS = 3;
    public static final int TYPE_RESUME = 4;
    public static final int TYPE_CANCEL = 5;
    public static final int TYPE_UPDATE = 6;
    public static final int TYPE_FAIL = 7;

    private Handler mHandler;

    public DeliveryListener(Handler handler) {
        mHandler = handler;
    }

    public void deliveryCallback(FileRequest request, int type) {
        if (request == null || request.mCallable == null || mHandler == null) {
            return;
        }
        ListenerRunnable runnable = new ListenerRunnable(request,type);
        mHandler.post(runnable);
    }

    public void deliveryUpdate(FileRequest request, long current, long total) {
        if (request == null || request.mCallable == null || mHandler == null) {
            return;
        }
        ListenerRunnable runnable = new ListenerRunnable(request,TYPE_UPDATE);
        runnable.setUpdate(current,total);
        mHandler.post(runnable);
    }

    public void deliveryFail(FileRequest request, int code, String msg) {
        if (request == null || request.mCallable == null || mHandler == null) {
            return;
        }
        ListenerRunnable runnable = new ListenerRunnable(request,TYPE_FAIL);
        runnable.setFailMsg(code,msg);
        mHandler.post(runnable);
    }

    private class ListenerRunnable implements Runnable {
        private FileRequest mListener;
        private int mType;
        private long mCurrent;
        private long mTotal;
        private int mCode;
        private String mMSG;

        public ListenerRunnable(FileRequest listener, int type) {
            mListener = listener;
            mType = type;
        }

        public void setUpdate(long current, long total) {
            mCurrent = current;
            mTotal = total;
        }

        public void setFailMsg(int code,String msg){
            mCode = code;
            mMSG = msg;
        }

        @Override
        public void run() {
            if (mListener == null) {
                return;
            }
            switch (mType) {
                case TYPE_START:
                    mListener.mCallable.onStartDownload(mListener);
                    break;
                case TYPE_PAUSE:
                    mListener.mCallable.onPauseDownload(mListener);
                    break;
                case TYPE_SUCCESS:
                    mListener.mCallable.onSuccessDownload(mListener);
                    break;
                case TYPE_FINISH:
                    mListener.mCallable.onFinishDownload(mListener);
                    break;
                case TYPE_RESUME:
                    break;
                case TYPE_FAIL:
                    mListener.mCallable.onFailDownload(mListener, mCode, mMSG);
                    break;
                case TYPE_UPDATE:
                    mListener.mCallable.onUpdateProgress(mListener, mCurrent, mTotal);
                    break;
            }
        }
    }
}
