package com.rair.diary.ui.find;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.rair.diary.R;
import com.rair.diary.adapter.CommentRvAdapter;
import com.rair.diary.bean.Comment;
import com.rair.diary.bean.Diary;
import com.rair.diary.bean.User;
import com.rair.diary.ui.setting.user.LoginActivity;
import com.rair.diary.utils.RairUtils;
import com.rair.diary.view.CircleImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class FindDetailActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.find_iv_back)
    ImageView findIvBack;
    @BindView(R.id.find_iv_collect)
    ImageView findIvCollect;

    @BindView(R.id.find_rv_comments)
    XRecyclerView findRvComments;
    @BindView(R.id.find_et_comments_content)
    EditText findEtCommentsContent;
    @BindView(R.id.find_tv_comments_commit)
    TextView findTvCommentsCommit;
    private Unbinder unbinder;
    private Diary diary;
    private int pageNum;
    private ArrayList<Comment> comments;
    private CommentRvAdapter adapter;
    private TextView findTvCommentsTip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_detail);
        unbinder = ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        View headerView = getLayoutInflater().inflate(R.layout.view_rv_header, null);
        View footerView = getLayoutInflater().inflate(R.layout.view_rv_footer, new LinearLayout(this), false);
        CircleImageView findCivHead = (CircleImageView) headerView.findViewById(R.id.find_civ_head);
        ImageView findIvSex = (ImageView) headerView.findViewById(R.id.find_iv_sex);
        TextView findTvName = (TextView) headerView.findViewById(R.id.find_tv_name);
        TextView findTvPublishTime = (TextView) headerView.findViewById(R.id.find_tv_publish_time);
        TextView findTvCreateTime = (TextView) headerView.findViewById(R.id.find_tv_create_time);
        TextView findTvSign = (TextView) headerView.findViewById(R.id.find_tv_sign);
        TextView findTvTitle = (TextView) headerView.findViewById(R.id.find_tv_title);
        TextView findTvContent = (TextView) headerView.findViewById(R.id.find_tv_content);
        ImageView findIvImage = (ImageView) headerView.findViewById(R.id.find_iv_image);
        findTvCommentsTip = (TextView) footerView.findViewById(R.id.find_tv_comments_tip);
        findTvCommentsTip.setOnClickListener(this);
        findRvComments.addHeaderView(headerView);
        findRvComments.setFootView(footerView);
        Intent intent = getIntent();
        diary = (Diary) intent.getSerializableExtra("diary");
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        String image = intent.getStringExtra("image");
        String date = intent.getStringExtra("date");
        String week = intent.getStringExtra("week");
        String weather = intent.getStringExtra("weather");
        String head = intent.getStringExtra("head");
        String name = intent.getStringExtra("name");
        String sex = intent.getStringExtra("sex");
        String sign = intent.getStringExtra("sign");
        String publishTime = intent.getStringExtra("publish");

        findTvTitle.setText(title);
        findTvContent.setText(content);
        findTvCreateTime.setText(String.format(getString(R.string.create), date + "\t" + week + "\t" + weather));
        findTvPublishTime.setText(String.format(getString(R.string.shareby), publishTime));
        findTvName.setText(name);
        findTvSign.setText(sign);
        if (head != null) {
            Picasso.with(this).load(head).into(findCivHead);
        } else {
            Picasso.with(this).load(R.mipmap.ic_head).into(findCivHead);
        }
        if (image != null) {
            Picasso.with(this).load(image).into(findIvImage);
        }
        if (sex != null) {
            if (sex.equals("nan")) {
                Picasso.with(this).load(R.mipmap.male).into(findIvSex);
            } else {
                Picasso.with(this).load(R.mipmap.female).into(findIvSex);
            }
        }

        comments = new ArrayList<>();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        findRvComments.setLayoutManager(layoutManager);
        findRvComments.setLoadingMoreEnabled(true);
        findRvComments.setPullRefreshEnabled(false);
        adapter = new CommentRvAdapter(this, R.layout.view_comment_item, comments);
        findRvComments.setAdapter(adapter);
        findRvComments.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                if (totalItemCount <= (lastVisibleItemPosition + 5)) {
                    findTvCommentsTip.setVisibility(View.VISIBLE);
                    //loadComments();
                } else {
                    findTvCommentsTip.setVisibility(View.GONE);
                }
            }
        });
        loadComments();
    }

    /**
     * 加载评论
     */
    private void loadComments() {
        BmobQuery<Comment> query = new BmobQuery<>();
        query.addWhereRelatedTo("comment", new BmobPointer(diary));
        query.include("user");
        query.order("createdAt");
        query.setLimit(5);
        query.setSkip(5 * (pageNum++));
        query.findObjects(new FindListener<Comment>() {
            @Override
            public void done(List<Comment> list, BmobException e) {
                if (e == null) {
                    comments.addAll(list);
                    adapter.notifyDataSetChanged();
                    if (list.size() != 0 && list.get(list.size() - 1) != null) {
                        findTvCommentsTip.setText("更多评论");
                        if (list.size() < 5) {
                            findTvCommentsTip.setText("暂无更多评论~");
                        }
                    } else {
                        findTvCommentsTip.setText("暂无更多评论~");
                        pageNum--;
                    }
                } else {
                    pageNum--;
                }
            }
        });
    }

    @OnClick({R.id.find_iv_back, R.id.find_tv_comments_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.find_iv_back:
                this.finish();
                break;
            case R.id.find_tv_comments_commit:
                pushComment();
                break;
            case R.id.find_tv_comments_tip:
                loadComments();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.find_tv_comments_tip:
                loadComments();
                break;
        }
    }

    /**
     * 提交评论
     */

    private void pushComment() {
        User user = BmobUser.getCurrentUser(User.class);
        if (user == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            String commentStr = findEtCommentsContent.getText().toString().trim();
            if (TextUtils.isEmpty(commentStr)) {
                RairUtils.showSnackar(findEtCommentsContent, "来评论一句吧");
                return;
            }
            String timeMillis = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA).format(new Date());
            final Comment comment = new Comment();
            comment.setUser(user);
            comment.setDiary(diary);
            comment.setCommentTime(timeMillis);
            comment.setContent(commentStr);
            comment.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        comments.add(0, comment);
                        adapter.notifyDataSetChanged();
                        findEtCommentsContent.setText("");
                        RairUtils.hideInput(FindDetailActivity.this);
                        BmobRelation relation = new BmobRelation();
                        relation.add(comment);
                        diary.setComment(relation);
                        diary.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    RairUtils.showSnackar(findEtCommentsContent, "评论成功");
                                } else {
                                    RairUtils.showSnackar(findEtCommentsContent, "评论失败");
                                }
                            }
                        });
                    } else {
                        RairUtils.showSnackar(findEtCommentsContent, "评论失败");
                    }
                }
            });
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
