package com.rair.diary.ui.setting.secret;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rair.diary.R;
import com.rair.diary.base.RairApp;
import com.rair.diary.utils.RairUtils;
import com.rair.diary.utils.SPUtils;
import com.rair.diary.view.LockPattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SecretActivity extends AppCompatActivity {

    @BindView(R.id.secret_iv_back)
    ImageView secretIvBack;
    @BindView(R.id.secret_rl_number)
    RelativeLayout secretRlNumber;
    @BindView(R.id.secret_rl_pattern)
    RelativeLayout secretRlPattern;
    @BindView(R.id.secret_rl_clear)
    RelativeLayout secretRlClear;
    private Unbinder unbinder;
    private SPUtils spUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret);
        unbinder = ButterKnife.bind(this);
        spUtils = RairApp.getRairApp().getSpUtils();
    }

    @OnClick({R.id.secret_iv_back, R.id.secret_rl_number, R.id.secret_rl_pattern, R.id.secret_rl_clear})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.secret_iv_back:
                this.finish();
                break;
            case R.id.secret_rl_number:
                numberPwd();
                break;
            case R.id.secret_rl_pattern:
                patternPwd();
                break;
            case R.id.secret_rl_clear:
                spUtils.put("hasPatternPwd", false);
                spUtils.put("hasNumberPwd", false);
                spUtils.remove("patternPwd");
                spUtils.remove("numberPwd");
                RairUtils.showSnackar(secretRlClear, "密码已清空");
                break;
        }
    }

    /**
     * 数字密码
     */
    private void numberPwd() {
        View view = getLayoutInflater().inflate(R.layout.view_number_pwd_dialog, null);
        final EditText etPwd = (EditText) view.findViewById(R.id.number_et_pwd);
        final EditText etPwd1 = (EditText) view.findViewById(R.id.number_et_pwd1);
        TextView tvCancel = (TextView) view.findViewById(R.id.number_tv_cancel);
        TextView tvOk = (TextView) view.findViewById(R.id.number_tv_ok);
        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.number_tv_cancel:
                        dialog.dismiss();
                        break;
                    case R.id.number_tv_ok:
                        String pwd = etPwd.getText().toString().trim();
                        String pwd1 = etPwd1.getText().toString().trim();
                        if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(pwd1)) {
                            Toast.makeText(SecretActivity.this, "请输入数字密码", Toast.LENGTH_SHORT).show();
                        } else if (pwd.equals(pwd1)) {
                            spUtils.put("numberPwd", pwd);
                            spUtils.put("hasNumberPwd", true);
                            spUtils.put("hasPatternPwd", false);
                            dialog.dismiss();
                            Toast.makeText(SecretActivity.this, "数字密码设置成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SecretActivity.this, "两次输入密码不一致", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        };
        tvCancel.setOnClickListener(onClickListener);
        tvOk.setOnClickListener(onClickListener);
        dialog.show();
    }

    /**
     * 图案密码
     */
    private void patternPwd() {
        View view = getLayoutInflater().inflate(R.layout.view_pattern_pwd_dialog, null);
        final LockPattern patternLp1 = (LockPattern) view.findViewById(R.id.pattern_lp_1);
        final LockPattern patternLp2 = (LockPattern) view.findViewById(R.id.pattern_lp_2);
        final LinearLayout patternLl1 = (LinearLayout) view.findViewById(R.id.pattern_ll_1);
        final LinearLayout patternLl2 = (LinearLayout) view.findViewById(R.id.pattern_ll_2);
        TextView patternTvCancel = (TextView) view.findViewById(R.id.pattern_tv_cancel);
        TextView patternTvNext = (TextView) view.findViewById(R.id.pattern_tv_next);
        TextView patternTvUp = (TextView) view.findViewById(R.id.pattern_tv_up);
        TextView patternTvOk = (TextView) view.findViewById(R.id.pattern_tv_ok);

        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.pattern_tv_cancel:
                        dialog.dismiss();
                        break;
                    case R.id.pattern_tv_next:
                        patternLl1.setVisibility(View.GONE);
                        patternLl2.setVisibility(View.VISIBLE);
                        patternLp1.setVisibility(View.GONE);
                        patternLp2.setVisibility(View.VISIBLE);
                        break;
                    case R.id.pattern_tv_up:
                        patternLl1.setVisibility(View.VISIBLE);
                        patternLl2.setVisibility(View.GONE);
                        patternLp1.setVisibility(View.VISIBLE);
                        patternLp2.setVisibility(View.GONE);
                        break;
                    case R.id.pattern_tv_ok:
                        String patternPwd = spUtils.getString("patternPwd");
                        String patternPwd1 = spUtils.getString("patternPwd1");
                        if (patternPwd1.equals(patternPwd)) {
                            spUtils.put("hasPatternPwd", true);
                            spUtils.put("hasNumberPwd", false);
                            spUtils.remove("patternPwd1");
                            Toast.makeText(SecretActivity.this, "图案密码设置成功", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(SecretActivity.this, "两次图案不一致", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        };
        patternTvCancel.setOnClickListener(onClickListener);
        patternTvNext.setOnClickListener(onClickListener);
        patternTvUp.setOnClickListener(onClickListener);
        patternTvOk.setOnClickListener(onClickListener);

        patternLp1.setOnCompleteListener(new LockPattern.OnCompleteListener() {
            @Override
            public void onComplete(String password) {
                spUtils.put("patternPwd", password);
            }

            @Override
            public void onPwdShortOrLong(int pwdLength) {
                Toast.makeText(SecretActivity.this, "至少3个圈圈", Toast.LENGTH_SHORT).show();
            }
        });
        patternLp2.setOnCompleteListener(new LockPattern.OnCompleteListener() {
            @Override
            public void onComplete(String password) {
                spUtils.put("patternPwd1", password);
            }

            @Override
            public void onPwdShortOrLong(int pwdLength) {
                Toast.makeText(SecretActivity.this, "至少3个圈圈", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
