package com.example.bishe.cet4.tabs;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.bishe.cet4.R;
import com.example.bishe.cet4.activity.WordDetailActivity;
import com.example.bishe.cet4.database.AssetsDatabaseManager;
import com.example.bishe.cet4.database.DBHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Skywilling on 2018/1/2.
 */

public class SearchFragment extends Fragment {
    private EditText editText_input=null;
    private Drawable drawable_search_icon=null;
    private Drawable drawable_search_delete_btn=null;
    private Button search_btn=null;
    private ListView listView_suggestion=null;
    private List<Map<String, String>> suggestWords=null;
    private DBHelper dbHelper=null;
    private SQLiteDatabase db=null;
    private String keyword=null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_tab_search,container,false);
        //初始化数据库
        initDataBase();
        //初始化控件
        initView(view);
        //初始化事件
        initEvent();
        return view;
    }
    private void initDataBase(){
        AssetsDatabaseManager assetsDatabaseManager=AssetsDatabaseManager.getAssetsDatabaseManager();
        db=assetsDatabaseManager.getDatabase("dict.db");
        dbHelper=new DBHelper(db);
    }
    private void initView(View view){
        editText_input=view.findViewById(R.id.search_input);
        search_btn=view.findViewById(R.id.search_btn);
        listView_suggestion=view.findViewById(R.id.search_suggestion);
        drawable_search_icon=getResources().getDrawable(R.drawable.ic_search_icon);
        drawable_search_icon.setBounds(0,0,drawable_search_icon.getIntrinsicWidth(),drawable_search_icon.getIntrinsicHeight());
        drawable_search_delete_btn=getResources().getDrawable(R.drawable.ic_search_delete_button);
        drawable_search_delete_btn.setBounds(0,0,drawable_search_delete_btn.getIntrinsicWidth(),drawable_search_delete_btn.getIntrinsicHeight());
        editText_input.setCompoundDrawables(drawable_search_icon,null,null,null);
    }
    private void initEvent(){
        editText_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0){
                    keyword= inputFilter(s);
                    if(keyword.length()>0){
                        if(isLocatedSearch(keyword)){
                            //本地搜索
                            suggestWords = dbHelper.getSuggestWords(keyword);
                            showSuggestionWords(suggestWords,keyword);
                        }
                    }
                    editText_input.setCompoundDrawables(drawable_search_icon,null,drawable_search_delete_btn,null);
                }else{
                    if(suggestWords==null){
                        suggestWords=new ArrayList<>();
                    }else{
                        suggestWords.clear();
                    }
                    showSuggestionWords(suggestWords,"");
                    editText_input.setCompoundDrawables(drawable_search_icon,null,null,null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        editText_input.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(editText_input.getCompoundDrawables()[2]==null){
                    return false;
                }else{
                    if(event.getAction()!=MotionEvent.ACTION_UP){
                        return false;
                    }
                    if(event.getX()>(editText_input.getWidth()-editText_input.getCompoundDrawables()[2].getBounds().width())){
                        editText_input.setText("",null);
                        editText_input.setCompoundDrawables(drawable_search_icon,null,null,null);
                        return true;
                    }else {
                        return false;
                    }
                }
            }
        });
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(getContext(), WordDetailActivity.class);
                intent.putExtra("keyword",keyword);
                if(isChinese(keyword)){
                    intent.putExtra("type",WordDetailActivity.TYPE_CHINESE);
                }else if(isEnglish(keyword)){
                    intent.putExtra("type",WordDetailActivity.TYPE_ENGLISH);
                }else{
                    intent.putExtra("type",WordDetailActivity.TYPE_AUTO);
                }
                intent.putExtra("type_show",WordDetailActivity.TYPE_DETAIL);
                startActivity(intent);
            }
        });
        listView_suggestion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent();
                intent.setClass(getContext(), WordDetailActivity.class);
                intent.putExtra("keyword",suggestWords.get(position).get("english"));
                intent.putExtra("type",WordDetailActivity.TYPE_ENGLISH);
                intent.putExtra("type_show",WordDetailActivity.TYPE_DETAIL);
                startActivity(intent);
            }
        });
    }

    private String inputFilter(CharSequence s){
        String res="";
        for(int i=0;i<s.length();i++){
            if(s.charAt(i)!='*'&&s.charAt(i)!='_'&&s.charAt(i)!='\''){
                res+=s.charAt(i);
            }
        }
        return res.trim();
    }

    private Boolean isChinese(String str){
        for(int i=0;i<str.length();i++){
            int n=(int)str.charAt(i);
            if(n<19968||n>40623){
                return false;
            }
        }
        return true;
    }

    private Boolean isEnglish(String str){
        for(int i=0;i<str.length();i++){
            char n=str.charAt(i);
            if(!(n>='a'&&n<='z')||!(n>='A'&&n<='Z')){
                return false;
            }
        }
        return true;
    }

    private Boolean isLocatedSearch(String str){
        char n=str.charAt(0);
        if((n>='0'&&n<='9')||(n>='a'&&n<='z')||(n>='A'&&n<='Z')||n=='①'||n=='②'||n=='Ⅱ'||n=='α'||n=='β'||n=='γ'||n=='δ'||n=='ε'||n=='λ'){
            return true;
        }else{
            return false;
        }
    }

    private void showSuggestionWords(final List words, final String keyword){
        listView_suggestion.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return words.size();
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view=null;
                if(null==convertView){
                    LayoutInflater inflater=LayoutInflater.from(getContext());
                    view=inflater.inflate(R.layout.word_suggestion_item_layout,null);
                }else{
                    view=convertView;
                }
                view.setMinimumHeight(160);
                Map<String,String> map=(Map<String, String>) words.get(position);
                TextView english=view.findViewById(R.id.english);
                TextView chinese=view.findViewById(R.id.chinese);
                english.setText(matcherSearchKeyword(map.get("english"),Color.RED,keyword));
                chinese.setText(map.get("chinese"));
                return view;
            }
        });
    }

    private SpannableString matcherSearchKeyword(String text,int color,String keyword){
        SpannableString spannableString=new SpannableString(text);
        Pattern pattern=Pattern.compile(keyword);
        Matcher matcher=pattern.matcher(spannableString);
        if(matcher.find()){
            int start=matcher.start();
            int end=matcher.end();
            spannableString.setSpan(new ForegroundColorSpan(color),start,end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }
}
