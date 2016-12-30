package com.tomato;

import android.app.Application;

import com.tomato.downloader.FileDownloadClient;

/**
 * @author yeshuxin on 16-10-31.
 */

public class TomatoApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        FileDownloadClient.initDownloadClient(this);
    }
}
