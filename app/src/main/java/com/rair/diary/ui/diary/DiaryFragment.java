package com.rair.diary.ui.diary;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.rair.diary.R;
import com.rair.diary.adapter.DiaryRvAdapter;
import com.rair.diary.base.RairApp;
import com.rair.diary.bean.Diary;
import com.rair.diary.bean.DiaryBean;
import com.rair.diary.bean.User;
import com.rair.diary.db.DiaryDao;
import com.rair.diary.ui.diary.add.AddDiaryActivity;
import com.rair.diary.ui.diary.detail.DiaryDetailActivity;
import com.rair.diary.utils.RairUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class DiaryFragment extends Fragment implements TextWatcher, DiaryRvAdapter.OnRvItemClickListener, XRecyclerView.LoadingListener {

    @BindView(R.id.diary_et_search)
    EditText diaryEtSearch;
    @BindView(R.id.diary_xrv_list)
    XRecyclerView diaryXRvList;
    @BindView(R.id.diary_fab_add)
    FloatingActionButton diaryFabAdd;
    @BindView(R.id.diary_iv_delete)
    ImageView diaryIvDelete;
    Unbinder unbinder;
    private ArrayList<DiaryBean> datas;
    private DiaryDao diaryDao;
    private DiaryRvAdapter rvAdapter;

    public static DiaryFragment newInstance() {
        DiaryFragment diaryFragment = new DiaryFragment();
        return diaryFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_diary, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        queryDatas();
    }

    private void initView() {
        diaryDao = new DiaryDao(getContext());
        datas = new ArrayList<>();
        diaryXRvList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAdapter = new DiaryRvAdapter(getContext(), datas);
        diaryXRvList.setAdapter(rvAdapter);
        diaryXRvList.setLoadingListener(this);
        rvAdapter.setOnRvItemClickListener(this);
        diaryEtSearch.addTextChangedListener(this);
        queryDatas();
    }

    @Override
    public void onRefresh() {
        diaryXRvList.refreshComplete();
    }

    @Override
    public void onLoadMore() {
        diaryXRvList.loadMoreComplete();
    }

    @Override
    public void OnItemClick(int position) {
        Intent intent = new Intent(getContext(), DiaryDetailActivity.class);
        intent.putExtra("title", datas.get(position).getTitle());
        intent.putExtra("content", datas.get(position).getContent());
        intent.putExtra("image", datas.get(position).getImage());
        intent.putExtra("date", datas.get(position).getDate());
        intent.putExtra("week", datas.get(position).getWeek());
        intent.putExtra("weather", datas.get(position).getWeather());
        intent.putExtra("id", datas.get(position).getId());
        startActivity(intent);
    }

    @Override
    public void OnOptionClick(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DialogStyle);
        builder.setTitle(R.string.option);
        builder.setItems(new String[]{getString(R.string.share), getString(R.string.publish),
                        getString(R.string.delete), getString(R.string.export), getString(R.string.empty)},
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        if (which == 0) {
                            doShare(dialog, position);
                        } else if (which == 1) {
                            String title = datas.get(position).getTitle();
                            String content = datas.get(position).getContent();
                            String image = datas.get(position).getImage();
                            String weather = datas.get(position).getWeather();
                            String date = datas.get(position).getDate();
                            String week = datas.get(position).getWeek();
                            if (TextUtils.isEmpty(image) || image.equals("n") || image == null) {
                                doPublish(title, content, weather, date, week);
                            } else {
                                doPublishPic(title, content, image, weather, date, week);
                            }
                            dialog.dismiss();
                        } else if (which == 2) {
                            doDelete(dialog, position);
                        } else if (which == 3) {
                            doExport(dialog, position);
                        } else if (which == 4) {
                            doClear(dialog);
                        }
                    }
                });
        builder.show();
    }

    /**
     * 分享操作
     *
     * @param dialog
     * @param position
     */
    private void doShare(DialogInterface dialog, int position) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share));
        intent.putExtra(Intent.EXTRA_TITLE, datas.get(position).getTitle());
        intent.putExtra(Intent.EXTRA_TEXT, datas.get(position).getContent());
        intent.setType("text/plain");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(Intent.createChooser(intent, getString(R.string.share)));
        dialog.dismiss();
    }

    /**
     * 发布
     *
     * @param content
     * @param weather
     * @param date
     * @param week
     */
    private void doPublishPic(String title, String content, String image, String weather, String date, String week) {
        final User user = BmobUser.getCurrentUser(User.class);
        if (user != null) {
            String timeMillis = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA).format(new Date());
            final Diary diary = new Diary();
            diary.setUser(user);
            diary.setName(user.getUsername());
            diary.setTitle(title);
            diary.setContent(content);
            diary.setWeather(weather);
            diary.setDate(date);
            diary.setWeek(week);
            diary.setCreateTime(timeMillis);
            final BmobFile imageFile = new BmobFile(new File(image));
            imageFile.uploadblock(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        diary.setImage(imageFile);
                        diary.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null) {
                                    toSetRelation(user, diary);
                                }
                            }
                        });
                    }
                }
            });
        } else {
            RairUtils.showSnackar(diaryEtSearch, "请先登录后再操作");
        }
    }

    private void doPublish(String title, String content, String weather, String date, String week) {
        final User user = BmobUser.getCurrentUser(User.class);
        if (user != null) {
            String timeMillis = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA).format(new Date());
            final Diary diary = new Diary();
            diary.setUser(user);
            diary.setName(user.getUsername());
            diary.setTitle(title);
            diary.setContent(content);
            diary.setWeather(weather);
            diary.setDate(date);
            diary.setWeek(week);
            diary.setCreateTime(timeMillis);
            diary.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        toSetRelation(user, diary);
                    }
                }
            });
        } else {
            RairUtils.showSnackar(diaryEtSearch, "请先登录后再操作");
        }
    }

    /**
     * 关联
     *
     * @param user
     * @param diary
     */
    private void toSetRelation(User user, Diary diary) {
        BmobRelation relation = new BmobRelation();
        relation.add(diary);
        user.setDiary(relation);
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    RairUtils.showSnackar(diaryEtSearch, "发布成功");
                } else {
                    RairUtils.showSnackar(diaryEtSearch, "发布失败");
                }
            }
        });
    }

    /**
     * 删除操作
     *
     * @param dialog
     * @param position
     */
    private void doDelete(DialogInterface dialog, final int position) {
        dialog.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DialogStyle);
        builder.setMessage(getString(R.string.delete_sure));
        builder.setTitle(getString(R.string.delete));
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                diaryDao.delete(datas.get(position).getId());
                queryDatas();
                RairUtils.showSnackar(diaryEtSearch, getString(R.string.delete_over));
                dialogInterface.cancel();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 导出
     *
     * @param dialog
     * @param position
     */
    private void doExport(DialogInterface dialog, final int position) {
        dialog.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DialogStyle);
        builder.setTitle(getString(R.string.export));
        builder.setMessage(getString(R.string.is_export));
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String fileName = RairUtils.getTime();
                File rairPath = RairApp.getRairApp().getRairPath();
                String path = new File(rairPath, fileName + ".txt").getAbsolutePath();
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_CONTACTS)) {
                        RairUtils.showSnackar(diaryXRvList, "需要读写权限");
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                    } else {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                    }
                } else {
                    if (exportTxt(path, datas.get(position))) {
                        RairUtils.showSnackar(diaryEtSearch, getString(R.string.save_success));
                    } else {
                        RairUtils.showSnackar(diaryEtSearch, getString(R.string.save_failed));
                    }
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 清空
     *
     * @param dialog
     */
    private void doClear(DialogInterface dialog) {
        dialog.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DialogStyle);
        builder.setMessage(getString(R.string.empty_all));
        builder.setTitle(getString(R.string.emptyall));
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                diaryDao.deleteAll();
                queryDatas();
                RairUtils.showSnackar(diaryEtSearch, getString(R.string.emptyed));
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    @OnClick({R.id.diary_fab_add, R.id.diary_iv_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.diary_fab_add:
                Intent intent = new Intent(getContext(), AddDiaryActivity.class);
                startActivity(intent);
                break;
            case R.id.diary_iv_delete:
                diaryEtSearch.setText("");
                RairUtils.hideInput(getContext());
                break;
        }
    }

    /**
     * 转成txt导出
     *
     * @param outputPdfPath 导出路径
     * @param diary         日记
     * @return 是否成功
     */
    private boolean exportTxt(String outputPdfPath, DiaryBean diary) {
        try {
            File outputFile = new File(outputPdfPath);
            FileOutputStream outStream = new FileOutputStream(outputFile);
            OutputStreamWriter writer = new OutputStreamWriter(outStream, "utf-8");
            StringBuilder sb = new StringBuilder();
            sb.append(diary.getDate());
            sb.append("\t\t");
            sb.append(diary.getWeek());
            sb.append("\t\t");
            sb.append(diary.getWeather());
            sb.append("\n");
            sb.append(diary.getTitle());
            sb.append("\n");
            sb.append(diary.getContent());
            sb.append("\n");
            writer.write(sb.toString());
            writer.flush();
            writer.close();
            outStream.close();
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 查询数据
     */
    private void queryDatas() {
        datas.clear();
        diaryDao.query(datas);
        rvAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String searchText = diaryEtSearch.getText().toString().trim();
        if (!TextUtils.isEmpty(searchText)) {
            diaryDao.dimSearch(searchText, datas);
            rvAdapter.notifyDataSetChanged();
        } else {
            datas.clear();
            queryDatas();
            RairUtils.hideInput(getContext());
        }
        if (s.length() > 0) {
            diaryIvDelete.setVisibility(View.VISIBLE);
        } else {
            diaryIvDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
