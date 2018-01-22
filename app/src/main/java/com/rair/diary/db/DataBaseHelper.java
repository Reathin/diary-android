package com.rair.diary.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.rair.diary.constant.Constants;

public class DataBaseHelper extends SQLiteOpenHelper {

    public DataBaseHelper(Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
    }

    public DataBaseHelper(Context context, int version) {
        super(context, Constants.DB_NAME, null, version);
    }

    /**
     * 该函数是子啊第一次创建数据库的时候执行，实际上是第一次
     * 得到SQLiteDatabase对象的时候才会被调用
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table DIARY (date TEXT,week TEXT,weather TEXT,title TEXT,content TEXT,image TEXT,id INTEGER PRIMARY KEY AUTOINCREMENT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (newVersion) {
            case 2:
                db.execSQL("ALTER TABLE DIARY ADD image TEXT");
                break;
            default:
                db.execSQL("DROP TABLE IF EXISTS DIARY");
                onCreate(db);
                break;
        }
    }
}