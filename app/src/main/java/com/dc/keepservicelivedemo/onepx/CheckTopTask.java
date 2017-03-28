package com.dc.keepservicelivedemo.onepx;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.util.List;

/**
 * Created by dc on 2017/3/28.
 *
 * 检测 后台 进程  是否运行
 *
 *
 * 没有进行 启动
 *
 *
 */

public class CheckTopTask implements Runnable{

    private static final String TAG = "CheckTopTask";
    private Context context;


    public CheckTopTask(Context context) {
        this.context = context;
    }


    /**
     * 启动 1像素 activity
     * FLAG_ACTIVITY_SINGLE_TOP  栈顶不启动新的
     *
     * @param context
     */
    public static void startForeground(Context context) {
        try {
            Intent intent = new Intent(context, OnePXActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "e:" + e);
        }
    }


    @Override
    public void run() {


        boolean foreground = isForeground(context);
        Log.d(TAG, "foreground:" + foreground);
        //没有运行  重新 启动 activity
        if (!foreground) {
            startForeground(context);
        }

    }


    /**
     * 当前程序进程 是都在 运行
     * @param context
     * @return
     */
    private boolean isForeground(Context context) {
        try {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
            if (runningAppProcesses != null) {
                int myPid = android.os.Process.myPid();

                for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
                    if (runningAppProcessInfo.pid == myPid) {
                        return runningAppProcessInfo.importance <= ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "e:" + e);
        }
        return false;
    }







}
