package com.rair.diary.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rair.diary.R;
import com.rair.diary.bean.DiaryBean;
import com.rair.diary.constant.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by mzaiy on 2017/6/1.
 */

public class DiaryRvAdapter extends RecyclerView.Adapter<DiaryRvAdapter.DiaryHolder> {

    private Context context;
    private ArrayList<DiaryBean> datas;
    private OnRvItemClickListener onRvItemClickListener;

    public interface OnRvItemClickListener {
        void OnItemClick(int position);

        void OnOptionClick(int position);
    }

    public void setOnRvItemClickListener(OnRvItemClickListener onRvItemClickListener) {
        this.onRvItemClickListener = onRvItemClickListener;
    }

    public DiaryRvAdapter(Context context, ArrayList<DiaryBean> datas) {
        this.context = context;
        this.datas = datas;
    }

    @Override
    public DiaryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_diary_item, parent, false);
        return new DiaryHolder(view);
    }

    @Override
    public void onBindViewHolder(DiaryHolder holder, int position) {
        setColor(holder);
        DiaryBean diaryBean = datas.get(position);
        holder.tvTitle.setText(diaryBean.getTitle());
        holder.tvContent.setText(diaryBean.getContent());
        String image = diaryBean.getImage();
        if (!image.equals("n")) {
            File file = new File(image);
            if (file.exists()) {
                holder.ivShow.setVisibility(View.VISIBLE);
                Bitmap bitmap = BitmapFactory.decodeFile(image);
                holder.ivShow.setImageBitmap(bitmap);
            } else {
                holder.ivShow.setVisibility(View.GONE);
            }
        }
        holder.tvDate.setText(String.format(Constants.FORMAT, diaryBean.getDate(), diaryBean.getWeek(), diaryBean.getWeather()));
        final int mPosition = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRvItemClickListener != null)
                    onRvItemClickListener.OnItemClick(mPosition);
            }
        });
        holder.ivOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRvItemClickListener != null)
                    onRvItemClickListener.OnOptionClick(mPosition);
            }
        });
    }

    private void setColor(DiaryHolder holder) {
        int random = new Random().nextInt(9);
        switch (random) {
            case 0:
                holder.viewColor.setBackgroundColor(Color.rgb(255, 82, 82));
                break;
            case 1:
                holder.viewColor.setBackgroundColor(Color.rgb(76, 175, 80));
                break;
            case 2:
                holder.viewColor.setBackgroundColor(Color.rgb(255, 64, 129));
                break;
            case 3:
                holder.viewColor.setBackgroundColor(Color.rgb(68, 138, 255));
                break;
            case 4:
                holder.viewColor.setBackgroundColor(Color.rgb(255, 255, 0));
                break;
            case 5:
                holder.viewColor.setBackgroundColor(Color.rgb(224, 64, 251));
                break;
            case 6:
                holder.viewColor.setBackgroundColor(Color.rgb(24, 255, 255));
                break;
            case 7:
                holder.viewColor.setBackgroundColor(Color.rgb(97, 97, 97));
                break;
            case 8:
                holder.viewColor.setBackgroundColor(Color.rgb(255, 171, 64));
                break;

        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class DiaryHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle, tvContent, tvDate;
        private View viewColor;
        private ImageView ivOption, ivShow;

        DiaryHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.diary_item_tv_title);
            tvContent = (TextView) itemView.findViewById(R.id.diary_item_tv_content);
            tvDate = (TextView) itemView.findViewById(R.id.diary_item_tv_date);
            viewColor = itemView.findViewById(R.id.diary_item_view_color);
            ivOption = (ImageView) itemView.findViewById(R.id.diary_item_iv_option);
            ivShow = (ImageView) itemView.findViewById(R.id.diary_item_iv_show);
        }
    }
}
