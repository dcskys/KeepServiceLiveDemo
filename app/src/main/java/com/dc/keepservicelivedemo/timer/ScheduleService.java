package com.dc.keepservicelivedemo.timer;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.dc.keepservicelivedemo.foreground.DaemonService;

/**
 *
 * 最低  api 为 21   android 5.0
 *
 * JobScheduler  使用  需要继承 JobService
 *
 * 个job service运行在你的主线程，这意味着你需要使用子线程，handler, 或者一个异步任务来运行耗时的操作以防止阻塞主线程
 *
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ScheduleService  extends JobService {

    private static final String TAG = "ScheduleService";

    /**
     * 当任务开始时会执行onStartJob(JobParameters params)方法，因为这是系统用来触发已经被执行的任务。
     * @param params 是false,系统假设返回时任务已经完毕。true,那么系统假定这个任务正要被执行
     * @return
     */
    @Override
    public boolean onStartJob(JobParameters params) {


        Log.d(TAG, "onStartJob(): params = [" + params + "]");
        Intent intent = new Intent(getApplicationContext(), DaemonService.class);
        startService(intent);
        jobFinished(params, false);

        return false;

    }

    /**
     *
     * 上面 返回 true 才会被调用
     * @param params
     * @return
     */
    @Override
    public boolean onStopJob(JobParameters params) {

        Log.d(TAG, "onStopJob(): params = [" + params + "]");
        return false;
    }


}
