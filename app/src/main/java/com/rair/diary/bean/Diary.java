package com.rair.diary.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by Rair on 2017/6/12.
 * Email:rairmmd@gmail.com
 * Author:Rair
 */
public class Diary extends BmobObject {

    //发布者
    private User user;
    //昵称
    private String name;
    //标题
    private String title;
    //内容
    private String content;
    //图片
    private BmobFile image;
    //天气
    private String weather;
    //日期
    private String date;
    //星期
    private String week;
    //评论
    private BmobRelation comment;
    //发布时间
    private String createTime;
    //点赞总数
    private Integer likes;
    //点赞的人
    private BmobRelation likers;
    //评论总数
    private Integer comments;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public BmobFile getImage() {
        return image;
    }

    public void setImage(BmobFile image) {
        this.image = image;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public BmobRelation getComment() {
        return comment;
    }

    public void setComment(BmobRelation comment) {
        this.comment = comment;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public BmobRelation getLikers() {
        return likers;
    }

    public void setLikers(BmobRelation likers) {
        this.likers = likers;
    }

    public Integer getComments() {
        return comments;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
    }
}
