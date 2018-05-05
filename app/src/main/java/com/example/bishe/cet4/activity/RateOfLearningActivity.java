package com.example.bishe.cet4.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bishe.cet4.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RateOfLearningActivity extends Activity implements View.OnClickListener{
    private ExpandableListView expandableListView=null;
    private List<String> groupName=null;
    private List<List<Map<String,String>>> childContext=null;
    private TextView textView_title=null;
    private ImageView iv_back=null;
    private PieChart pieChart=null;
    private int words_count;
    private int learned_words_count;
    private List<PieEntry> pieEntries=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate_of_learning_layout);
        //初始化控件
        initViews();
        //初始化数据
        initData();
        //初始化事件
        initEvents();
    }

    private void initData(){
        Intent intent=getIntent();
        Bundle bundle=intent.getBundleExtra("RateOfLearning");
        groupName= (List<String>) bundle.getSerializable("groupName");
        childContext= (List<List<Map<String, String>>>) bundle.getSerializable("childContext");
        expandableListView.setAdapter(new RateOfLearningActivity.MyExpandableListViewAdapter());
        words_count=intent.getIntExtra("words_count",-1);
        learned_words_count=intent.getIntExtra("learned_words_count",-1);
        pieEntries=new ArrayList<>();
        pieEntries.add(new PieEntry((words_count-learned_words_count)*100.0f/words_count,"未学习"));
        pieEntries.add(new PieEntry(learned_words_count*100.0f/words_count,"已学习"));
        PieDataSet pieDataSet=new PieDataSet(pieEntries,"");
        pieDataSet.setSliceSpace(3f);
        pieDataSet.setSelectionShift(10f);
        pieDataSet.addColor(Color.parseColor("#f17548"));
        PieData pieData=new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setDrawValues(true);
        pieData.setValueTextSize(20f);
        pieChart.setData(pieData);
    }

    private void initViews(){
        expandableListView=findViewById(R.id.expandableListView);
        textView_title=findViewById(R.id.id_title);
        iv_back=findViewById(R.id.id_back);
        pieChart=findViewById(R.id.id_pieChart);
        pieChart.setDescription(null);
        pieChart.setDrawHoleEnabled(false);
        pieChart.setDrawEntryLabels(false);
        pieChart.setUsePercentValues(true);
    }

    private void initEvents(){
       iv_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_back:
                finish();
                break;
        }
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
