package com.example.exercise;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
//SQLite 테이블 생성 및 관리
public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    //SQLite 테이블 생성
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlExercise = "CREATE TABLE if not exists exercise ("
                + "seq integer primary key,"
                + "e_date text, e_type text, e_name text, set_n integer, e_volume text, e_Number text);";
        db.execSQL(sqlExercise);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sqlExercise = "DROP TABLE if exists exercise;";
        db.execSQL(sqlExercise);

        onCreate(db);
    }
    //insert
    public boolean dataInsert(int seq, String date, String type, String name, int setNum, String volume, String number) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("seq", seq);
        values.put("e_date", date);
        values.put("e_type", type);
        values.put("e_name", name);
        values.put("set_n", String.valueOf(setNum));
        values.put("e_volume", volume);
        values.put("e_number",number);

        long result = db.insertOrThrow("exercise", null, values);
        if(result == -1) {
            return false;
        }
        db.close();

        return true;
    }
    //update
    public boolean dataUpdate(String seq, String date, String type, String name, int setNum, String volume, String number) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("e_date", date);
        values.put("e_type", type);
        values.put("e_name", name);
        values.put("set_n", setNum);
        values.put("e_volume", volume);
        values.put("e_number", number);

        long result = db.update("exercise", values, "seq = ?", new String[] {seq});
        if(result == -1) {
            return false;
        }
        db.close();

        return true;
    }
    //delete
    public boolean dataDelete(String seq) {
        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.delete("exercise", "seq = ?", new String[] {seq});
        if(result == -1) {
            return false;
        }
        db.close();

        return true;
    }
}
