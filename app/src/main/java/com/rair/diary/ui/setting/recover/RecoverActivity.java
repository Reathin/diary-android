package com.rair.diary.ui.setting.recover;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.rair.diary.R;
import com.rair.diary.base.RairApp;
import com.rair.diary.bean.User;
import com.rair.diary.constant.Constants;
import com.rair.diary.utils.RairUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class RecoverActivity extends AppCompatActivity {

    @BindView(R.id.recover_iv_back)
    ImageView recoverIvBack;
    @BindView(R.id.recover_ll_upload)
    LinearLayout recoverLlUpload;
    @BindView(R.id.recover_ll_download)
    LinearLayout recoverLlDownload;
    private Unbinder unbinder;
    private ProgressDialog progressDialog;
    private String sdPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover);
        unbinder = ButterKnife.bind(this);
        init();
    }

    private void init() {
        progressDialog = new ProgressDialog(this, R.style.DialogStyle);
        progressDialog.setCanceledOnTouchOutside(false);
        File rairPath = RairApp.getRairApp().getRairPath();
        sdPath = new File(rairPath, Constants.BACKUP_NAME).getAbsolutePath();
    }

    @OnClick({R.id.recover_iv_back, R.id.recover_ll_upload, R.id.recover_ll_download})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.recover_iv_back:
                this.finish();
                break;
            case R.id.recover_ll_upload:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                        RairUtils.showSnackar(recoverLlDownload, "需要读写权限");
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                    }
                } else {
                    doUpLoad();
                }
                break;
            case R.id.recover_ll_download:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                        RairUtils.showSnackar(recoverLlDownload, "需要读写权限");
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                } else {
                    doDownLoad();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doUpLoad();
                } else {
                    RairUtils.showSnackar(recoverLlDownload, "没有授予读写权限，导出失败,请到设置中手动打开");
                }
                break;
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doDownLoad();
                } else {
                    RairUtils.showSnackar(recoverLlDownload, "没有授予读写权限，恢复失败,请到设置中手动打开");
                }
                break;
        }
    }

    /**
     * 上传
     */
    private void doUpLoad() {
        progressDialog.setMessage("正在上传。。。");
        progressDialog.show();
        String dbPath = this.getDatabasePath(Constants.DB_NAME).getAbsolutePath();
        boolean success = copyFile(dbPath, sdPath);
        if (success) {
            final BmobFile dbFile = new BmobFile(new File(sdPath));
            dbFile.uploadblock(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        User user = BmobUser.getCurrentUser(User.class);
                        user.setDbFile(dbFile);
                        user.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    progressDialog.dismiss();
                                    RairUtils.showSnackar(recoverLlDownload, "备份成功");
                                } else {
                                    progressDialog.dismiss();
                                    RairUtils.showSnackar(recoverLlDownload, "备份失败");
                                }
                            }
                        });
                    } else {
                        progressDialog.dismiss();
                    }

                }

                @Override
                public void onProgress(Integer value) {
                    progressDialog.setProgress(value);
                }
            });
        } else {
            RairUtils.showSnackar(recoverLlDownload, "文件导出错误");
        }
    }

    /**
     * 下载
     */
    private void doDownLoad() {
        progressDialog.setMessage("正在下载。。。");
        progressDialog.show();
        User user = BmobUser.getCurrentUser(User.class);
        if (user.getDbFile() != null) {
            BmobFile dbFile = user.getDbFile();
            dbFile.download(new File(sdPath), new DownloadFileListener() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        RairUtils.showSnackar(recoverLlDownload, "下载成功,保存路径:" + s);
                        progressDialog.setMessage("正在还原。。。");
                        String dbPath = RecoverActivity.this.getDatabasePath(Constants.DB_NAME).getAbsolutePath();
                        boolean success = pasteFile(sdPath, dbPath);
                        if (success) {
                            progressDialog.dismiss();
                            RairUtils.showSnackar(recoverLlDownload, "恢复成功");
                        } else {
                            progressDialog.dismiss();
                            RairUtils.showSnackar(recoverLlDownload, "恢复失败");
                        }
                    } else {
                        progressDialog.dismiss();
                        RairUtils.showSnackar(recoverLlDownload, "下载失败");
                    }
                }

                @Override
                public void onProgress(Integer integer, long l) {
                    progressDialog.setMessage("正在下载。。。网速" + l);
                    progressDialog.setProgress(integer);
                }
            });
        } else {
            progressDialog.dismiss();
            RairUtils.showSnackar(recoverLlDownload, "没有找到云端备份文件");
        }

    }

    /**
     * 复制单个文件
     *
     * @param dbPath String 原文件路径
     * @param sdPath String 复制后路径
     * @return boolean
     */
    public boolean copyFile(String dbPath, String sdPath) {
        try {
            int len;
            File dbFile = new File(dbPath);
            File sdFile = new File(sdPath);
            if (!sdFile.exists()) {
                sdFile.createNewFile();
            }
            if (dbFile.exists()) { // 文件存在时
                FileInputStream fis = new FileInputStream(dbPath); // 读入原文件
                FileOutputStream fos = new FileOutputStream(sdPath);
                byte[] buffer = new byte[1024];
                while ((len = fis.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    fos.flush();
                }
                fis.close();
                fos.close();
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 还原数据
     *
     * @param sdPath
     * @param dbPath
     * @return
     */
    public boolean pasteFile(String sdPath, String dbPath) {
        try {
            int len;
            File dbFile = new File(dbPath);
            File sdFile = new File(sdPath);
            if (!dbFile.exists()) {
                dbFile.createNewFile();
            }
            if (sdFile.exists()) { // 文件存在时
                FileInputStream fis = new FileInputStream(sdFile); // 读入原文件
                FileOutputStream fos = new FileOutputStream(dbPath);
                byte[] buffer = new byte[1024];
                while ((len = fis.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    fos.flush();
                }
                fis.close();
                fos.close();
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
