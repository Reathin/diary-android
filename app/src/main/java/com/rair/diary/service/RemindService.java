package com.rair.diary.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.rair.diary.constant.Constants;

/**
 * Created by mzaiy on 2017/6/5.
 */

public class RemindService extends Service {

    private static RemindService remindService;

    @Override
    public void onCreate() {
        super.onCreate();
        remindService = this;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sendBroadcast(new Intent(Constants.SET_RECEIVER));
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(60000);
                        sendBroadcast(new Intent(Constants.REMIND_RECEIVER));
                    } catch (Exception e) {
                    }
                }
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }
}
