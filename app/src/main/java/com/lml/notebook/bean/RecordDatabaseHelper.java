package com.lml.notebook.bean;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
/*
*记账页
 */
//SQLiteOpenHelper类
public class RecordDatabaseHelper extends SQLiteOpenHelper {

    private String TAG = "RecordDatabaseHelper";//方便调试

    public static final String DB_NAME = "Record";

    private static final String CREATE_RECORD_DB = "create table Record ("
            + "id integer primary key autoincrement, "
            + "name text, "
            + "uuid text, "
            + "type integer, "
            + "category, "  //消费类型
            + "remark text, " //备注
            + "amount double, "//金额
            + "time integer, "//时间戳
            + "date date )";

    public RecordDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_RECORD_DB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addRecord(RecordBean bean) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", bean.getName());
        values.put("uuid", bean.getUuid());
        values.put("type", bean.getType());
        values.put("category", bean.getCategory());
        values.put("remark", bean.getRemark());
        values.put("amount", bean.getAmount());
        values.put("date", bean.getDate());
        values.put("time", bean.getTimeStamp());
        db.insert(DB_NAME, null, values);
        values.clear();
        Log.d(TAG, bean.getUuid() + "added");
    }

    public void removeRecord(String uuid,String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DB_NAME, "uuid = ? AND name = ?", new String[]{uuid,name});
    }

    public void editRecord(String uuid, RecordBean record,String name) {
        removeRecord(uuid,name);
        record.setUuid(uuid);
        addRecord(record);
    }

    public LinkedList<RecordBean> readRecords(String dateStr,String name) {
        LinkedList<RecordBean> records = new LinkedList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select DISTINCT * from Record where date = ? And name = ? order by time asc", new String[]{dateStr,name});
        if (cursor.moveToFirst()) {//确保查询有效
            do {
                String names = cursor.getString(cursor.getColumnIndex("name"));
                String uuid = cursor.getString(cursor.getColumnIndex("uuid"));
                int type = cursor.getInt(cursor.getColumnIndex("type"));
                String category = cursor.getString(cursor.getColumnIndex("category"));
                String remark = cursor.getString(cursor.getColumnIndex("remark"));
                double amount = cursor.getDouble(cursor.getColumnIndex("amount"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                long timeStamp = cursor.getLong(cursor.getColumnIndex("time"));

                RecordBean record = new RecordBean();
                record.setName(names);
                record.setUuid(uuid);
                record.setType(type);
                record.setCategory(category);
                record.setRemark(remark);
                record.setAmount(amount);
                record.setDate(date);
                record.setTimeStamp(timeStamp);

                records.add(record);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return records;//请求完成
    }

    public LinkedList<String> getAvaliableDate() {

        LinkedList<String> dates = new LinkedList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select DISTINCT * from Record  order by date asc", new String[]{});
        if (cursor.moveToFirst()) {
            do {
                String date = cursor.getString(cursor.getColumnIndex("date"));//取数据
                if (!dates.contains(date)) {
                    dates.add(date);//数组之和
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return dates;
    }
}
