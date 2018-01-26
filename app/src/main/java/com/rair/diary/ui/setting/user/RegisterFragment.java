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
import com.rair.diary.bean.User;
import com.rair.diary.utils.RairUtils;
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
public class RegisterFragment extends Fragment {


    @BindView(R.id.register_et_name)
    EditTextWithDel registerEtName;
    @BindView(R.id.register_et_email)
    EditTextWithDel registerEtEmail;
    @BindView(R.id.register_et_pwd)
    EditTextWithDel registerEtPwd;
    @BindView(R.id.register_tv_register)
    TextView registerTvRegister;
    Unbinder unbinder;

    public static RegisterFragment newInstance() {
        RegisterFragment registerFragment = new RegisterFragment();
        return registerFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
    }

    @OnClick(R.id.register_tv_register)
    public void onViewClicked() {
        doRegister();
    }

    /**
     * 注册操作
     */
    private void doRegister() {
        String userName = registerEtName.getText().toString();
        String userMail = registerEtEmail.getText().toString();
        String userPwd = registerEtPwd.getText().toString();
        if (TextUtils.isEmpty(userName)) {
            RairUtils.showSnackar(registerTvRegister, "请输入用户名");
            return;
        }
        if (TextUtils.isEmpty(userMail)) {
            RairUtils.showSnackar(registerTvRegister, "请输入邮箱");
            return;
        }
        if (!RairUtils.isEmail(userMail)) {
            RairUtils.showSnackar(registerTvRegister, "邮箱格式不正确");
            return;
        }
        if (TextUtils.isEmpty(userPwd)) {
            RairUtils.showSnackar(registerTvRegister, "请输入密码");
            return;
        }
        User user = new User();
        user.setUsername(userName);
        user.setSign("编辑个性签名");
        user.setEmail(userMail);
        user.setNickName(userName);
        user.setSex(0);
        user.setPassword(userPwd);
        user.signUp(new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    RairUtils.showSnackar(registerTvRegister, "注册成功，已登录");
                    getActivity().finish();
                } else {
                    switch (e.getErrorCode()) {
                        case 202:
                            RairUtils.showSnackar(registerTvRegister, "注册失败，用户名已经存在");
                            break;
                        case 203:
                            RairUtils.showSnackar(registerTvRegister, "注册失败，邮箱已经存在");
                            break;
                        default:
                            RairUtils.showSnackar(registerTvRegister, "注册失败");
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
