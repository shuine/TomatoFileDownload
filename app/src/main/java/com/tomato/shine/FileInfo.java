package com.tomato.shine;

import android.text.TextUtils;

/**
 * @author yeshuxin on 17-1-9.
 */

public class FileInfo {

    private String url;
    private String filePath;
    private String md5;
    private String fileId;

    public FileInfo(String url, String filePath, String md5) {
        this(url, filePath, md5, "");
    }

    public FileInfo(String url, String filePath, String md5, String fileId) {
        this.url = url;
        this.filePath = filePath;
        this.fileId = fileId;
        this.md5 = md5;
    }

    public FileInfo(DownloadTaskInfo task){
        this(task.getUrl(), task.getFilePath(), task.getMd5(), task.getFileId());
    }

    public String getUrl() {
        if (TextUtils.isEmpty(url)) {
            url = "";
        }
        return url;
    }

    public String getFilePath() {
        if (TextUtils.isEmpty(filePath)) {
            filePath = "";
        }
        return filePath;
    }

    public String getFileId() {
        if (TextUtils.isEmpty(fileId)) {
            fileId = "";
        }
        return fileId;
    }

    public String getMd5() {
        if (TextUtils.isEmpty(md5)) {
            md5 = "";
        }
        return md5;
    }
}
