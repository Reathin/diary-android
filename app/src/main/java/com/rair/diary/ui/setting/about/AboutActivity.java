package com.rair.diary.ui.setting.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rair.diary.R;
import com.rair.diary.constant.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.about_iv_back)
    ImageView aboutIvBack;
    @BindView(R.id.about_tv_join)
    TextView aboutTvJoin;
    @BindView(R.id.about_tv_version)
    TextView aboutTvVersion;
    @BindView(R.id.about_tv_support)
    TextView aboutTvSupport;
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        unbinder = ButterKnife.bind(this);
    }

    @OnClick({R.id.about_iv_back, R.id.about_tv_join, R.id.about_tv_version, R.id.about_tv_support})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.about_iv_back:
                this.finish();
                break;
            case R.id.about_tv_join:
                joinQQGroup(Constants.QQ_KEY);
                break;
            case R.id.about_tv_version:
                break;
            case R.id.about_tv_support:
                break;
        }
    }

    /****************
     *
     * 发起添加群流程。群号：Rair修仙开发交流(650494950) 的 key 为： J_-56inXPgycIyUIF9tPW19bfUFZul9m
     * 调用 joinQQGroup(J_-56inXPgycIyUIF9tPW19bfUFZul9m) 即可发起手Q客户端申请加群 Rair修仙开发交流(650494950)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     ******************/
    public boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
