package com.dc.keepservicelivedemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.dc.keepservicelivedemo.foreground.DaemonService;

/**
 * 本 Demo 主要用来练习   保证Service 存活
 *
 *DaemonService  确保其存存活
 *
 * 1.使用前台 activity 提高权限
 *
 *2.前台服务
 * 3.定时和系统广播
 *
 *一般使用 定时闹钟 可以解决 一些定时问题
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_main);


        startService(new Intent(getApplicationContext(), DaemonService.class));

        Log.d("MainActivity", "onCreate(): savedInstanceState = [" + savedInstanceState + "]");

        finish();

    }
}
