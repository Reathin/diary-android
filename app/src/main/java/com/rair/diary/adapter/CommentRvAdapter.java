package com.rair.diary.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rair.diary.R;
import com.rair.diary.bean.Comment;
import com.rair.diary.bean.User;
import com.rair.diary.view.CircleImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Rair on 2017/6/25.
 * Email:rairmmd@gmail.com
 * Author:Rair
 */

public class CommentRvAdapter extends RecyclerView.Adapter<CommentRvAdapter.CommentHolder> {

    private Context context;
    private ArrayList<Comment> datas;

    public CommentRvAdapter(Context context, ArrayList<Comment> datas) {
        this.context = context;
        this.datas = datas;
    }

    @Override
    public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_comment_item, parent, false);
        return new CommentHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentHolder holder, int position) {
        Comment comment = datas.get(position);
        User user = comment.getUser();
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
        holder.tvName.setText(user.getNickName());
        holder.tvContent.setText(comment.getContent());
        holder.tvTime.setText(comment.getCommentTime());
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class CommentHolder extends RecyclerView.ViewHolder {

        private CircleImageView civHead;
        private ImageView ivSex;
        private TextView tvName, tvTime, tvContent;

        CommentHolder(View itemView) {
            super(itemView);
            civHead = (CircleImageView) itemView.findViewById(R.id.comment_item_civ_head);
            ivSex = (ImageView) itemView.findViewById(R.id.comment_item_iv_sex);
            tvName = (TextView) itemView.findViewById(R.id.comment_item_tv_name);
            tvTime = (TextView) itemView.findViewById(R.id.comment_item_tv_time);
            tvContent = (TextView) itemView.findViewById(R.id.comment_item_tv_content);
        }
    }
}
