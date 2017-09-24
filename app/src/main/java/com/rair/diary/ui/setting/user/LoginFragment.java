package com.rair.diary.ui.setting.user;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rair.diary.R;
import com.rair.diary.base.RairApp;
import com.rair.diary.bean.User;
import com.rair.diary.utils.RairUtils;
import com.rair.diary.utils.SPUtils;
import com.rair.diary.view.EditTextWithDel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    @BindView(R.id.login_et_name)
    EditTextWithDel loginEtName;
    @BindView(R.id.login_et_pwd)
    EditTextWithDel loginEtPwd;
    @BindView(R.id.login_tv_login)
    TextView loginTvLogin;
    @BindView(R.id.login_tv_forget)
    TextView loginTvForget;
    Unbinder unbinder;
    private SPUtils spUtils;

    public static LoginFragment newInstance() {
        LoginFragment loginFragment = new LoginFragment();
        return loginFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        spUtils = RairApp.getRairApp().getSpUtils();
    }

    @OnClick({R.id.login_tv_login, R.id.login_tv_forget})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login_tv_login:
                doLogin();
                break;
            case R.id.login_tv_forget:
                break;
        }
    }

    /**
     * 登录操作
     */
    private void doLogin() {
        String userName = loginEtName.getText().toString();
        String userPwd = loginEtPwd.getText().toString();
        if (TextUtils.isEmpty(userName)) {
            RairUtils.showSnackar(loginTvLogin, "请输入用户名/邮箱");
            return;
        }
        if (TextUtils.isEmpty(userPwd)) {
            RairUtils.showSnackar(loginTvLogin, "请输入密码");
            return;
        }
        User user = new User();
        user.setUsername(userName);
        user.setNickName(userName);
        user.setPassword(userPwd);
        user.login(new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    RairUtils.showSnackar(loginTvLogin, "登陆成功");
                    getActivity().finish();
                } else {
                    switch (e.getErrorCode()) {
                        case 101:
                            RairUtils.showSnackar(loginTvLogin, "登陆失败，用户不存在或密码错误");
                            break;
                        default:
                            RairUtils.showSnackar(loginTvLogin, "登陆失败");
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
