package com.example.bishe.cet4.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.example.bishe.cet4.object.Word;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;

/**
 * Created by Skywilling on 2018/1/3.
 */

public class DBHelper {
    private SQLiteDatabase db=null;

    public DBHelper(SQLiteDatabase db){
        this.db=db;
    }

    public  List<Word> selectWordById(int id){
        List<Word> list=new ArrayList<Word>();
        Cursor cursor=db.rawQuery("select * from words where id= ? ",new String[]{String.valueOf(id)});
        while(cursor.moveToNext()){
            list.add(new Word(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4)));
        }
        cursor.close();
        return list;
    }

    public String selectEnglishById(int id){
        Cursor cursor=db.rawQuery("select english from words where id= ? ",new String[]{String.valueOf(id)});
        String res;
        if(cursor.moveToNext()){
            res=cursor.getString(0);
        }else{
            res=null;
        }
        cursor.close();
        return res;
    }

    public String selectChineseById(int id){
        Cursor cursor=db.rawQuery("select num from test_question where id=?",new String[]{String.valueOf(id)});
        int num;
        if(cursor.moveToNext()){
            num=cursor.getInt(0);
        }else{
            num=0;
        }
        Random random=new Random();
        int random_num=random.nextInt(num);
        cursor=db.rawQuery("select answer"+(random_num+1)+" from test_question where id=?",new String[]{String.valueOf(id)});
        String res;
        if(cursor.moveToNext()){
            res=cursor.getString(0);
        }else{
            res=null;
        }
        cursor.close();
        return res;
    }

    public String selectedByTime(String time){
        Cursor cursor=db.rawQuery("select words_num from words_plan where time=?",new String[]{time});
        String result_str;
        if(cursor.moveToNext()){
            result_str=cursor.getString(0);
        }else{
            result_str=null;
        }
        cursor.close();
        return result_str;
    }

    public int selectCount(){
        Cursor cursor=db.rawQuery("select count(*) from words_plan",null);
        int count;
        if(cursor.moveToNext()){
            count=cursor.getInt(0);
        }else{
            count=-1;
        }
        cursor.close();
        return count;
    }

    public String select_words_plan_byId(int id){
        Cursor cursor=db.rawQuery("select words_num from words_plan where id=?",new String[]{String.valueOf(id)});
        String res;
        if(cursor.moveToNext()){
            res=cursor.getString(0);
        }else{
            res=null;
        }
        cursor.close();
        return res;
    }

    public String selectAllLearnWordNum(){
        String words_num_str="";
        Cursor cursor=db.rawQuery("select words_num from words_plan",null);
        while (cursor.moveToNext()){
            words_num_str+=cursor.getString(0);
        }
        cursor.close();
        return words_num_str;
    }

    public List<String> selectAllLearnWord(){
        List<String> res=new ArrayList<String>();
        Cursor cursor=db.rawQuery("select words_num from words_plan",null);
        while (cursor.moveToNext()){
            res.add(cursor.getString(0));
        }
        cursor.close();
        return res;
    }

    public void insertPlanWords(String time,String res_str){
        db.execSQL("insert into words_plan(time,words_num) values(?,?)",new Object[]{time,res_str});
    }

    public List getSuggestWords(String word){
        db.execSQL("PRAGMA case_sensitive_like = 1");//解决like大小写不敏感
        Cursor cursor=db.rawQuery("select english,chinese from dict_"+word.charAt(0)+" where english like '"+word+"%' limit 20",null);
        List<Map<String,String>> list=new ArrayList<Map<String,String>>();
        while(cursor.moveToNext()){
            Map<String,String> map=new HashMap<String,String>();
            map.put("english",cursor.getString(0));
            map.put("chinese",cursor.getString(1));
            list.add(map);
        }
        cursor.close();
        return list;
    }
}
