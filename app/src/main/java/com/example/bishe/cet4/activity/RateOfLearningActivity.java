package com.example.bishe.cet4.activity;

import android.app.Activity;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.bishe.cet4.R;
import com.example.bishe.cet4.database.AssetsDatabaseManager;
import com.example.bishe.cet4.database.DBHelper;
import com.example.bishe.cet4.tabs.LearnFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RateOfLearningActivity extends Activity {
    private ExpandableListView expandableListView=null;
    private List<String> groupName=null;
    private List<List<Map<String,String>>> childContext=null;
    private SQLiteDatabase db=null;
    private DBHelper dbHelper=null;
    private TextView textView_title=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate_of_learning_layout);
        //初始化数据库
        initDataBase();
        //初始化数据
        initData();
        //初始化控件
        initViews();
        //初始化事件
        initEvents();
    }

    private void initDataBase(){
        AssetsDatabaseManager assetsDatabaseManager=AssetsDatabaseManager.getAssetsDatabaseManager();
        db=assetsDatabaseManager.getDatabase("dict.db");
        dbHelper=new DBHelper(db);
    }

    private void initViews(){
        expandableListView=findViewById(R.id.expandableListView);
        textView_title=findViewById(R.id.id_title);
        expandableListView.setAdapter(new RateOfLearningActivity.MyExpandableListViewAdapter());
    }

    private void initEvents(){
        textView_title.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(textView_title.getCompoundDrawables()[0]==null){
                    return false;
                }else{
                    if(event.getAction()!=MotionEvent.ACTION_DOWN){
                        return false;
                    }
                    if(event.getX()<textView_title.getCompoundDrawables()[0].getBounds().width()){
                        finish();
                        return true;
                    }else{
                        return false;
                    }
                }
            }
        });
    }

    private void initData(){
        groupName=new ArrayList<>();
        childContext=new ArrayList<>();
        List<String> learn_word=dbHelper.selectAllLearnWord();
        String []learned_word_num_array;
        List<Map<String,String>> childs;
        //已学习
        if(learn_word.size()>1){                        //除今天以外的数据
            for(int i=0;i<learn_word.size()-1;i++){
                learned_word_num_array=learn_word.get(i).split(",");
                childs=new ArrayList();
                for(int j=0;j<learned_word_num_array.length;j++){
                    Map map=new HashMap();
                    map.put("id",learned_word_num_array[j]);
                    map.put("english",dbHelper.selectEnglishById(Integer.parseInt(learned_word_num_array[j])));
                    childs.add(map);
                }
                groupName.add("第"+(i+1)+"天  共"+childs.size()+"个");
                childContext.add(childs);
            }
        }
        if(learn_word.size()==1){                   //今天的数据
            learned_word_num_array=learn_word.get(0).split(",");
        }else{
            learned_word_num_array=learn_word.get(learn_word.size()-1).split(",");
        }
        childs=new ArrayList();
        for(int i=0;i<learned_word_num_array.length;i++){
            Map map=new HashMap();
            map.put("id",learned_word_num_array[i]);
            map.put("english",dbHelper.selectEnglishById(Integer.parseInt(learned_word_num_array[i])));
            childs.add(map);
        }
        groupName.add("今天   共"+childs.size()+"个");
        childContext.add(childs);
        //未学习
        String learned_word_str=dbHelper.selectAllLearnWordNum();
        learned_word_num_array=learned_word_str.split(",");
        int []learned_words_int=new int[learned_word_num_array.length];
        for(int i=0;i<learned_word_num_array.length;i++){
            learned_words_int[i]=Integer.parseInt(learned_word_num_array[i]);
        }
        Arrays.sort(learned_words_int);
        childs=new ArrayList<>();
        int j=0,i=0;
        while(i<1000){
            if(j<learned_words_int.length&&i==learned_words_int[j]){
                i++;
                j++;
            }else{
                Map map=new HashMap();
                map.put("id",i);
                map.put("english",dbHelper.selectEnglishById(i));
                childs.add(map);
                i++;
            }
        }
        groupName.add("未学习  共"+childs.size()+"个");
        childContext.add(childs);
    }

    class MyExpandableListViewAdapter implements ExpandableListAdapter {

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getGroupCount() {
            return groupName.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return childContext.get(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groupName.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return childContext.get(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            RateOfLearningActivity.GroupViewHolder groupViewHolder;
            if(convertView==null){
                convertView= LayoutInflater.from(getApplicationContext()).inflate(R.layout.learn_word_item_group_layout,parent,false);
                groupViewHolder=new RateOfLearningActivity.GroupViewHolder();
                groupViewHolder.setGroupName((TextView) convertView.findViewById(R.id.item_group));
                convertView.setTag(groupViewHolder);
            }else{
                groupViewHolder=(RateOfLearningActivity.GroupViewHolder) convertView.getTag();
            }
            groupViewHolder.getGroupName().setText(groupName.get(groupPosition));
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            RateOfLearningActivity.ChildViewHolder childViewHolder;
            if(convertView==null){
                convertView=LayoutInflater.from(getApplicationContext()).inflate(R.layout.learn_word_item_child_layout,parent,false);
                childViewHolder=new RateOfLearningActivity.ChildViewHolder();
                childViewHolder.setChildContext((TextView) convertView.findViewById(R.id.item_child));
                convertView.setTag(childViewHolder);
            }else{
                childViewHolder=(RateOfLearningActivity.ChildViewHolder) convertView.getTag();
            }
            childViewHolder.getChildContext().setText(childContext.get(groupPosition).get(childPosition).get("english"));
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public void onGroupExpanded(int groupPosition) {

        }

        @Override
        public void onGroupCollapsed(int groupPosition) {

        }

        @Override
        public long getCombinedChildId(long groupId, long childId) {
            return 0;
        }

        @Override
        public long getCombinedGroupId(long groupId) {
            return 0;
        }
    }

    class GroupViewHolder{
        private TextView groupName;

        public TextView getGroupName() {
            return groupName;
        }

        public void setGroupName(TextView groupName) {
            this.groupName = groupName;
        }
    }

    class ChildViewHolder{
        private TextView childContext;

        public TextView getChildContext() {
            return childContext;
        }

        public void setChildContext(TextView childContext) {
            this.childContext = childContext;
        }
    }
}
