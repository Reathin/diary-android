package com.rair.diary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.rair.diary.R;
import com.rair.diary.bean.Diary;
import com.rair.diary.bean.User;
import com.rair.diary.view.CircleImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Rair on 2017/6/12.
 * Email:rairmmd@gmail.com
 * Author:Rair
 */
public class FindXrvAdapter extends XRecyclerView.Adapter<FindXrvAdapter.FindHolder> {

    private Context context;
    private ArrayList<Diary> datas;
    private OnRvItemClickListener onRvItemClickListener;

    public interface OnRvItemClickListener {
        void OnItemClick(int position);
    }

    public void setOnRvItemClickListener(OnRvItemClickListener onRvItemClickListener) {
        this.onRvItemClickListener = onRvItemClickListener;
    }

    public FindXrvAdapter(Context context, ArrayList<Diary> datas) {
        this.context = context;
        this.datas = datas;
    }

    @Override
    public FindHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_find_item, parent, false);
        return new FindHolder(view);
    }

    @Override
    public void onBindViewHolder(final FindHolder holder, int position) {
        Diary diary = datas.get(position);
        User user = diary.getUser();
        if (user.getSex() != null) {
            if (user.getSex().equals("nan")) {
                Picasso.with(context).load(R.mipmap.male).into(holder.ivSex);
            } else {
                Picasso.with(context).load(R.mipmap.female).into(holder.ivSex);
            }
        }
        if (user.getHeadFile() != null) {
            BmobFile headFileFile = user.getHeadFile();
            Picasso.with(context).load(headFileFile.getFileUrl()).into(holder.civHead);
        } else {
            Picasso.with(context).load(R.mipmap.ic_head).into(holder.civHead);
        }
        holder.tvName.setText(diary.getName());
        holder.tvTime.setText(diary.getCreateTime());
        holder.tvContent.setText(diary.getContent());
        final int mPosition = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRvItemClickListener != null)
                    onRvItemClickListener.OnItemClick(mPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class FindHolder extends XRecyclerView.ViewHolder {

        private CircleImageView civHead;
        private ImageView ivSex;
        private TextView tvName, tvTime, tvContent;

        FindHolder(View itemView) {
            super(itemView);
            civHead = (CircleImageView) itemView.findViewById(R.id.find_item_civ_head);
            ivSex = (ImageView) itemView.findViewById(R.id.find_item_iv_sex);
            tvName = (TextView) itemView.findViewById(R.id.find_item_tv_name);
            tvTime = (TextView) itemView.findViewById(R.id.find_item_tv_time);
            tvContent = (TextView) itemView.findViewById(R.id.find_item_tv_content);
        }
    }
}
