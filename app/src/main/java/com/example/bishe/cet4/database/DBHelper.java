package com.example.bishe.cet4.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.bishe.cet4.object.Word;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Skywilling on 2018/1/3.
 */

public class DBHelper {
    private SQLiteDatabase db;

    public DBHelper(SQLiteDatabase db){
        this.db=db;
    }

    public  List<Word> selectById(SQLiteDatabase db, int id){
        List<Word> list=new ArrayList<Word>();
        Cursor cursor=db.rawQuery("select * from words where id= ? ",new String[]{String.valueOf(id)});
        while(cursor.moveToNext()){
            list.add(new Word(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4)));
        }
        cursor.close();
        return list;
    }
}
