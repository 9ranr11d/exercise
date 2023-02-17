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
        String sqlExercise = "CREATE TABLE if not exists EXERCISE_TB ("
                + "SEQ integer primary key,"
                + "DATE text, TYPE text, NAME text, SET_NUM integer, VOLUME text, NUMBER text);";
        db.execSQL(sqlExercise);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sqlExercise = "DROP TABLE if exists EXERCISE_TB;";
        db.execSQL(sqlExercise);

        onCreate(db);
    }
    //insert
    public boolean dataInsert(int seq, String date, String type, String name, int setNum, String volume, String number) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("SEQ", seq);
        values.put("DATE", date);
        values.put("TYPE", type);
        values.put("NAME", name);
        values.put("SET_NUM", setNum);
        values.put("VOLUME", volume);
        values.put("NUMBER",number);

        long result = db.insertOrThrow("EXERCISE_TB", null, values);
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
        values.put("DATE", date);
        values.put("TYPE", type);
        values.put("NAME", name);
        values.put("SET_NUM", setNum);
        values.put("VOLUME", volume);
        values.put("NUMBER", number);

        long result = db.update("EXERCISE_TB", values, "SEQ = ?", new String[] {seq});
        if(result == -1) {
            return false;
        }
        db.close();

        return true;
    }
    //delete
    public boolean dataDelete(String seq) {
        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.delete("EXERCISE_TB", "SEQ = ?", new String[] {seq});
        if(result == -1) {
            return false;
        }
        db.close();

        return true;
    }
}
