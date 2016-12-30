package com.tomato.downloader;

import android.os.Handler;

import java.util.concurrent.Executor;

/**
 * @author yeshuxin on 16-10-21.
 */

public class FileResponseDelivery implements ResponseDelivery{

    private Executor mExcutor;

    public FileResponseDelivery(final Handler handler){
        mExcutor = new Executor() {
            @Override
            public void execute(Runnable command) {
                handler.post(command);
            }
        };
    }
    public FileResponseDelivery(Executor executor){
        mExcutor = executor;
    }

    @Override
    public void postResponse(FileDownloadRequest request, FileResponse response) {
        postResponse(request,response,null);

    }

    @Override
    public void postResponse(FileDownloadRequest request, FileResponse response, Runnable runnable) {

        mExcutor.execute(new ResponseRunnable(request,response,runnable));
    }

    @Override
    public void postError(FileDownloadRequest request, String error) {

    }

    @Override
    public void postCallBack(FileDownloadRequest request, DownloadCallback callback) {

    }

    class ResponseRunnable implements Runnable{

        private FileDownloadRequest mRequest;
        private FileResponse mResponse;
        private Runnable mRunnable;

        public ResponseRunnable(FileDownloadRequest request,FileResponse response,Runnable runnable){
            mRequest = request;
            mResponse = response;
            mRunnable = runnable;
        }

        @Override
        public void run() {

            if(mRequest.isCancel()){
                mRequest.finish();
            }
            DownloadCallback callback = mRequest.getDownloadCallback();
            if(mResponse.isSuccess() && callback !=null){
                callback.onDownloadComplete(mRequest.getFileInfo());
            }else {
                if(callback != null) {
                    callback.onDownloadFail(mRequest.getFileInfo(),
                            0);
                }
            }

            if(mRunnable != null){
                mRunnable.run();
            }
        }
    }
}
