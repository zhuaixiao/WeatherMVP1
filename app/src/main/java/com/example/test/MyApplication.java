package com.example.test;

import android.app.Application;
import android.content.Context;

import com.tencent.bugly.Bugly;

import org.litepal.LitePal;

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
        Bugly.init(this, "427db4c23c", false);
        context = this;
    }

    public static Context getContext() {
        return context;
    }

}
