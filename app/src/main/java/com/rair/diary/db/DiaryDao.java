package com.rair.diary.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rair.diary.bean.DiaryBean;

import java.util.ArrayList;
import java.util.List;

public class DiaryDao {

    private DataBaseHelper helper;

    public DiaryDao(Context context) {
        helper = new DataBaseHelper(context);
    }

    /**
     * 插入数据
     *
     * @param diary
     */
    public void insert(DiaryBean diary) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("date", diary.getDate());
        values.put("week", diary.getWeek());
        values.put("weather", diary.getWeather());
        values.put("title", diary.getTitle());
        values.put("content", diary.getContent());
        values.put("image", diary.getImage());
        db.insert("DIARY", null, values);
        db.close();
    }

    /**
     * 根据id删除
     *
     * @param id
     */
    public void delete(long id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from DIARY where id = " + id);
        db.close();
    }

    /**
     * 更新
     *
     * @param title   标题
     * @param content 内容
     * @param id      id
     */
    public void update(String title, String content, long id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("update DIARY set title = \" " + title + " \", content = \" " + content + "\" where id = " + id);
        db.close();
    }

    /**
     * 查询
     *
     * @param diaries
     */
    public void query(ArrayList<DiaryBean> diaries) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("DIARY", null, null, null, null, null, "id desc");
        diaries.clear();
        while (cursor.moveToNext()) {
            DiaryBean diary = new DiaryBean();
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            String image = cursor.getString(cursor.getColumnIndex("image"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            String week = cursor.getString(cursor.getColumnIndex("week"));
            String weather = cursor.getString(cursor.getColumnIndex("weather"));
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            diary.setDate(date);
            diary.setWeek(week);
            diary.setWeather(weather);
            diary.setTitle(title);
            diary.setContent(content);
            diary.setImage(image);
            diary.setId(id);
            diaries.add(diary);
        }
        cursor.close();
        db.close();
    }

    public void deleteAll() {
        String delete_sql = "delete from DIARY";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(delete_sql);
        db.close();
    }

    public void dimSearch(String query, List<DiaryBean> diaries) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("DIARY", null, "content like '%" + query + "%' " + " OR title like '%" + query + "%'", null, null, null, "id desc");
        diaries.clear();
        while (cursor.moveToNext()) {
            DiaryBean diary = new DiaryBean();
            String date = cursor.getString(cursor.getColumnIndex("date"));
            String week = cursor.getString(cursor.getColumnIndex("week"));
            String weather = cursor.getString(cursor.getColumnIndex("weather"));
            String image = cursor.getString(cursor.getColumnIndex("image"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            diary.setDate(date);
            diary.setWeek(week);
            diary.setWeather(weather);
            diary.setImage(image);
            diary.setContent(content);
            diary.setTitle(title);
            diary.setId(id);
            diaries.add(diary);
        }
        cursor.close();
        db.close();
    }
}
