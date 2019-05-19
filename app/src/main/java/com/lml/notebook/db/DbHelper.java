package com.lml.notebook.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.lml.notebook.bean.UserBean;

import java.util.ArrayList;
import java.util.List;


/**
 * 数据库工具类
 */
public class DbHelper extends SQLiteOpenHelper {

    public DbHelper(Context ctx) {
        super(ctx, "BookManageSystem", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //用户表
        db.execSQL("CREATE TABLE if not exists user(id integer PRIMARY KEY autoincrement,"
                + " nickName text, password text, url text )");
        //数据表
        db.execSQL("CREATE TABLE if not exists data(id integer PRIMARY KEY autoincrement,"
                + " name text,title text, description text, priority text,path text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private static DbHelper dbManager;


    public static DbHelper getInstance(Context ctx) {
        if (dbManager == null) {
            synchronized (DbHelper.class) {
                if (dbManager == null) {
                    dbManager = new DbHelper(ctx);
                }
            }
        }
        return dbManager;
    }

    public boolean saveUser(UserBean bean) {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            //注册之前先查询是否重复注册
            Cursor cursor = db.rawQuery("SELECT * FROM user WHERE nickName = ?", new String[]{bean.getNickName()});
            boolean hasUser = false;
            if (cursor.moveToNext()) {
                hasUser = true;
            }
            cursor.close();
            if (hasUser) {
                return true;
            }
            //如果不重复则注册
            db.execSQL("INSERT INTO user(nickName , password , url  ) " +
                    "VALUES ('" + bean.getNickName()
                    + "', '" + bean.getPassword()

                    + "', '" + bean.getUrl()
                    + "')");
        }
        return false;
    }




    /**
     * 更新
     *
     * @param record
     */
    public void updateUser(UserBean record) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", record.getId());
        contentValues.put("nickName", record.getNickName());

        contentValues.put("password", record.getPassword());

        contentValues.put("url", record.getUrl());
        if (db != null) {
            db.update("user", contentValues, "id = ?", new String[]{record.getId()+""});
        }
    }

    /**
     * 查找用户（登录操作）
     *

     * @return 用户
     */
    public UserBean findUser(String[] args) {
        UserBean bean = new UserBean();
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            Cursor cursor = db.query("user", null, "nickName = ?", args, null, null, null);
            if (cursor.moveToNext()) {
                bean.setId(cursor.getInt(cursor.getColumnIndex("id")));
                bean.setNickName(cursor.getString(cursor.getColumnIndex("nickName")));
                bean.setPassword(cursor.getString(cursor.getColumnIndex("password")));
                bean.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            }
            cursor.close();

        }
        return bean;
    }



    public boolean saveNote(Note bean) {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            //注册之前先查询是否重复注册
            Cursor cursor = db.rawQuery("SELECT * FROM data WHERE title = ?", new String[]{bean.getTitle()});
            boolean hasUser = false;
            if (cursor.moveToNext()) {
                hasUser = true;
            }
            cursor.close();
            if (hasUser) {
                return true;
            }
            //如果不重复则注册
            db.execSQL("INSERT INTO data(name,title , description , priority ,path ) " +
                    "VALUES ('" + bean.getName()
                    + "', '" + bean.getTitle()
                    + "', '" + bean.getDescription()
                    + "', '" + bean.getPriority()
                    + "', '" + bean.getPath()
                    + "')");
        }
        return false;
    }





    /**
     * 查找（登录操作）
     *便签页

     * @return
     */
    public Note findNote(String[] args) {
        Note bean = new Note();
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            Cursor cursor = db.query("data", null, "name = ?", args, null, null, null);
            if (cursor.moveToNext()) {
                bean.setId(cursor.getInt(cursor.getColumnIndex("id")));
                bean.setName(cursor.getString(cursor.getColumnIndex("name")));
                bean.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                bean.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                bean.setPriority(cursor.getInt(cursor.getColumnIndex("priority")));
                bean.setPath(cursor.getString(cursor.getColumnIndex("path")));

            }
            cursor.close();

        }
        return bean;
    }

    /**
     * 获取记录数据
     *
     * @return
     */
    public List<Note> getAllHeart(String name) {
        List<Note> records = new ArrayList<Note>();
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            //查询记录
            Cursor cursor = db.rawQuery("SELECT * FROM data WHERE name = ?", new String[]{name});
            while (cursor.moveToNext()) {
                Note bean = new Note();
                bean.setId(cursor.getInt(cursor.getColumnIndex("id")));
                bean.setName(cursor.getString(cursor.getColumnIndex("name")));
                bean.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                bean.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                bean.setPriority(cursor.getInt(cursor.getColumnIndex("priority")));
                bean.setPath(cursor.getString(cursor.getColumnIndex("path")));
                records.add(bean);
            }
            cursor.close();
        }
        return records;
    }


    /**
     * 删除
     *
     * @param id
     */
    public void deleteRecord(int id) {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            db.execSQL("DELETE FROM data WHERE id = " + id);
        }
    }

    /**
     * 删除全部数据
     *
     */
    public void deleteAll( ) {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            db.execSQL("DELETE FROM data");
        }
    }
}
