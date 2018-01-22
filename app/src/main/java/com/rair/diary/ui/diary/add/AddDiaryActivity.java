package com.rair.diary.ui.diary.add;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rair.diary.R;
import com.rair.diary.bean.DiaryBean;
import com.rair.diary.constant.Constants;
import com.rair.diary.db.DiaryDao;
import com.rair.diary.utils.RairUtils;
import com.rair.diary.view.LinedEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class AddDiaryActivity extends AppCompatActivity {

    @BindView(R.id.add_et_title)
    EditText addEtTitle;
    @BindView(R.id.add_et_content)
    LinedEditText addEtContent;
    @BindView(R.id.add_iv_back)
    ImageView addIvBack;
    @BindView(R.id.add_iv_save)
    ImageView addIvSave;
    @BindView(R.id.add_tv_title)
    TextView addTvTitle;
    @BindView(R.id.add_iv_qing)
    ImageView addIvQing;
    @BindView(R.id.add_iv_yin)
    ImageView addIvYin;
    @BindView(R.id.add_iv_yu)
    ImageView addIvYu;
    @BindView(R.id.add_iv_leiyu)
    ImageView addIvLeiyu;
    @BindView(R.id.add_iv_xue)
    ImageView addIvXue;
    @BindView(R.id.add_ll_weather)
    LinearLayout addLlWeather;
    @BindView(R.id.add_iv_photo)
    ImageView addIvPhoto;
    @BindView(R.id.add_iv_weather)
    ImageView addIvWeather;
    @BindView(R.id.add_iv_show)
    ImageView addIvShow;
    private Unbinder unbinder;
    public static String[] WEEK = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
    public static final int WEEKDAYS = 7;
    private String mDate;
    private String mWeek;
    private String weather;
    private String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_diary);
        unbinder = ButterKnife.bind(this);
        intView();
    }

    private void intView() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        mDate = dateFormat.format(date);
        mWeek = DateToWeek(date);
        addTvTitle.setText(String.format(Constants.FORMAT,mDate,mWeek,""));
    }

    public String DateToWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayIndex = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayIndex < 1 || dayIndex > WEEKDAYS) {
            return null;
        }
        return WEEK[dayIndex - 1];
    }

    @OnClick({R.id.add_iv_back, R.id.add_iv_save, R.id.add_iv_qing, R.id.add_iv_yin,
            R.id.add_iv_yu, R.id.add_iv_leiyu, R.id.add_iv_xue, R.id.add_iv_photo, R.id.add_iv_weather})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add_iv_back:
                this.finish();
                break;
            case R.id.add_iv_save:
                doSave();
                break;
            case R.id.add_iv_qing:
                addLlWeather.setVisibility(View.GONE);
                addIvWeather.setImageResource(R.mipmap.ic_weather_qing);
                weather = "晴";
                break;
            case R.id.add_iv_yin:
                addLlWeather.setVisibility(View.GONE);
                addIvWeather.setImageResource(R.mipmap.ic_weather_qing);
                weather = "阴";
                break;
            case R.id.add_iv_yu:
                addLlWeather.setVisibility(View.GONE);
                addIvWeather.setImageResource(R.mipmap.ic_weather_yu);
                weather = "雨";
                break;
            case R.id.add_iv_leiyu:
                addLlWeather.setVisibility(View.GONE);
                addIvWeather.setImageResource(R.mipmap.ic_weather_leiyu);
                weather = "雷雨";
                break;
            case R.id.add_iv_xue:
                addLlWeather.setVisibility(View.GONE);
                addIvWeather.setImageResource(R.mipmap.ic_weather_xue);
                weather = "雪";
                break;
            case R.id.add_iv_photo:
                addLlWeather.setVisibility(View.GONE);
                checkSelfPermission();
                break;
            case R.id.add_iv_weather:
                addLlWeather.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void doSave() {
        String title = addEtTitle.getText().toString().trim();
        String content = addEtContent.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            title = "无标题";
        }
        if (TextUtils.isEmpty(content)) {
            content = "无内容";
        }
        if (TextUtils.isEmpty(weather)) {
            weather = "晴";
        }
        if (TextUtils.isEmpty(image)) {
            image = "";
        }
        DiaryDao diaryDao = new DiaryDao(this);
        DiaryBean diary = new DiaryBean();
        diary.setDate(mDate);
        diary.setWeek(mWeek);
        diary.setWeather(weather);
        diary.setTitle(title);
        diary.setContent(content);
        diary.setImage(image);
        diaryDao.insert(diary);
        this.finish();
        RairUtils.hideInput(this);
        RairUtils.showSnackar(addEtContent, "保存成功");
    }

    /**
     * 检查权限
     */
    private void checkSelfPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                RairUtils.showSnackar(addIvPhoto, "需要读写权限");
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
        } else {
            if (image == null) {
                MultiImageSelector.create()
                        .showCamera(true)
                        .single()
                        .start(this, 0);
            } else {
                RairUtils.showSnackar(addIvPhoto, "你已经选择了一张图片");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MultiImageSelector.create()
                            .showCamera(true) // 是否显示相机. 默认为显示
                            .single() // 单选模式
                            .multi() // 多选模式, 默认模式;
                            .start(this, 0);
                } else {
                    RairUtils.showSnackar(addIvPhoto, "没有授予读写权限，导出失败,请到设置中手动打开");
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == 0) {
            List<String> selectPaths = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            if (selectPaths.size() != 0) {
                String imagePath = selectPaths.get(0);
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                addIvShow.setVisibility(View.VISIBLE);
                addIvShow.setImageBitmap(bitmap);
                image = imagePath;
            } else {
                addIvShow.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

}
