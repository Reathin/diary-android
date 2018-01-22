package com.rair.diary.ui.setting.remind;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.rair.diary.R;
import com.rair.diary.utils.RairUtils;
import com.rair.diary.utils.SPUtils;
import com.rair.diary.base.RairApp;
import com.rair.diary.constant.Constants;
import com.rair.diary.service.RemindService;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class NotifyActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.notify_iv_back)
    ImageView notifyIvBack;
    @BindView(R.id.notify_switch_open)
    Switch notifySwitchOpen;
    @BindView(R.id.notify_tv_set)
    TextView notifyTvSet;
    @BindView(R.id.notify_tv_current_time)
    TextView notifyTvCurrentTime;
    @BindView(R.id.notify_tv_remind_time)
    TextView notifyTvRemindTime;
    @BindView(R.id.notify_tv_tips)
    TextView notifyTvTips;
    private Unbinder unbinder;
    private Calendar calendar;
    private AlarmManager manager;
    private SPUtils spUtils;
    private Intent mIntent;
    private TimeChangeReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);
        unbinder = ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        // 获取闹钟管理的实例
        manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        spUtils = RairApp.getRairApp().getSpUtils();
        boolean isSet = spUtils.getBoolean("isSet", false);
        if (isSet) {
            notifySwitchOpen.setChecked(true);
            notifyTvSet.setEnabled(true);
            notifyTvTips.setEnabled(true);
        }
        notifySwitchOpen.setOnCheckedChangeListener(this);

        receiver = new TimeChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.SET_RECEIVER);
        intentFilter.setPriority(Integer.MAX_VALUE);
        // 注册广播接收器
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int hour = spUtils.getInt("hour", 0);
        int minute = spUtils.getInt("minute", 0);
        String remindTime = "提醒时间：" + RairUtils.format(hour) + ":" + RairUtils.format(minute);
        notifyTvRemindTime.setText(remindTime);
        mIntent = new Intent(NotifyActivity.this, RemindService.class);

        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        String currentTime = "当前时间：" + RairUtils.format(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + RairUtils.format(calendar.get(Calendar.MINUTE));
        notifyTvCurrentTime.setText(currentTime);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            notifyTvSet.setEnabled(true);
            notifyTvTips.setEnabled(true);
            spUtils.put("isSet", true);
            int hour = spUtils.getInt("hour");
            int minute = spUtils.getInt("minute");
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            setRemind();
        } else {
            notifyTvSet.setEnabled(false);
            notifyTvTips.setEnabled(false);
            spUtils.put("isSet", false);
            if (mIntent != null) {
                stopService(mIntent);
            }
        }
    }

    private void setRemind() {
        mIntent = new Intent(NotifyActivity.this, RemindService.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(NotifyActivity.this, 0, mIntent, 0);
        // 设置闹钟
        manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        // 设置周期闹钟
        int intervalMillis = 24 * 60 * 60 * 1000;
        long triggerAtMillis = System.currentTimeMillis() + (10 * 1000);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, intervalMillis, pendingIntent);
        startService(mIntent);
    }

    @OnClick({R.id.notify_iv_back, R.id.notify_tv_set})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.notify_iv_back:
                this.finish();
                break;
            case R.id.notify_tv_set:
                setRemindTime();
                break;
        }
    }

    /**
     * 设置提醒时间
     */
    private void setRemindTime() {
        calendar.setTimeInMillis(System.currentTimeMillis());
        int mHour = calendar.get(Calendar.HOUR_OF_DAY);
        int mMinute = calendar.get(Calendar.MINUTE);
        new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                spUtils.put("hour", hourOfDay);
                spUtils.put("minute", minute);
                spUtils.put("setTime", RairUtils.format(hourOfDay) + RairUtils.format(minute));
                setRemind();
            }
        }, mHour, mMinute, true).show();
    }

    class TimeChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.SET_RECEIVER)) {
                handler.sendEmptyMessage(0x001);
            }
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0x001) {
                int hour = spUtils.getInt("hour", 0);
                int minute = spUtils.getInt("minute", 0);
                String remindTime = "提醒时间：" + RairUtils.format(hour) + ":" + RairUtils.format(minute);
                notifyTvRemindTime.setText(remindTime);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        if (receiver != null) unregisterReceiver(receiver);
    }
}
