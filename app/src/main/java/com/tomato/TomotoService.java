package com.tomato;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * @author yeshuxin on 17-2-17.
 */

public class TomotoService extends Service {

    private int mCount;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mCount = 10;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.e("shine","count:"+mCount);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("shine","count:"+mCount++);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
