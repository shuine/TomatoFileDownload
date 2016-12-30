package com.tomato.plugindownloader;

import android.text.TextUtils;

import java.util.concurrent.Callable;

/**
 * @author yeshuxin on 16-11-29.
 */

public class FileRequest {

    public String url;
    public String path;
    public String fileName;

    public DownloadListener mCallable;

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof FileRequest)){
            return false;
        }
        if(!TextUtils.isEmpty(url)){
            if(!url.equals(((FileRequest) obj).url)){
                return false;
            }
        }
        if(!TextUtils.isEmpty(path)){
            if(!path.equals(((FileRequest) obj).path)){
                return false;
            }
        }
        if(!TextUtils.isEmpty(fileName)){
            if(!fileName.equals(((FileRequest) obj).fileName)){
                return false;
            }
        }
        return true;
    }
}
