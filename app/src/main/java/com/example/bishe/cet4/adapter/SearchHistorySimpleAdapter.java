package com.example.bishe.cet4.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


import com.example.bishe.cet4.R;

import java.util.List;
import java.util.Map;


public class SearchHistorySimpleAdapter extends SimpleAdapter implements OnClickListener{
    private Context context;
    private List<? extends Map<String,?>> data;
    private int resource;
    private String []from;
    private int []to;
    private SearchHistorySimpleAdapter.CallBack callBack;
    private LayoutInflater layoutInflater;

    public SearchHistorySimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to,CallBack callBack) {
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
        return data.size()+1;
    }

    @Override
    public Object getItem(int position) {
        return position<data.size()?data.get(position):null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(position<data.size()){
            ViewHolder viewHolder=null;
            if(convertView==null){
                convertView=layoutInflater.inflate(resource,null);
                viewHolder=new ViewHolder();
                viewHolder.tv_text=convertView.findViewById(to[0]);
                viewHolder.iv_delete=convertView.findViewById(R.id.id_delete);
                convertView.setTag(viewHolder);
            }else{
                viewHolder=(ViewHolder) convertView.getTag();
            }
            viewHolder.tv_text.setText(String.valueOf(data.get(position).get(from[0])));
            viewHolder.iv_delete.setOnClickListener(this);
            viewHolder.iv_delete.setTag(position);
            convertView.setMinimumHeight(160);
        }else {
            if(convertView==null){
                convertView=layoutInflater.inflate(R.layout.search_history_delete_all_layout,null);
            }
            convertView.setMinimumHeight(160);
        }
        return convertView;
    }

    @Override
    public void onClick(View v) {
        callBack.OnClick(v);
    }

    public class ViewHolder{
        public TextView tv_text;
        public ImageView iv_delete;
    }


    public interface CallBack{
        public void OnClick(View v);
    }
}
