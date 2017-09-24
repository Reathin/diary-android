package com.rair.diary.ui.setting;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.rair.diary.R;
import com.rair.diary.base.RairApp;
import com.rair.diary.bean.User;
import com.rair.diary.ui.setting.about.AboutActivity;
import com.rair.diary.ui.setting.export.ExportActivity;
import com.rair.diary.ui.setting.feedback.FeedBackActivity;
import com.rair.diary.ui.setting.recover.RecoverActivity;
import com.rair.diary.ui.setting.remind.NotifyActivity;
import com.rair.diary.ui.setting.secret.SecretActivity;
import com.rair.diary.ui.setting.user.LoginActivity;
import com.rair.diary.ui.setting.user.UserActivity;
import com.rair.diary.utils.SPUtils;
import com.rair.diary.view.CircleImageView;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * A simple {@link Fragment} subclass.
 */
public class SetFragment extends Fragment {

    @BindView(R.id.set_civ_head)
    CircleImageView setCivHead;
    @BindView(R.id.set_tv_name)
    TextView setTvName;
    @BindView(R.id.set_ll_user)
    LinearLayout setLlUser;
    @BindView(R.id.set_ll_notify)
    LinearLayout setLlNotify;
    @BindView(R.id.set_ll_recover)
    LinearLayout setLlRecover;
    @BindView(R.id.set_ll_secret)
    LinearLayout setLlSecret;
    @BindView(R.id.set_ll_export)
    LinearLayout setLlExport;
    @BindView(R.id.set_switch_night)
    Switch setSwitchNight;
    @BindView(R.id.set_ll_bg)
    LinearLayout setLlBg;
    @BindView(R.id.set_ll_info)
    LinearLayout setLlInfo;
    @BindView(R.id.set_ll_feedback)
    LinearLayout setLlFeedback;
    Unbinder unbinder;
    private SPUtils spUtils;

    public static SetFragment newInstance() {
        SetFragment setFragment = new SetFragment();
        return setFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_set, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        initView();
    }

    private void initView() {
        spUtils = RairApp.getRairApp().getSpUtils();
        boolean isNight = spUtils.getBoolean("isNight");
        if (isNight) setSwitchNight.setChecked(true);
        setSwitchNight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    spUtils.put("isNight", true);
                    RairApp.getRairApp().configTheme();
                    getActivity().recreate();
                } else {
                    spUtils.put("isNight", false);
                    RairApp.getRairApp().configTheme();
                    getActivity().recreate();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        User user = BmobUser.getCurrentUser(User.class);
        if (user != null) {
            setTvName.setText(user.getUsername());
            loadHead(user);
        } else {
            setTvName.setText("未登录");
        }
    }

    /**
     * 加载头像
     *
     * @param user
     */
    private void loadHead(User user) {
        if (user.getHeadFile() != null) {
            BmobFile headFile = user.getHeadFile();
            Picasso.with(getContext()).load(headFile.getFileUrl()).into(setCivHead);
        } else {
            Picasso.with(getContext()).load(R.mipmap.ic_head).into(setCivHead);
        }
    }

    @OnClick({R.id.set_ll_user, R.id.set_ll_notify, R.id.set_ll_recover, R.id.set_ll_secret, R.id.set_ll_export, R.id.set_ll_bg, R.id.set_ll_info, R.id.set_ll_feedback})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.set_ll_user:
                if (BmobUser.getCurrentUser(User.class) == null) {
                    Intent loginIntent = new Intent(getContext(), LoginActivity.class);
                    startActivity(loginIntent);
                } else {
                    Intent userIntent = new Intent(getContext(), UserActivity.class);
                    startActivity(userIntent);
                }
                break;
            case R.id.set_ll_notify:
                Intent notifyIntent = new Intent(getContext(), NotifyActivity.class);
                startActivity(notifyIntent);
                break;
            case R.id.set_ll_recover:
                if (BmobUser.getCurrentUser(User.class) == null) {
                    Intent loginIntent = new Intent(getContext(), LoginActivity.class);
                    startActivity(loginIntent);
                } else {
                    Intent recoverIntent = new Intent(getContext(), RecoverActivity.class);
                    startActivity(recoverIntent);
                }
                break;
            case R.id.set_ll_secret:
                Intent secretIntent = new Intent(getContext(), SecretActivity.class);
                startActivity(secretIntent);
                break;
            case R.id.set_ll_export:
                Intent exportIntent = new Intent(getContext(), ExportActivity.class);
                startActivity(exportIntent);
                break;
            case R.id.set_ll_bg:
                break;
            case R.id.set_ll_info:
                Intent aboutIntent = new Intent(getContext(), AboutActivity.class);
                startActivity(aboutIntent);
                break;
            case R.id.set_ll_feedback:
                Intent feedbackIntent = new Intent(getContext(), FeedBackActivity.class);
                startActivity(feedbackIntent);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
