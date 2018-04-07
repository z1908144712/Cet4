package com.example.bishe.cet4.tabs;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bishe.cet4.R;
import com.example.bishe.cet4.database.AssetsDatabaseManager;
import com.example.bishe.cet4.database.DBHelper;
import com.example.bishe.cet4.object.Word;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Skywilling on 2018/1/2.
 */

public class WordFragment extends Fragment{
    private TextView textView_english;
    private TextView textView_phonetic;
    private TextView textView_chinese;
    private ExampleList listView_example;
    private TextView textView_word_num;
    private SQLiteDatabase db;
    private int myChoice=0;
    private static GestureDetector gestureDetector;
    private int words_num[];
    private DBHelper dbHelper;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_tab_word,container,false);
        initViews(view);
        initEvents(view);
        initDatabase();
        initData();
        return view;
    }
    private void initDatabase(){
        AssetsDatabaseManager assetsDatabaseManager=AssetsDatabaseManager.getAssetsDatabaseManager();
        db=assetsDatabaseManager.getDatabase("dict.db");
        dbHelper=new DBHelper(db);
    }
    private void initViews(View view){
        textView_english=view.findViewById(R.id.word_english);
        textView_chinese=view.findViewById(R.id.word_chinese);
        textView_phonetic=view.findViewById(R.id.word_phonetic);
        listView_example=(ExampleList) view.findViewById(R.id.word_examples_list);
        textView_word_num=view.findViewById(R.id.word_index);
    }
    private void initEvents(final View view){
        //设置手势检测器
        gestureDetector=new GestureDetector(getContext(),new GestureEvent());
        //设置手势监听器
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
        listView_example.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return false;
            }
        });
    }
    public void initData(){
        DBHelper dbHelper=new DBHelper(db);
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("plandays",Context.MODE_PRIVATE);
        String previous_time=sharedPreferences.getString("previous_time",null);
        String words_num_str=dbHelper.selectedByTime(previous_time);
        String []words_num_strs=words_num_str.split(",");
        words_num=new int[words_num_strs.length];
        for (int i=0;i<words_num.length;i++){
            words_num[i]=Integer.parseInt(words_num_strs[i]);
        }
        showWord(0);
    }
    private void showWord(int id){
        textView_word_num.setText((id+1)+"/"+words_num.length);
        List list=dbHelper.selectWordById(words_num[id]);
        Word word=(Word)list.get(0);
        textView_english.setText(word.getEnglish());
        textView_chinese.setText("");
        String chineses[]=word.getChinese().split("#");
        for(int i=0;i<chineses.length;i++){
            textView_chinese.append(chineses[i]+" ");
        }
        textView_phonetic.setText("");
        String phonetics[]=word.getPhonetic().split(",");
        for(int i=0;i<phonetics.length;i++){
            textView_phonetic.append("["+phonetics[i]+"]\n");
        }
        String examples[]=word.getExample().split("#");
        List <Map<String,Object>> example_list=new ArrayList<Map<String,Object>>();
        for(int i=0;i<examples.length;i+=2){
            Map<String,Object> map=new HashMap<String,Object>();
            map.put("id",String.valueOf(i/2+1)+".");
            map.put("english",examples[i].substring(2));
            map.put("chinese",examples[i+1]);
            example_list.add(map);
        }
        listView_example.setAdapter(new SimpleAdapter(getContext(),example_list,R.layout.word_examples_list_layout,new String[]{"id","english","chinese"},new int[]{R.id.word_example_list_id,R.id.word_example_list_english,R.id.word_example_list_chinese}));
    }

    class GestureEvent implements GestureDetector.OnGestureListener{

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if((e2.getX()-e1.getX())>200&&Math.abs(velocityX)>50){
                //向左滑动
                if(myChoice>0){
                    myChoice--;
                    Animation animation= AnimationUtils.loadAnimation(getContext(),R.anim.anim_revert);
                    getView().startAnimation(animation);
                    showWord(myChoice);
                }else{
                    Toast.makeText(getContext(),"已经是第一个了~",Toast.LENGTH_SHORT).show();
                }
            }else if((e1.getX()-e2.getX())>200&&Math.abs(velocityX)>50){
                //向右滑动
                if(myChoice<words_num.length-1){
                    myChoice++;
                    Animation animation= AnimationUtils.loadAnimation(getContext(),R.anim.anim_next);
                    getView().startAnimation(animation);
                    showWord(myChoice);
                }else{
                    Toast.makeText(getContext(),"已经是最后一个了~",Toast.LENGTH_SHORT).show();
                }
            }
            return false;
        }
    }

    public static class ExampleList extends ListView{

        public ExampleList(Context context) {
            super(context);
        }

        public ExampleList(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public ExampleList(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            gestureDetector.onTouchEvent(ev);
            return super.onInterceptTouchEvent(ev);
        }

    }
}

