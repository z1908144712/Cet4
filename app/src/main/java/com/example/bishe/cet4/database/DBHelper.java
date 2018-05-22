package com.example.bishe.cet4.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.example.bishe.cet4.object.UserData;
import com.example.bishe.cet4.object.Word;
import com.example.bishe.cet4.object.WordCollection;
import com.example.bishe.cet4.object.WordPlan;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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
    public List<Word> selectAllWords(){
        Cursor cursor=db.rawQuery("select * from words",null);
        List<Word> words=new ArrayList<>();
        while(cursor.moveToNext()){
            words.add(new Word(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6)));
        }
        cursor.close();
        return words;
    }

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
            word=new Word(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6));
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

    public int selectIdByEnglish(String english){
        Cursor cursor=db.rawQuery("select id from words where english=?",new String[]{english});
        int id;
        if(cursor.moveToNext()){
            id=cursor.getInt(0);
        }else{
            id=-1;
        }
        cursor.close();
        return id;
    }

    public void updateWord(Word word){
       Word word1=selectWordById(word.getId().toString());
       if(!word1.getChinese().equals(word.getChinese())){
           db.execSQL("update words set chinese=? where id=?",new Object[]{word.getChinese(),word.getId()});
       }
       if(!word1.getEnglish().equals(word.getEnglish())){
           db.execSQL("update words set english=? where id=?",new Object[]{word.getEnglish(),word.getId()});
       }
       if(!word1.getPhonetic().equals(word.getPhonetic())){
           db.execSQL("update words set phonetic=? where id=?",new Object[]{word.getPhonetic(),word.getId()});
       }
       if(!word1.getMain_chinese().equals(word.getMain_chinese())){
           db.execSQL("update words set main_chinese=? where id=?",new Object[]{word.getMain_chinese(),word.getId()});
       }
        if(!word1.getUk_phonetic().equals(word.getUk_phonetic())){
            db.execSQL("update words set uk_phonetic=? where id=?",new Object[]{word.getUk_phonetic(),word.getId()});
        }
        if(!word1.getUs_phonetic().equals(word.getUs_phonetic())){
            db.execSQL("update words set us_phonetic=? where id=?",new Object[]{word.getUs_phonetic(),word.getId()});
        }
    }

    public void insertWord(Word word){
        Word word1=selectWordById(String.valueOf(word.getId()));
        if(word1==null){
            db.execSQL("insert into words(id,english,chinese,phonetic,main_chinese,us_phonetic,uk_phonetic) values(?,?,?,?,?,?,?)",new Object[]{word.getId(),word.getEnglish(),word.getChinese(),word.getPhonetic(),word.getMain_chinese(),word.getUs_phonetic(),word.getUk_phonetic()});
        }else{
            updateWord(word);
        }
    }

    /**
     * test_question
     * */

    public int selectCountFromTestQuestion(){
        int num;
        Cursor cursor=db.rawQuery("select count(*) from test_question",null);
        if(cursor.moveToNext()){
            num=cursor.getInt(0);
        }else {
            num=0;
        }
        cursor.close();
        return num;
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

    public void updateTestQuestion(Word word){
        String english=word.getEnglish();
        String answers[]=word.getChinese().split("#");
        int num=answers.length;
        String old_answers[]=null;
        int old_num;
        Cursor cursor=db.rawQuery("select * from test_question where question=?",new String[]{english});
        if (cursor.moveToNext()){
            old_num=cursor.getInt(2);
            old_answers=new String[old_num];
            for(int i=0;i<old_num;i++){
                old_answers[i]=cursor.getString(3+i);
            }
        }else {
            old_num=0;
        }
        cursor.close();
//        System.out.println("old_num="+old_num+" num"+num);
        if(old_answers!=null&&old_num!=0){
            if(old_num>num){
                for(int i=0;i<num;i++){
                    if(!old_answers[i].equals(answers[i])){
                        switch (i){
                            case 0:
                                db.execSQL("update test_question set answer1=? where question=?",new Object[]{answers[i],english});
                                break;
                            case 1:
                                db.execSQL("update test_question set answer2=? where question=?",new Object[]{answers[i],english});
                                break;
                            case 2:
                                db.execSQL("update test_question set answer3=? where question=?",new Object[]{answers[i],english});
                                break;
                            case 3:
                                db.execSQL("update test_question set answer4=? where question=?",new Object[]{answers[i],english});
                                break;
                            case 4:
                                db.execSQL("update test_question set answer5=? where question=?",new Object[]{answers[i],english});
                                break;
                            case 5:
                                db.execSQL("update test_question set answer6=? where question=?",new Object[]{answers[i],english});
                                break;
                        }
                    }
                }
                for(int i=num-1;i<old_num;i++){
                    switch (i){
                        case 0:
                            db.execSQL("update test_question set answer1=? where question=?",new Object[]{null,english});
                            break;
                        case 1:
                            db.execSQL("update test_question set answer2=? where question=?",new Object[]{null,english});
                            break;
                        case 2:
                            db.execSQL("update test_question set answer3=? where question=?",new Object[]{null,english});
                            break;
                        case 3:
                            db.execSQL("update test_question set answer4=? where question=?",new Object[]{null,english});
                            break;
                        case 4:
                            db.execSQL("update test_question set answer5=? where question=?",new Object[]{null,english});
                            break;
                        case 5:
                            db.execSQL("update test_question set answer6=? where question=?",new Object[]{null,english});
                            break;
                    }
                }
            }else if(old_num==num){
                for(int i=0;i<num;i++){
                    if(!old_answers[i].equals(answers[i])){
                        switch (i){
                            case 0:
                                db.execSQL("update test_question set answer1=? where question=?",new Object[]{answers[i],english});
                                break;
                            case 1:
                                db.execSQL("update test_question set answer2=? where question=?",new Object[]{answers[i],english});
                                break;
                            case 2:
                                db.execSQL("update test_question set answer3=? where question=?",new Object[]{answers[i],english});
                                break;
                            case 3:
                                db.execSQL("update test_question set answer4=? where question=?",new Object[]{answers[i],english});
                                break;
                            case 4:
                                db.execSQL("update test_question set answer5=? where question=?",new Object[]{answers[i],english});
                                break;
                            case 5:
                                db.execSQL("update test_question set answer6=? where question=?",new Object[]{answers[i],english});
                                break;
                        }
                    }
                }
            }else{
                for(int i=0;i<old_num;i++){
                    if(!old_answers[i].equals(answers[i])){
                        switch (i){
                            case 0:
                                db.execSQL("update test_question set answer1=? where question=?",new Object[]{answers[i],english});
                                break;
                            case 1:
                                db.execSQL("update test_question set answer2=? where question=?",new Object[]{answers[i],english});
                                break;
                            case 2:
                                db.execSQL("update test_question set answer3=? where question=?",new Object[]{answers[i],english});
                                break;
                            case 3:
                                db.execSQL("update test_question set answer4=? where question=?",new Object[]{answers[i],english});
                                break;
                            case 4:
                                db.execSQL("update test_question set answer5=? where question=?",new Object[]{answers[i],english});
                                break;
                            case 5:
                                db.execSQL("update test_question set answer6=? where question=?",new Object[]{answers[i],english});
                                break;
                        }
                    }
                }
                for(int i=old_num-1;i<num;i++){
                    switch (i){
                        case 0:
                            db.execSQL("update test_question set answer1=? where question=?",new Object[]{answers[i],english});
                            break;
                        case 1:
                            db.execSQL("update test_question set answer2=? where question=?",new Object[]{answers[i],english});
                            break;
                        case 2:
                            db.execSQL("update test_question set answer3=? where question=?",new Object[]{answers[i],english});
                            break;
                        case 3:
                            db.execSQL("update test_question set answer4=? where question=?",new Object[]{answers[i],english});
                            break;
                        case 4:
                            db.execSQL("update test_question set answer5=? where question=?",new Object[]{answers[i],english});
                            break;
                        case 5:
                            db.execSQL("update test_question set answer6=? where question=?",new Object[]{answers[i],english});
                            break;
                    }
                }
            }
        }
    }

    public void insertTestQuestion(Word word){
        String english=word.getEnglish();
        String answers[]=word.getChinese().split("#");
        int num=answers.length;
        int id=selectCountFromTestQuestion();
        switch (num){
            case 0:
                db.execSQL("insert into test_question(id,question,num,answer1,answer2,answer3,answer4,answer5,answer6) values(?,?,?,?,?,?,?,?,?)",new Object[]{id,english,num,null,null,null,null,null,null});
                break;
            case 1:
                db.execSQL("insert into test_question(id,question,num,answer1,answer2,answer3,answer4,answer5,answer6) values(?,?,?,?,?,?,?,?,?)",new Object[]{id,english,num,answers[0],null,null,null,null,null});
                break;
            case 2:
                db.execSQL("insert into test_question(id,question,num,answer1,answer2,answer3,answer4,answer5,answer6) values(?,?,?,?,?,?,?,?,?)",new Object[]{id,english,num,answers[0],answers[1],null,null,null,null});
                break;
            case 3:
                db.execSQL("insert into test_question(id,question,num,answer1,answer2,answer3,answer4,answer5,answer6) values(?,?,?,?,?,?,?,?,?)",new Object[]{id,english,num,answers[0],answers[1],answers[2],null,null,null});
                break;
            case 4:
                db.execSQL("insert into test_question(id,question,num,answer1,answer2,answer3,answer4,answer5,answer6) values(?,?,?,?,?,?,?,?,?)",new Object[]{id,english,num,answers[0],answers[1],answers[2],answers[3],null,null});
                break;
            case 5:
                db.execSQL("insert into test_question(id,question,num,answer1,answer2,answer3,answer4,answer5,answer6) values(?,?,?,?,?,?,?,?,?)",new Object[]{id,english,num,answers[0],answers[1],answers[2],answers[3],answers[4],null});
                break;
            case 6:
                db.execSQL("insert into test_question(id,question,num,answer1,answer2,answer3,answer4,answer5,answer6) values(?,?,?,?,?,?,?,?,?)",new Object[]{id,english,num,answers[0],answers[1],answers[2],answers[3],answers[4],answers[5]});
                break;
        }

    }
    /**
     * words_plan
     **/
    public List<WordPlan> selectAllWordPlan(){
        List<WordPlan> wordPlans=new ArrayList<>();
        Cursor cursor=db.rawQuery("select time,words_num,learn_time from words_plan",null);
        while (cursor.moveToNext()){
            WordPlan wordPlan=new WordPlan();
            wordPlan.setTime(cursor.getString(0));
            wordPlan.setWords_num(cursor.getString(1));
            wordPlan.setLearn_time(cursor.getString(2));
            wordPlans.add(wordPlan);
        }
        cursor.close();
        return wordPlans;
    }

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

    public int select_learned_words_num(){
        Cursor cursor=db.rawQuery("select words_num from words_plan",null);
        int count=0;
        while(cursor.moveToNext()){
            count+=cursor.getString(0).split(",").length;
        }
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

    public String select_time_from_words_plan_by_id(int id){
        Cursor cursor=db.rawQuery("select time from words_plan where id=?",new String[]{String.valueOf(id)});
        String time;
        if(cursor.moveToNext()){
            time=cursor.getString(0);
        }else{
            time=null;
        }
        cursor.close();
        return time;
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

    public List<String> selectAllLearnedTime(){
        List<String> res=new ArrayList<>();
        Cursor cursor=db.rawQuery("select learn_time from words_plan",null);
        while(cursor.moveToNext()){
            res.add(cursor.getString(0));
        }
        cursor.close();
        return res;
    }

    public void insertPlanWords(String time,String res_str){
        db.execSQL("insert into words_plan(time,words_num,learn_time) values(?,?,?)",new Object[]{time,res_str,"0:0:0"});
    }

    public void insertPlanWords(WordPlan wordPlan){
        db.execSQL("insert into words_plan(time,words_num,learn_time) values(?,?,?)",new Object[]{wordPlan.getTime(),wordPlan.getWords_num(),wordPlan.getLearn_time()});
    }

    public void updateTimePlanWords(String time,String learn_time){
        db.execSQL("update words_plan set learn_time=? where time=?",new Object[]{learn_time,time});
    }

    public void updateAll_WordPlan(List wordPlans){
        db.execSQL("delete from words_plan");
        db.execSQL("delete from sqlite_sequence where name='words_plan'");
        for(int i=0;i<wordPlans.size();i++){
            Gson gson=new Gson();
            WordPlan wordPlan=gson.fromJson(gson.toJson(wordPlans.get(i)),WordPlan.class);
            insertPlanWords(wordPlan);
        }
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
        boolean isExist;
        if(cursor.moveToNext()){
            isExist=true;
        }else{
            isExist=false;
        }
        cursor.close();
        return isExist;
    }

    public void addWrongNumWordCollection(int id,String english,int belongto){
        if(isExistWordCollection(english)){
            int wrong_num=getWrongNumWordCollection(english);
            wrong_num++;
            db.execSQL("update word_collection set wrong_num=? where english=?",new Object[]{wrong_num,english});
        }else{
            insertIntoWordCollection(id,english,belongto);
        }

    }

    public void decWrongNumWordCollection(String english){
        int wrong_num=getWrongNumWordCollection(english);
        wrong_num--;
        if(wrong_num==0){
            db.execSQL("delete from word_collection where english=?",new Object[]{english});
        }else{
            updateWrongNumWordCollection(english,wrong_num);
        }
    }

    public void updateWrongNumWordCollection(String english,int wrong_num){
        db.execSQL("update word_collection set wrong_num=? where english=?",new Object[]{wrong_num,english});
    }

    public void insertIntoWordCollection(int id,String english,int belongto){
        db.execSQL("insert into word_collection(id,english,wrong_num,belongto) values(?,?,?,?)",new Object[]{id,english,1,belongto});
    }

    public void insertIntoWordCollection(WordCollection wordCollection){
        db.execSQL("insert into word_collection(id,english,wrong_num,belongto) values(?,?,?,?)",new Object[]{wordCollection.getId(),wordCollection.getEnglish(),wordCollection.getWrong_num(),wordCollection.getBelongto()});
    }

    public List<WordCollection> getAll_Word_WordCollection(){
        Cursor cursor=db.rawQuery("select * from word_collection",null);
        List<WordCollection> wordList=new ArrayList<>();
        while(cursor.moveToNext()){
            wordList.add(new WordCollection(cursor.getInt(0),cursor.getString(1),cursor.getInt(2),cursor.getInt(3)));
        }
        cursor.close();
        return wordList;
    }

    public void updateAll_Word_Collection(List wordCollections){
        db.execSQL("delete from word_collection");
        db.execSQL("delete from sqlite_sequence where name='word_collection'");
        for(int i=0;i<wordCollections.size();i++){
            Gson gson=new Gson();
            WordCollection wordCollection=gson.fromJson(gson.toJson(wordCollections.get(i)),WordCollection.class);
            insertIntoWordCollection(wordCollection);
        }
    }

    public Map<String,Object> select_words_and_count_from_collection_word_by_belongto(int belongto){
        Map<String,Object> map=null;
        Cursor cursor=db.rawQuery("select * from word_collection where belongto=?",new String[]{String.valueOf(belongto)});
        String num="";
        String ids="";
        while (cursor.moveToNext()){
            ids+=cursor.getString(0)+",";
            num+=cursor.getString(1)+" ";
        }
        if(num.length()>0){
            map=new HashMap<>();
            map.put("day",belongto);
            map.put("num",num);
            map.put("words",ids);
        }
        cursor.close();
        return map;
    }
    /**
    * 获取UserData
    * */
    public UserData getUserData(){
        UserData userData=new UserData();
        userData.setWordplan(selectAllWordPlan());
        userData.setWrongwords(getAll_Word_WordCollection());
        userData.setLearneddays(selectCountFromWordsPlan());
        return userData;
    }
    /**
     * search_history
     * **/
    public List<String> select_from_search_history(){
        Cursor cursor=db.rawQuery("select text from search_history order by id DESC",null);
        List<String> texts=new ArrayList<>();
        while (cursor.moveToNext()){
            texts.add(cursor.getString(0));
        }
        return texts;
    }

    public boolean select_from_search_history_by_text(String text){
        Cursor cursor=db.rawQuery("select * from search_history where text=?",new String[]{text});
        boolean isExist=false;
        if(cursor.moveToNext()){
            isExist=true;
        }
        cursor.close();
        return isExist;
    }

    public void insert_into_search_history(String text){
        if(!select_from_search_history_by_text(text)){
            db.execSQL("insert into search_history(text) values(?);",new Object[]{text});
        }
    }

    public void delete_search_history_by_text(String text){
        db.execSQL("delete from search_history where text=?",new Object[]{text});
    }

    public void delete_all_search_history(){
        db.execSQL("delete from search_history");
        db.execSQL("delete from sqlite_sequence where name='search_history'");
        db.execSQL("delete from search_history");
    }
}
