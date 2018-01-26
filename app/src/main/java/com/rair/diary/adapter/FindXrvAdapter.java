package com.rair.diary.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.rair.diary.R;
import com.rair.diary.bean.Diary;
import com.rair.diary.bean.User;
import com.rair.diary.view.CircleImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Rair on 2017/6/12.
 * Email:rairmmd@gmail.com
 * Author:Rair
 */
public class FindXrvAdapter extends BaseQuickAdapter<Diary, BaseViewHolder> {

    private Context context;

    public FindXrvAdapter(Context context, int layoutResId, @Nullable List<Diary> data) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, Diary item) {
        User user = item.getUser();
        ImageView ivSex = helper.getView(R.id.find_item_iv_sex);
        CircleImageView civHead = helper.getView(R.id.find_item_civ_head);
        if (user.getSex() == 0) {
            Picasso.with(context).load(R.mipmap.male).into(ivSex);
        } else {
            Picasso.with(context).load(R.mipmap.female).into(ivSex);
        }
        if (user.getHeadFile() != null) {
            BmobFile headFileFile = user.getHeadFile();
            Picasso.with(context).load(headFileFile.getFileUrl()).into(civHead);
        } else {
            Picasso.with(context).load(R.mipmap.ic_head).into(civHead);
        }
        helper.setText(R.id.find_item_tv_name, item.getName());
        helper.setText(R.id.find_item_tv_time, item.getCreateTime());
        helper.setText(R.id.find_item_tv_content, item.getContent());
    }

}
