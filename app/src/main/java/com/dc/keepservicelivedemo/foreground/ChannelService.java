package com.dc.keepservicelivedemo.foreground;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * 虚假的service   用于 api>18 开启前台服务
 *
 *
 */
public class ChannelService extends Service {

    private static final String TAG = "ChannelService";

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

         //启动一个 相同id 的前台服务
        Log.d(TAG, "onStartCommand(): intent = [" + intent.toUri(0) + "], flags = [" + flags + "], startId = [" + startId + "]");
        startForeground(DaemonService.SERVICE_ID, new Notification());
        stopForeground(true);
        stopSelf();
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy()");
    }
}
