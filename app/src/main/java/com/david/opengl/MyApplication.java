package com.david.opengl;

import android.app.Application;

/**
 * @Author: liuwei
 * @Create: 2019/10/11 11:11
 * @Description:
 */
public class MyApplication extends Application {
    private static MyApplication application;


    public static MyApplication getApplication() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }
}
