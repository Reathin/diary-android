package com.rair.diary.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jpeng.jptabbar.JPTabBar;
import com.jpeng.jptabbar.OnTabSelectListener;
import com.jpeng.jptabbar.anno.NorIcons;
import com.jpeng.jptabbar.anno.SeleIcons;
import com.jpeng.jptabbar.anno.Titles;
import com.rair.diary.R;
import com.rair.diary.base.RairApp;
import com.rair.diary.constant.Constants;
import com.rair.diary.ui.diary.DiaryFragment;
import com.rair.diary.ui.find.FindFragment;
import com.rair.diary.ui.setting.SetFragment;
import com.rair.diary.utils.RairUtils;
import com.rair.diary.utils.SPUtils;
import com.rair.diary.view.LockPattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity implements OnTabSelectListener {

    @Titles
    public static final int[] titles = {R.string.diary, R.string.find, R.string.mine};
    @SeleIcons
    public static final int[] selIcons = {R.mipmap.ic_diary_sel, R.mipmap.ic_find_sel, R.mipmap.ic_setting_sel};
    @NorIcons
    public static final int[] icons = {R.mipmap.ic_diary, R.mipmap.ic_find, R.mipmap.ic_setting};

    @BindView(R.id.main_fl_container)
    FrameLayout mainFlContainer;
    @BindView(R.id.main_jp_tabbar)
    JPTabBar mainJpTabbar;
    private Unbinder bind;
    private Fragment[] fragments;
    private SPUtils spUtils;
    private int lastIndex = -1;
    private long exitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bind = ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        spUtils = RairApp.getRairApp().getSpUtils();
        mainJpTabbar.setTabListener(this);
        fragments = new Fragment[3];
        if (spUtils.getBoolean(Constants.RECREATE, false)) {
            showFragment(2);
            mainJpTabbar.setSelectTab(2);
            spUtils.put(Constants.RECREATE, false);
        } else {
            showFragment(0);
            boolean hasPatternPwd = spUtils.getBoolean("hasPatternPwd", false);
            boolean hasNumberPwd = spUtils.getBoolean("hasNumberPwd", false);
            if (hasNumberPwd) {
                String numberPwd = spUtils.getString("numberPwd");
                numberUnLock(numberPwd);
            }
            if (hasPatternPwd) {
                String patternPwd = spUtils.getString("patternPwd");
                patternUnLock(patternPwd);
            }
        }
    }

    /**
     * 数字密码
     *
     * @param numberPwd 密码
     */
    private void numberUnLock(final String numberPwd) {
        View view = getLayoutInflater().inflate(R.layout.view_number_unlock_dialog, null);
        final EditText etPwd = (EditText) view.findViewById(R.id.unlock_et_pwd);
        TextView tvOk = (TextView) view.findViewById(R.id.unlock_tv_ok);
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setView(view)
                .create();
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.unlock_tv_ok:
                        String pwd = etPwd.getText().toString().trim();
                        if (TextUtils.isEmpty(pwd)) {
                            Toast.makeText(MainActivity.this, "请输入数字密码", Toast.LENGTH_SHORT).show();
                        } else if (pwd.equals(numberPwd)) {
                            Toast.makeText(MainActivity.this, "密码正确", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(MainActivity.this, "密码错误，请重试", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        };
        tvOk.setOnClickListener(onClickListener);
        dialog.show();
    }

    /**
     * 图案解锁
     *
     * @param patternPwd
     */
    private void patternUnLock(final String patternPwd) {
        View view = getLayoutInflater().inflate(R.layout.view_pattern_unlock_dialog, null);
        final LockPattern patternLp1 = (LockPattern) view.findViewById(R.id.unlock_lp);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setView(view)
                .create();
        patternLp1.setOnCompleteListener(new LockPattern.OnCompleteListener() {
            @Override
            public void onComplete(String password) {
                if (password.equals(patternPwd)) {
                    Toast.makeText(MainActivity.this, "密码正确", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(MainActivity.this, "密码错误，5秒后重试", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPwdShortOrLong(int pwdLength) {
            }
        });
        dialog.show();
    }

    /**
     * 用于显示Fragment的方法
     *
     * @param
     */
    private void showFragment(int currentIndex) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (currentIndex == lastIndex)
            return;
        if (lastIndex != -1) {
            ft.hide(fragments[lastIndex]);
        }
        if (fragments[currentIndex] == null) {
            switch (currentIndex) {
                case 0:
                    fragments[currentIndex] = DiaryFragment.newInstance();
                    break;
                case 1:
                    fragments[currentIndex] = FindFragment.newInstance();
                    break;
                case 2:
                    fragments[currentIndex] = SetFragment.newInstance();
                    break;
            }
            ft.add(R.id.main_fl_container, fragments[currentIndex]);
        } else {
            ft.show(fragments[currentIndex]);
        }
        ft.commit();
        lastIndex = currentIndex;
    }

    @Override
    public void onTabSelect(int index) {
        showFragment(index);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void recreate() {
        super.recreate();
        spUtils.put(Constants.RECREATE, true);
    }

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            RairUtils.showSnackar(mainJpTabbar, "再按一次退出");
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }
}

