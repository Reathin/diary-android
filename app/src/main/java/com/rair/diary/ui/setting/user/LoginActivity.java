package com.rair.diary.ui.setting.user;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.rair.diary.R;
import com.rair.diary.adapter.LoginPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.login_iv_back)
    ImageView loginIvBack;
    @BindView(R.id.login_tabs)
    TabLayout loginTabs;
    @BindView(R.id.login_pagers)
    ViewPager loginPagers;
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        unbinder = ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        String[] titles = {getString(R.string.login), getString(R.string.register)};
        Fragment[] fragments = {LoginFragment.newInstance(), RegisterFragment.newInstance()};
        LoginPagerAdapter pagerAdapter = new LoginPagerAdapter(fragmentManager, titles, fragments);
        loginPagers.setAdapter(pagerAdapter);
        loginTabs.setupWithViewPager(loginPagers);
    }

    @OnClick(R.id.login_iv_back)
    public void onViewClicked() {
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
