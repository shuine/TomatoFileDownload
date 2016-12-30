package com.tomato.plugindownloader;

import android.text.TextUtils;

import com.tomato.utils.FileUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;

import static com.tomato.plugindownloader.FileUtils.DOWNLOAD_SUFFIX;

/**
 * @author yeshuxin on 16-11-29.
 */

public class DownloadCallable implements Callable<Boolean> {

    //不合法参数
    public static final int ERROR_INVALID_PARAMS = 0x001;
    //创建文件失败
    public static final int ERROR_CREATE_FILE = 0x002;
    //数据保存失败
    public static final int ERROR_STORAGE = 0x003;
    //网络异常
    public static final int ERROR_NETWORK = 0x004;


    private FileRequest mRequest;
    private DownloadListener mDownloadListener;

    public DownloadCallable(FileRequest request) {

        mRequest = request;
        if (request.mCallable != null) {
            mDownloadListener = request.mCallable;
        }
    }

    public void setOnDownloadListener(DownloadListener listener) {
        mDownloadListener = listener;
    }

    @Override
    public Boolean call() throws Exception {

        boolean result = false;
        if (mRequest == null || TextUtils.isEmpty(mRequest.url)) {
            downloadFail(ERROR_INVALID_PARAMS,"请求参数错误");
            return result;
        }

        String fileName = mRequest.fileName;
        if (TextUtils.isEmpty(fileName)) {
            fileName = FileUtils.getFileName(mRequest.url);
            mRequest.fileName = fileName;
        }

        if (TextUtils.isEmpty(fileName)) {
            downloadFail(ERROR_INVALID_PARAMS,"请求缺少文件名称");
            return result;
        }

        if(TextUtils.isEmpty(mRequest.path)){
            mRequest.path = FileUtils.getRootPath();
        }

        File tempFile = new File(mRequest.path , fileName + FileUtils.DOWNLOAD_SUFFIX);
        try {
            tempFile = FileUtil.createNewFile(tempFile);
        } catch (IOException e) {
            downloadFail(ERROR_CREATE_FILE, "无法创建文件");
        }
        // download
        long fileLength = tempFile.length();
        FileOutputStream outFile = null;
        HttpURLConnection httpURLConnection = null;
        BufferedInputStream inputStream = null;

        try {
            outFile = new FileOutputStream(tempFile, true);
            URL url = new URL(mRequest.url);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Range", "bytes=" + fileLength + "-");
            inputStream = new BufferedInputStream(
                    httpURLConnection.getInputStream());
            long fileTotalLength = httpURLConnection.getContentLength() + fileLength;

            byte[] buffer = new byte[1024];
            long curDownLength = fileLength;
            long phaseDownloadLength = 0;
            float phaseLength = fileTotalLength / 15;
            int length = 0;
            downloadStart();
            while ((length = inputStream.read(buffer, 0, 1024)) != -1) {
                outFile.write(buffer, 0, length);
                outFile.flush();
                //更新下载进度
                phaseDownloadLength += length;
                if (phaseDownloadLength >= phaseLength) {
                    curDownLength += phaseDownloadLength;
                    phaseDownloadLength = 0;
                    updateProgress(curDownLength, fileTotalLength);
                }
                //end of 更新下载进度

            }
            mDownloadListener.onUpdateProgress(mRequest, curDownLength, fileTotalLength);
            downloadFinish();
            if (length == -1) {
                result = true;
                downloadSuccess();
            } else {
                downloadFail(ERROR_NETWORK, "未完成下载");
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            downloadFail(ERROR_STORAGE, "无法存储文件数据");

        } catch (MalformedURLException e) {
            e.printStackTrace();
            tempFile.delete();
            downloadFail(ERROR_NETWORK, "网络异常");
        } catch (IOException e) {
            //取消下载时　触发该异常
            if(e instanceof InterruptedIOException){
                return result;
            }
            e.printStackTrace();
            downloadFail(ERROR_STORAGE, "文件读写异常");

        }  finally {
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
        return result;
    }

    private void downloadFail(int code, String msg) {

        if (mDownloadListener != null) {
            mDownloadListener.onFailDownload(mRequest, code, msg);
        }
    }

    private void downloadSuccess(){
        if(mDownloadListener!=null){
            mDownloadListener.onSuccessDownload(mRequest);
        }
    }

    private void downloadFinish() {

        if (mDownloadListener != null) {
            mDownloadListener.onFinishDownload(mRequest);
        }
    }

    private void downloadStart() {
        if (mDownloadListener != null) {
            mDownloadListener.onStartDownload(mRequest);
        }
    }

    private void updateProgress(long current, long total) {
        if (mDownloadListener != null) {
            mDownloadListener.onUpdateProgress(mRequest, current, total);
        }
    }
}
