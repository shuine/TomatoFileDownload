package com.tomato.shine;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.tomato.shine.ErrorType.ERROR_CHK_MD5;
import static com.tomato.shine.ErrorType.ERROR_CREATE_FILE;
import static com.tomato.shine.ErrorType.ERROR_INVALID_PARAM;
import static com.tomato.shine.ErrorType.ERROR_NETWORK;

/**
 * @author yeshuxin on 17-1-9.
 */

public class DownloadRunnable implements Runnable,Comparable<DownloadRunnable> {


    private FileDownloadRequest mRequest;

    private DownloadTaskInfo mTaskInfo;

    //下载状态监听,主要是负责监听进度更新数据库内容
    private DownloadStatusListener mStatusListener;

    //是否需要停止下载
    private boolean mIsCancel = false;

    private String mTempFilePath;

    //TODO  定义优先级
    private int mPriority = 0;

    //下载完成的回调给业务方
    private Handler mHandler;

    public DownloadRunnable(FileDownloadRequest request, DownloadStatusListener listener) {
        if (request != null) {
            this.mRequest = request;
            this.mTaskInfo = request.getTaskInfo();
            this.mPriority = request.getPriority();
        }

        this.mStatusListener = listener;
        if (mTaskInfo != null) {
            mTempFilePath = mTaskInfo.getFilePath() + ".temp";
        } else {
            mTempFilePath = "";
        }

        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void run() {
        if (mTaskInfo == null) {
            return;
        }
        if (TextUtils.isEmpty(mTaskInfo.getUrl())) {
            downloadFail(ERROR_INVALID_PARAM, "");
            return;
        }
        if (TextUtils.isEmpty(mTaskInfo.getFilePath())) {
            //TODO  set default file path
        }
        File savedFile = new File(mTempFilePath);
        try {
            if (!savedFile.exists()) {
                if (savedFile.getParent() == null) {
                    downloadFail(ERROR_CREATE_FILE, "文件路径错误");
                    return;
                }
                File parentFile = new File(savedFile.getParent());
                if (parentFile.exists()) {
                    try {
                        savedFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                        downloadFail(ERROR_CREATE_FILE, e.getMessage());
                        return;
                    }

                } else {
                    boolean result = new File(savedFile.getParent()).mkdirs();
                    if (result) {
                        try {
                            savedFile.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                            downloadFail(ERROR_CREATE_FILE, e.getMessage());
                            return;
                        }
                    } else{
                        downloadFail(ERROR_CREATE_FILE, "create directory failed");
                        return;
                    }
                }
            }
        } catch (Exception e) {
            downloadFail(ERROR_CREATE_FILE, "文件路径错误");
            return;
        }

        downloadStart();
        long fileLength = savedFile.length();
        FileOutputStream outFile = null;
        HttpURLConnection httpURLConnection = null;
        BufferedInputStream inputStream = null;

        try {
            outFile = new FileOutputStream(savedFile, true);
            URL url = new URL(mTaskInfo.getUrl());
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Range", "bytes=" + fileLength + "-");
            inputStream = new BufferedInputStream(
                    httpURLConnection.getInputStream());
            long fileTotalLength = httpURLConnection.getContentLength() + fileLength;

            byte[] buffer = new byte[1024];
            long curDnldLength = fileLength;
            long phaseDownloadLength = 0;
            float phaseLength = fileTotalLength * 0.015f;
            int length = 0;
            while ((length = inputStream.read(buffer, 0, 1024)) != -1 && !mIsCancel) {
                outFile.write(buffer, 0, length);
                outFile.flush();
                //更新下载进度
                phaseDownloadLength +=length;
                if (phaseDownloadLength >= phaseLength) {
                    curDnldLength += phaseDownloadLength;
                    phaseDownloadLength = 0;

                    if (fileTotalLength > 0 ) {
                        if (mStatusListener != null) {
                            mStatusListener.onDownloadUpdate(this, curDnldLength, fileTotalLength);
                        }
                    }
                }
                //end of 更新下载进度
            }
            if (!mIsCancel) {
                if (length == -1) {
                    downloadFinish();
                } else {
                    downloadFail(ERROR_NETWORK, "");
                }
            }else {
               downloadPause();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            downloadFail(ERROR_CREATE_FILE, e.getMessage());

        } catch (MalformedURLException e) {
            e.printStackTrace();
            savedFile.delete();
            downloadFail(ERROR_INVALID_PARAM, e.getMessage());


        } catch (IOException e) {
            e.printStackTrace();
            downloadFail(ERROR_CREATE_FILE, e.getMessage());

        } finally {
            if (outFile != null) {
                try {
                    outFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
    }

    private void downloadFinish() {

        if (!TextUtils.isEmpty(mTempFilePath)) {
            File tempFile = new File(mTempFilePath);
            if (confirmMD5(tempFile)) {
                downloadSuccess();
            } else {
                //md5 校验错误，删除临时文件
                tempFile.delete();
                downloadFail(ERROR_CHK_MD5, "");

            }
        }
    }

    /**
     * 下载失败
     * @param error
     * @param msg
     */
    private void downloadFail(int error, String msg) {
        if (this.mStatusListener != null) {
            this.mStatusListener.onDownloadFail(this, error, msg);
        }

    }

    /**
     * 下载开始
     */
    private void downloadStart(){
        if (this.mStatusListener != null) {
            this.mStatusListener.onDownloadStart(this);
        }
    }

    private void downloadPause(){
        if (this.mStatusListener != null) {
            this.mStatusListener.onDownloadPause(this);
        }
    }

    /**
     * 下载完毕并且md5校验成功
     */
    private void downloadSuccess() {
        if (mTaskInfo != null && !TextUtils.isEmpty(mTaskInfo.getFilePath())) {
            File desFile = new File(mTaskInfo.getFilePath());
            File tempFile = new File(mTempFilePath);
            if (desFile.exists()) {
                desFile.delete();
            }
            boolean result = tempFile.renameTo(desFile);

            //最终文件成功生成
            if (result) {
                if (mStatusListener != null) {
                    mStatusListener.onDownloadUpdate(this, desFile.length(), desFile.length());
                    mStatusListener.onDownloadSuccess(this);
                }
            } else {
                //最终文件生成失败, 删除下载的文件
                tempFile.delete();
                downloadFail(ERROR_CREATE_FILE, "文件重命名失败");
            }
        }
    }
    /**
     * 验证文件md5值是否正确
     * @param file
     * @return
     */
    private boolean confirmMD5(File file) {
        if (file != null) {
            if (mTaskInfo != null) {
                if (TextUtils.isEmpty(mTaskInfo.getMd5())) {
                    return true;
                } else {
                    String md5 = mTaskInfo.getMd5();
                    return md5.equals(mTaskInfo.getMd5());
                }
            }
        }
        return false;
    }

    public DownloadTaskInfo getTaskInfo() {
        return mTaskInfo;
    }

    public void cancel() {
        mIsCancel = true;
    }

    public void enqueue() {
        mIsCancel = false;
    }

    @Override
    public int compareTo(DownloadRunnable another) {
        if (another != null) {
            return this.mPriority - another.mPriority;
        }
        return 0;
    }

    public boolean isWifiOnly() {
        if (mRequest != null) {
            return mRequest.isWifiOnly();
        }
        return false;
    }

    public FileDownloadRequest getRequest() {
        return mRequest;
    }
}
