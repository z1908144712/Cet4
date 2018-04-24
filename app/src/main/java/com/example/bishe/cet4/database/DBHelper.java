package com.example.bishe.cet4.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.example.bishe.cet4.object.Word;
import com.example.bishe.cet4.object.WordCollection;

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
    /**
     * words
     **/
    public int selectCountFromWords(){
        Cursor cursor=db.rawQuery("select count(*) from words",null);
        int res_count=0;
        if(cursor.moveToNext()){
            res_count=cursor.getInt(0);
        }
        cursor.close();
        return res_count;
    }

    public  Word selectWordById(String id){
        Word word=null;
        Cursor cursor=db.rawQuery("select * from words where id= ? ",new String[]{id});
        if(cursor.moveToNext()){
            word=new Word(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4));
        }
        cursor.close();
        return word;
    }

    public String selectEnglishById(int id){
        Cursor cursor=db.rawQuery("select english from words where id= ? ",new String[]{String.valueOf(id)});
        String english;
        if(cursor.moveToNext()){
            english=cursor.getString(0);
        }else{
            english=null;
        }
        cursor.close();
        return english;
    }

    /**
     * test_question
     * */
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

    public int getIdByEnglish_Test_Question(String english){
        Cursor cursor=db.rawQuery("select id from test_question where question=?",new String[]{english});
        int id;
        if(cursor.moveToNext()){
            id=cursor.getInt(0);
        }else{
            id=-1;
        }
        cursor.close();
        return id;
    }
    /**
     * words_plan
     **/
    public String selectWordPlanByTime(String time){
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

    public String selectLearnTimeFromWordPlanByTime(String time){
        Cursor cursor=db.rawQuery("select learn_time from words_plan where time=?",new String[]{time});
        String learn_time;
        if(cursor.moveToNext()){
            learn_time=cursor.getString(0);
        }else{
            learn_time=null;
        }
        cursor.close();
        return learn_time;
    }

    public int selectCountToday(String time){
        String learn_word=selectWordPlanByTime(time);
        return learn_word.split(",").length;
    }

    public int selectCountFromWordsPlan(){
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

    public int select_learned_words_num_except_today(){
        Cursor cursor=db.rawQuery("select words_num from words_plan",null);
        int last_words_num=selectCountFromWords();
        List<String> learned_words=new ArrayList<>();
        while(cursor.moveToNext()){
            learned_words.add(cursor.getString(0));
        }
        for(int i=0;i<learned_words.size()-1;i++){
            last_words_num-=learned_words.get(i).split(",").length;
        }
        return last_words_num;
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
        db.execSQL("insert into words_plan(time,words_num,learn_time) values(?,?,?)",new Object[]{time,res_str,"0:0:0"});
    }

    public void updateTimePlanWords(String time,String learn_time){
        db.execSQL("update words_plan set learn_time=? where time=?",new Object[]{learn_time,time});
    }

    /**
     * dict_?
     * **/
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
    /**
     * word_collection
     * **/

    public int getWrongNumWordCollection(String english){
        Cursor cursor=db.rawQuery("select wrong_num from word_collection where english=?",new String[]{english});
        int wrong_num=0;
        if(cursor.moveToNext()){
            wrong_num=cursor.getInt(0);
        }else{
            wrong_num=-1;
        }
        cursor.close();
        return wrong_num;
    }

    public boolean isExistWordCollection(String english){
        Cursor cursor=db.rawQuery("select * from word_collection where english=?",new String[]{english});
        boolean isExist=false;
        if(cursor.moveToNext()){
            isExist=true;
        }else{
            isExist=false;
        }
        cursor.close();
        return isExist;
    }

    public void addWrongNumWordCollection(String english){
        if(isExistWordCollection(english)){
            int wrong_num=getWrongNumWordCollection(english);
            wrong_num++;
            db.execSQL("update word_collection set wrong_num=? where english=?",new Object[]{wrong_num,english});
        }else{
            insertIntoWordCollection(english);
        }

    }

    public void decWrongNumWordCollection(String english){
        int wrong_num=getWrongNumWordCollection(english);
        wrong_num--;
        if(wrong_num==0){
            db.execSQL("delete from word_collection where english=?",new Object[]{english});
        }else{
            db.execSQL("update word_collection set wrong_num=? where english=?",new Object[]{wrong_num,english});
        }
    }

    public void insertIntoWordCollection(String english){
        db.execSQL("insert into word_collection(english,wrong_num) values(?,?)",new Object[]{english,1});
    }

    public List<WordCollection> getAll_Word_WordCollection(){
        Cursor cursor=db.rawQuery("select english,wrong_num from word_collection",null);
        List<WordCollection> wordList=new ArrayList<>();
        while(cursor.moveToNext()){
            wordList.add(new WordCollection(cursor.getString(0),cursor.getInt(1)));
        }
        cursor.close();
        return wordList;
    }
}
