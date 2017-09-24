package com.rair.diary.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.rair.diary.R;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.splash_tv_tip)
    TextView splashTvTip;
    private Unbinder bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        bind = ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        String[] tips = {
                "日记，不光是庄稼，是粮食，也是一剂疗养生命疼痛的良药。",
                "坚持写日记可以培养一个人的真诚感。",
                "写日记，要有恒，几十年如一日。记下个人的成绩，也记下个人的得失。",
                "最自然、最活泼、最不摆架子而又最精练的文学作品，莫过于日记。",
                "日记的价值不仅表现在写的当时，更表现在若干时间之后。",
                "为了不忘昨天，为了憧憬明天，应该写日记。",
                "每天写日记之前，都应该静下心来回忆一下全天的生活。",
                "我敞开心扉向它（日记）倾诉，请它为我挽住时间的足迹。",
                "日记是向自己讲述故事的忠实伙伴；日记，是催人反省的香醇美酒。",
                "我觉得坚持写日记更主要的好处是帮助自己，养成勤于动脑的习惯。"
        };
        int random = new Random().nextInt(tips.length);
        splashTvTip.setText(tips[random]);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                SplashActivity.this.finish();
            }
        }, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }
}
