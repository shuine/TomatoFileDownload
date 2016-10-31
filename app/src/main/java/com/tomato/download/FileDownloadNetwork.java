package com.tomato.download;

import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author yeshuxin on 16-10-26.
 */

public class FileDownloadNetwork implements INetwork {

    private FileDownloadRequest mRequest;
    private DownloadCallback mStatusListener;
    private String mTempFilePath;

    private FileInfo mFileInfo;

    @Override
    public NetworkResponse performFileDownload(FileDownloadRequest request,DownloadCallback listener) {

        mRequest = request;
        mStatusListener = listener;
        mFileInfo = this.mRequest.getFileInfo();
        if(mFileInfo == null){
            return null;
        }

        if(mFileInfo != null){
            mTempFilePath = mFileInfo.getFilePath() + mFileInfo.getFileName() + ".rar";
        }else {
            mTempFilePath = "";
        }

        if(TextUtils.isEmpty(mFileInfo.getUrl())){
            downloadFail(0,"File URl is null");
            return null;
        }
        if(TextUtils.isEmpty(mFileInfo.getFilePath())){
            // TODO: 16-10-22 set default path
        }

        File tempFile = new File(mTempFilePath);

        try {
            if(!tempFile.exists()){
                if(tempFile.getParentFile() == null){
                    downloadFail(0,"File path is error");
                    return null;
                }
                File parentFile = new File(tempFile.getParent());
                if(parentFile.exists()){
                    tempFile.createNewFile();
                }else {
                    boolean result = parentFile.mkdirs();
                    if(result){
                        tempFile.createNewFile();
                    }
                }
            }
        }catch (IOException err){
            downloadFail(0,"File create fail");
        }

        // download
        long fileLength = tempFile.length();
        FileOutputStream outFile = null;
        HttpURLConnection httpURLConnection = null;
        BufferedInputStream inputStream = null;

        try {
            outFile = new FileOutputStream(tempFile, true);
            URL url = new URL(mFileInfo.getUrl());
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Range", "bytes=" + fileLength + "-");
            inputStream = new BufferedInputStream(
                    httpURLConnection.getInputStream());
            long fileTotalLength = httpURLConnection.getContentLength() + fileLength;

            byte[] buffer = new byte[1024];
            long curDownLength = fileLength;
            long phaseDownloadLength = 0;
            float phaseLength = fileTotalLength/15;
            int length = 0;
            while ((length = inputStream.read(buffer, 0, 1024)) != -1) {
                outFile.write(buffer, 0, length);
                outFile.flush();
                //更新下载进度
                phaseDownloadLength +=length;
                if (phaseDownloadLength >= phaseLength) {
                    curDownLength += phaseDownloadLength;
                    phaseDownloadLength = 0;

                    if (fileTotalLength > 0 ) {
                        if (mStatusListener != null) {
                            mStatusListener.onDownloadUpdate(request.getFileInfo(), curDownLength, fileTotalLength);
                        }
                    }
                }
                //end of 更新下载进度
            }

            if (length == -1) {
                downloadFinish();
            } else {
                downloadFail(0, "fail network error");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            downloadFail(0, e.getMessage());

        } catch (MalformedURLException e) {
            e.printStackTrace();
            tempFile.delete();
            downloadFail(0, e.getMessage());


        } catch (IOException e) {
            e.printStackTrace();
            downloadFail(0, e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            downloadFail(0, e.getMessage());

        }finally {
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
        return null;
    }

    public FileInfo getFileInfo(){
        return this.mFileInfo;
    }

    //// TODO: 16-10-26
    private void downloadSuccess(){
        if (mFileInfo != null && !TextUtils.isEmpty(mFileInfo.getFilePath())) {
            File desFile = new File(mFileInfo.getFilePath());
            File tempFile = new File(mTempFilePath);
           /* if (desFile.exists()) {
                desFile.delete();
            }*/
            boolean result = true;//tempFile.renameTo(desFile);

            //最终文件成功生成
            if (result) {
                if (mStatusListener != null) {
                    mStatusListener.onDownloadUpdate(mRequest.getFileInfo(), desFile.length(), desFile.length());
                    mStatusListener.onDownloadSuccess(mRequest.getFileInfo());
                }
            } else {
                //最终文件生成失败, 删除下载的文件
                tempFile.delete();
                downloadFail(0, "file rename fail");
            }
        }
    }

    private void downloadFail(int error,String msg){
        if (this.mStatusListener != null) {
            this.mStatusListener.onDownloadFail(mRequest.getFileInfo(), error);
        }
    }

    private void downloadFinish(){
        if (!TextUtils.isEmpty(mTempFilePath)) {
            File tempFile = new File(mTempFilePath);
            if (tempFile.exists()) {
                downloadSuccess();
            } else {
                tempFile.delete();
                downloadFail(0, "");
            }

            mStatusListener.onDownloadComplete(mRequest.getFileInfo());
        }
    }

}
