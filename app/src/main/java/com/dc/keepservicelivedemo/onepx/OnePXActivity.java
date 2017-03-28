package com.dc.keepservicelivedemo.onepx;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.PowerManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import java.lang.ref.WeakReference;

/**
 *
 *
 * 2017.03.28
 *  最后实现的功能是 Activity 为我们占据前台
 * ，保证进程不被杀死，后台的 Service 在辛勤工作，目的达到了
 *
 *
 *
 * 1个像素  透明的activity   在锁屏时  进行  启动 ，解锁取消   提高service 优先级
 *
 * Manifest 中设置一些属性，包括排除在最近任务列表外、透明主题、启动模式等
 *
 * 在Android中每次屏幕的切换动会重启Activity，所以应该在Activity销毁前保存当前活动的状态，在Activity再次Create的时候载入配置。在activity加上android:configChanges="keyboardHidden|orientation"属性,就不会重启activity.而只是调用onConfigurationChanged(Configuration newConfig).这样就可以在这个方法里调整显示方式.

 */
public class OnePXActivity extends Activity {

    private static final String TAG = "OnePxActivity";


    //弱引用 帮助你合理的释放对象，造成不必要的内存泄漏，能及时收回 Activity
    public static WeakReference<OnePXActivity> instance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e(TAG, "onCreate(): savedInstanceState = [" + savedInstanceState + "]");


        instance = new WeakReference<>(this); //弱引用

        //设置activity  的大小为1个像素
        Window window = getWindow();
        window.setGravity(Gravity.TOP | Gravity.LEFT);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.x = 0;
        attributes.y = 0;
        attributes.height = 1;
        attributes.width = 1;
        window.setAttributes(attributes);

    }


    /**
     * 亮屏时，结束
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        if (isScreenOn()) { //
            finishSelf();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");

        //避免内存泄漏
        if (instance != null && instance.get() == this) {
            instance = null;
        }

    }





    /**
     * 因为 Activity 是在锁屏的时候启动的，所以在用户点亮屏幕后，它是绝对不能存在的。我们要在 Activity 的生命周期里做些处理。
     * 为了稳妥起见，对 Activity 的触摸事件我们也要处理，直接销毁 Activity 就可以了。
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        finishSelf();
        return super.dispatchTouchEvent(motionEvent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        finishSelf();
        return super.onTouchEvent(motionEvent);
    }


    /**

     *代码 判断屏幕 是否亮屏。锁屏
     * @return
     */
    private boolean isScreenOn() {
        try {
            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                return powerManager.isInteractive();
            } else {
                return powerManager.isScreenOn();
            }
        } catch (Exception e) {
            Log.e(TAG, "e:" + e);
        }
        return false;
    }


    public void finishSelf() {
        if (!isFinishing()) {
            finish();
        }
    }
}
