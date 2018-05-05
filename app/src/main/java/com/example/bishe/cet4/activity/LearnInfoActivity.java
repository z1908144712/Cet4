package com.example.bishe.cet4.activity;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bishe.cet4.R;
import com.example.bishe.cet4.database.AssetsDatabaseManager;
import com.example.bishe.cet4.database.DBHelper;
import com.example.bishe.cet4.function.MyToast;
import com.example.bishe.cet4.function.WordTimer;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class LearnInfoActivity extends Activity implements View.OnClickListener{
    private BarChart barChart=null;
    private SQLiteDatabase db=null;
    private DBHelper dbHelper=null;
    private List<String> datas=null;
    private List<BarEntry> barEntries=null;
    private BarDataSet barDataSet=null;
    private BarData barData=null;
    private ImageView iv_back=null;
    private XAxis xAxis=null;
    private YAxis leftYAxis=null;
    private YAxis rightYAxis=null;
    private LimitLine limitLine=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.learn_info_activity_layout);
        //初始化数据库
        initDataBase();
        //初始化控件
        initViews();
        //初始化数据
        initData();
    }

    private void initDataBase(){
        AssetsDatabaseManager assetsDatabaseManager=AssetsDatabaseManager.getAssetsDatabaseManager();
        db=assetsDatabaseManager.getDatabase("dict.db");
        dbHelper=new DBHelper(db);
    }

    private void initViews(){
        iv_back=findViewById(R.id.id_back);
        iv_back.setOnClickListener(this);
        barChart=findViewById(R.id.id_barChart);
        barChart.setDescription(null);
        xAxis=barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(7);
        leftYAxis =barChart.getAxisLeft();
        rightYAxis=barChart.getAxisRight();
    }

    private void initData(){
        datas=dbHelper.selectAllLearnedTime();
        float average=0;
        barEntries=new ArrayList<>();
        for(int i=1;i<=datas.size();i++){
            average+=time(datas.get(i-1));
            barEntries.add(new BarEntry(i,time(datas.get(i-1))));
        }
        average/=datas.size();
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return "第"+(int)value+"天";
            }
        });
        limitLine=new LimitLine(average,"平均时间:"+String.format("%.1f",average)+"分钟");
        limitLine.setLineWidth(3f);
        leftYAxis.setAxisMinimum(0f);
        rightYAxis.setAxisMinimum(0f);
        rightYAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return value+"分钟";
            }
        });
        leftYAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return value+"分钟";
            }
        });
        leftYAxis.addLimitLine(limitLine);
        barDataSet=new BarDataSet(barEntries,"学习时间");
        barData=new BarData(barDataSet);
        barChart.setData(barData);
        MyMarkerView myMarkerView=new MyMarkerView(this);
        barChart.setMarker(myMarkerView);
    }

    private float time(String time){
        long t= WordTimer.time_string_to_long(time);
        return t*1.0f/60;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_back:
                finish();
                break;
        }
    }

    public class MyMarkerView extends MarkerView{
        private TextView tv_Content=null;

        public MyMarkerView(Context context) {
            super(context, R.layout.markerview_layout);
            tv_Content=findViewById(R.id.id_tvContent);
        }

        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            tv_Content.setText(String.format("%.2f",e.getY()));
            super.refreshContent(e, highlight);
        }

        @Override
        public MPPointF getOffset() {
            return new MPPointF(-(getWidth() / 2), -getHeight() - 10);
        }
    }
}
