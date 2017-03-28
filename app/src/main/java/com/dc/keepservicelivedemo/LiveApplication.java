package com.dc.keepservicelivedemo;

import android.app.Application;
import android.content.Context;

/**
 * Created by zcits on 2017/3/28.
 */

public class LiveApplication extends Application {

    private static Context appContext;

    public static Context getAppContext() {
        return appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
    }




}
