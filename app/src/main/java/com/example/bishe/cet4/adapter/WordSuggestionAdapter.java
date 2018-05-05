package com.example.bishe.cet4.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.bishe.cet4.R;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordSuggestionAdapter extends SimpleAdapter {
    private Context context;
    private List<? extends Map<String,?>> data;
    private int resource;
    private String []from;
    private int []to;
    private LayoutInflater layoutInflater;
    private String keyword;

    public WordSuggestionAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to,String keyword) {
        super(context, data, resource, from, to);
        this.context=context;
        this.layoutInflater=LayoutInflater.from(context);
        this.data=data;
        this.resource=resource;
        this.from=from;
        this.to=to;
        this.keyword=keyword;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if(convertView==null){
            convertView=layoutInflater.inflate(resource,null);
            viewHolder=new ViewHolder();
            viewHolder.tv_english=convertView.findViewById(to[0]);
            viewHolder.tv_chinese=convertView.findViewById(to[1]);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_english.setText(matcherSearchKeyword(data.get(position).get(from[0]).toString(), Color.RED,keyword));
        viewHolder.tv_chinese.setText(data.get(position).get(from[1]).toString());
        convertView.setMinimumHeight(160);
        return convertView;
    }

    private SpannableString matcherSearchKeyword(String text, int color, String keyword){
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

    public class ViewHolder{
        TextView tv_chinese;
        TextView tv_english;
    }
}
