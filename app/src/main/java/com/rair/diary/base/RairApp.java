package com.rair.diary.base;

import android.app.Application;
import android.os.Environment;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

import com.rair.diary.config.Cockroach;
import com.rair.diary.constant.Constants;
import com.rair.diary.utils.SPUtils;
import com.rair.diary.utils.Utils;

import java.io.File;

import cn.bmob.v3.Bmob;

/**
 * Created by mzaiy on 2017/6/5.
 */

public class RairApp extends Application {

    private static RairApp rairApp;
    private SPUtils spUtils;

    @Override
    public void onCreate() {
        super.onCreate();
        rairApp = this;
        Utils.init(this);
        spUtils = new SPUtils(Constants.SP_NAME);
        Bmob.initialize(this, Constants.APP_ID);
//        Cockroach.install(exceptionHandler);
        configTheme();
    }

    public void configTheme() {
        boolean isNight = spUtils.getBoolean("isNight", false);
        if (isNight) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    Cockroach.ExceptionHandler exceptionHandler = new Cockroach.ExceptionHandler() {
        @Override
        public void handlerException(Thread thread, Throwable throwable) {
            Log.e("TAG", "handlerException: " + throwable);
        }
    };

    public static RairApp getRairApp() {
        return rairApp;
    }

    public SPUtils getSpUtils() {
        return spUtils;
    }

    public File getRairPath() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File storageDirectory = Environment.getExternalStorageDirectory();
            File rairFile = new File(storageDirectory, Constants.RAIR_PATH);
            if (!rairFile.exists()) {
                rairFile.mkdirs();
            }
            return rairFile;
        }
        return getCacheDir();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Cockroach.uninstall();
    }
}
