package com.dc.keepservicelivedemo.onepx;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.dc.keepservicelivedemo.LiveApplication;
import com.dc.keepservicelivedemo.onepx.CheckTopTask;
import com.dc.keepservicelivedemo.onepx.OnePXActivity;

/**
 * 监听 锁屏  解锁的 广播
 *
 *
 * USER_PRESENT 可以静态注册，
 * 其余两个只能通过动态注册才能收到广播。索性把这三个广播都动态和静态注册一次
 *
 */
public class ScreenReceiver extends BroadcastReceiver {

    private static final String TAG = "ScreenReceiver";
    private Handler mHandler = new Handler(Looper.getMainLooper());

     //判断后台 是否运行的线程
    private CheckTopTask mCheckTopTask = new CheckTopTask(LiveApplication.getAppContext());

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "onReceive(): context = [" + context + "], intent = [" + intent + "]");
        String action = intent.getAction();

        // 这里可以启动一些服务


        try{
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                Log.i(TAG, "锁屏 开启一像素");
                CheckTopTask.startForeground(context); //启动 一像素 activity
                //为什么还要再三秒后还要执行一个任务？因为担心其他应用也采用同样的方案，把它的 Activity 盖在我们的上面。这个任务就是在三秒后检测当前 Activity 是否在前台
                // ，如果不在就再次启动，获得前台的焦点。腾讯就是这么搞的
                mHandler.postDelayed(mCheckTopTask, 3000);
            } else if (Intent.ACTION_USER_PRESENT.equals(action)||Intent.ACTION_SCREEN_ON.equals(action)) {
                Log.i(TAG, "开屏开锁   关闭 一像素");
                OnePXActivity onePxActivity = OnePXActivity.instance != null ? OnePXActivity.instance.get() : null;
                if (onePxActivity != null) {
                    onePxActivity.finishSelf();
                }
                mHandler.removeCallbacks(mCheckTopTask);
            }
        }catch (Exception e){
            Log.e(TAG, "e:" + e);
        }

    }
}
