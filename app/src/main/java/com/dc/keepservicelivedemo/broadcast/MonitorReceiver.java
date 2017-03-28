package com.dc.keepservicelivedemo.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dc.keepservicelivedemo.foreground.DaemonService;

/**
 *
 *是对于国内各种定制的 ROM 来说，此方案的效果并不好，
 * 因为安全管家会管理开机和后台自启，拦截发送给第三方应用的广播，所以该方案只能作为备选方案。

 */
public class MonitorReceiver extends BroadcastReceiver {

    private static final String TAG = "MonitorReceiver";


    @Override
    public void onReceive(Context context, Intent intent) {


        Log.d(TAG, "onReceive(): intent: " + intent.toUri(0));
        Intent target = new Intent(context, DaemonService.class);
        context.startService(target);


    }
}
