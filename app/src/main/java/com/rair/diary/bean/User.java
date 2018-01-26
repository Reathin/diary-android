package com.rair.diary.bean;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by mzaiy on 2017/6/9.
 */

public class User extends BmobUser {

    //头像
    private BmobFile headFile;
    //个性签名
    private String sign;
    //昵称
    private String nickName;
    //性别
    private Integer sex;
    //数据
    private BmobFile dbFile;
    //数据下载链接
    private String dbUrl;
    //粉丝
    private BmobRelation fans;
    //关注
    private BmobRelation focus;
    //评论
    private BmobRelation comment;
    //日记
    private BmobRelation diary;
    //是否Vip
    private Boolean isVip;

    public BmobFile getHeadFile() {
        return headFile;
    }

    public void setHeadFile(BmobFile headFile) {
        this.headFile = headFile;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public BmobFile getDbFile() {
        return dbFile;
    }

    public void setDbFile(BmobFile dbFile) {
        this.dbFile = dbFile;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public BmobRelation getFans() {
        return fans;
    }

    public void setFans(BmobRelation fans) {
        this.fans = fans;
    }

    public BmobRelation getFocus() {
        return focus;
    }

    public void setFocus(BmobRelation focus) {
        this.focus = focus;
    }

    public BmobRelation getComment() {
        return comment;
    }

    public void setComment(BmobRelation comment) {
        this.comment = comment;
    }

    public BmobRelation getDiary() {
        return diary;
    }

    public void setDiary(BmobRelation diary) {
        this.diary = diary;
    }

    public Boolean getVip() {
        return isVip;
    }

    public void setVip(Boolean vip) {
        isVip = vip;
    }
}
