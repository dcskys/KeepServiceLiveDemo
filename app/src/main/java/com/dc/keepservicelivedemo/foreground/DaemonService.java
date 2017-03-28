package com.dc.keepservicelivedemo.foreground;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.dc.keepservicelivedemo.onepx.ScreenReceiver;
import com.dc.keepservicelivedemo.timer.ScheduleService;

/**
 * 2.  使用前台 服务的方式 来实现
 * 优先级为2时，一般不容易被杀死
 *
 * 用户看不见 的前台服务
 * 利用系统的漏洞开启前台服务，提升进程的优先级。这里有两个思路：
 *
 * 思路一：API < 18，启动前台 Service 时直接传入new Notification()；
 思路二：API >= 18，同时启动两个id相同的前台 Service，然后再将后启动的 Service 做 stop 处理；
 *
 *
 */
public class DaemonService extends Service {

    public static final int SERVICE_ID = 9510;
    private static final String TAG = "DaemonService";

    /**
     * 定时唤醒的时间间隔，15 分钟
     */
    private final static long WAKE_INTERVAL = 15 * 60 * 1000;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");

        // 动态注册开关屏广播
        ScreenReceiver receiver = new ScreenReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.USER_PRESENT");
        registerReceiver(receiver, intentFilter);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand(): intent = [" + intent.toUri(0) + "], flags = [" + flags + "], startId = [" + startId + "]");

        // 开启前台服务
        startForeground(SERVICE_ID, new Notification());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) { //  api>18  启动一个傀儡 用户不可预见的前台
            Intent sendIntend = new Intent(getApplicationContext(), ChannelService.class);
            startService(sendIntend);
        }



        try {
            //todo  定时检查 WorkService 是否在运行，如果不在运行就把它拉起来
            // Android 5.0+ 使用 JobScheduler，效果比 AlarmManager 好, 注意android 6.o 的Doze模式
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                Log.i(TAG, "开启 JobService 定时");

                JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
                jobScheduler.cancelAll(); //取消所有任务
                //第一个参数是你要运行的任务的标识符，第二个是这个Service组件的类名。
                JobInfo.Builder builder = new JobInfo.Builder(1024, new ComponentName(getPackageName(), ScheduleService.class.getName()));
                builder.setPeriodic(WAKE_INTERVAL); //设置定时时间
                builder.setPersisted(true);//设备重启后也继续
                builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);//在满足指定的网络条件时才会被执行(这里是任意一种网络都行)
                int schedule = jobScheduler.schedule(builder.build());

                if (schedule <= 0) {
                    Log.w(TAG, "schedule error！");
                }
            } else {
                // Android 4.4- 使用 AlarmManager
                Log.i(TAG, "开启 AlarmManager 定时");
                AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent alarmIntent = new Intent(getApplication(), DaemonService.class);
                PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1024, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                am.cancel(pendingIntent);
                //不定期的节能 activity   相对时间唤醒cpu
                am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + WAKE_INTERVAL, WAKE_INTERVAL, pendingIntent);
            }
        } catch (Exception e) {
            Log.e(TAG, "e:" + e);

        }



        // 简单守护开机广播  (隐藏 程序图标)
        getPackageManager().setComponentEnabledSetting(
                new ComponentName(getPackageName(), DaemonService.class.getName()),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        return super.onStartCommand(intent, flags, startId);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
        Intent intent = new Intent(getApplicationContext(), DaemonService.class);
        startService(intent);
    }


}
