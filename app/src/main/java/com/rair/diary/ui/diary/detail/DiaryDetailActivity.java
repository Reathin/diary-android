package com.rair.diary.ui.diary.detail;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rair.diary.R;
import com.rair.diary.constant.Constants;
import com.rair.diary.db.DiaryDao;
import com.rair.diary.utils.RairUtils;
import com.rair.diary.view.LinedEditText;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class DiaryDetailActivity extends AppCompatActivity {

    @BindView(R.id.detail_iv_back)
    ImageView detailIvBack;
    @BindView(R.id.detail_iv_save)
    ImageView detailIvSave;
    @BindView(R.id.detail_et_title)
    EditText detailEtTitle;
    @BindView(R.id.detail_et_content)
    LinedEditText detailEtContent;
    @BindView(R.id.detail_fab_edit)
    FloatingActionButton detailFabEdit;
    @BindView(R.id.detail_fab_delete)
    FloatingActionButton detailFabDelete;
    @BindView(R.id.detail_ll_option)
    LinearLayout detailLlOption;
    @BindView(R.id.detail_iv_show)
    ImageView detailIvShow;
    @BindView(R.id.detail_tv_tite)
    TextView detailTvTite;
    private Unbinder bind;
    private DiaryDao diaryDao;
    private long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_detail);
        bind = ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        diaryDao = new DiaryDao(this);
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        String image = intent.getStringExtra("image");
        String date = intent.getStringExtra("date");
        String week = intent.getStringExtra("week");
        String weather = intent.getStringExtra("weather");
        id = intent.getLongExtra("id", 0);
        detailEtTitle.setText(title);
        detailEtContent.setText(content);
        detailTvTite.setText(String.format(Constants.FORMAT, date, week, weather));
        if (image != null || !image.equals("n") || !TextUtils.isEmpty(image)) {
            File file = new File(image);
            if (file.exists()) {
                detailIvShow.setVisibility(View.VISIBLE);
                Bitmap bitmap = BitmapFactory.decodeFile(image);
                detailIvShow.setImageBitmap(bitmap);
            } else {
                detailIvShow.setVisibility(View.GONE);
            }
        }
    }

    @OnClick({R.id.detail_iv_back, R.id.detail_iv_save, R.id.detail_fab_edit, R.id.detail_fab_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.detail_iv_back:
                this.finish();
                break;
            case R.id.detail_iv_save:
                doSave();
                break;
            case R.id.detail_fab_edit:
                detailEtTitle.setEnabled(true);
                detailEtContent.setEnabled(true);
                detailIvSave.setVisibility(View.VISIBLE);
                detailLlOption.setVisibility(View.GONE);
                break;
            case R.id.detail_fab_delete:
                doDelete();
                break;
        }
    }

    /**
     * 保存的方法
     */
    private void doSave() {
        String title = detailEtTitle.getText().toString().trim();
        String content = detailEtContent.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            title = "无标题";
        }
        if (TextUtils.isEmpty(content)) {
            content = "无内容";
        }
        diaryDao.update(title, content, id);
        this.finish();
        RairUtils.showSnackar(detailEtContent, "保存成功");
        RairUtils.hideInput(this);
    }

    /**
     * 删除的方法
     */
    private void doDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogStyle);
        builder.setMessage(getString(R.string.delete_sure));
        builder.setTitle(getString(R.string.delete));
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                diaryDao.delete(id);
                dialog.dismiss();
                DiaryDetailActivity.this.finish();
                Toast.makeText(DiaryDetailActivity.this, getString(R.string.delete_over), Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }
}
