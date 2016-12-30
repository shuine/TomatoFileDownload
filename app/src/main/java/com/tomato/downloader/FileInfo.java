package com.tomato.downloader;

/**
 * @author yeshuxin on 16-10-22.
 */

public class FileInfo {

    String mFileTag;
    String mUrl;
    String mFileName;
    String mPath;
    String mMD5;

    public String getTag(){
        return mFileTag;
    }
    public String getUrl(){
        return mUrl;
    }
    public String getFileName(){
        return mFileName;
    }
    public String getFilePath(){
        return mPath;
    }
    public String getMD5(){
        return mMD5;
    }
    public void setFileName(String name){
        mFileName = name;
    }
    public void setUrl(String url){
        mUrl = url;
    }
    public void setTag(String tag){
        mFileTag = tag;
    }
    public void setPat(String path){
        mPath = path;
    }
}
