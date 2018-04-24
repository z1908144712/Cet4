package com.example.bishe.cet4.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.bishe.cet4.R;

import java.util.List;
import java.util.Map;

public class WordCollectionSimpleAdapter extends SimpleAdapter implements View.OnClickListener {
    private Context context;
    private List<? extends Map<String,?>> data;
    private int resource;
    private String []from;
    private int []to;
    private CallBack callBack;
    private LayoutInflater layoutInflater;


    public WordCollectionSimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to,CallBack callBack) {
        super(context, data, resource, from, to);
        this.context=context;
        this.layoutInflater=LayoutInflater.from(context);
        this.data=data;
        this.resource=resource;
        this.from=from;
        this.to=to;
        this.callBack=callBack;
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
            viewHolder.textView_english=convertView.findViewById(to[0]);
            viewHolder.textView_wrong_num=convertView.findViewById(to[1]);
            viewHolder.button_delete=convertView.findViewById(R.id.id_word_collection_delete);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)convertView.getTag();
        }
        viewHolder.textView_english.setText(String.valueOf(data.get(position).get(from[0])));
        viewHolder.textView_wrong_num.setText(String.valueOf(data.get(position).get(from[1])));
        viewHolder.button_delete.setOnClickListener(this);
        viewHolder.button_delete.setTag(position);
        return convertView;
    }

    @Override
    public void onClick(View v) {
        callBack.OnClick(v);
    }

    public class ViewHolder{
        public TextView textView_english;
        public TextView textView_wrong_num;
        public Button button_delete;
    }

    public interface CallBack{
        public void OnClick(View v);
    }
}
