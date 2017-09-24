package com.rair.diary.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Rair on 2017/6/12.
 * Email:rairmmd@gmail.com
 * Author:Rair
 */
public class Comment extends BmobObject {

    //评论内容
    private String content;
    //用户
    private User user;
    //评论
    private Diary diary;

    //评论时间
    private  String commentTime;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Diary getDiary() {
        return diary;
    }

    public void setDiary(Diary diary) {
        this.diary = diary;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }
}
