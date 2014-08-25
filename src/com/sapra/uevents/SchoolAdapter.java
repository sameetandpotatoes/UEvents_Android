package com.sapra.uevents;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SchoolAdapter extends BaseAdapter{
	private final ArrayList<Object> mData;
    public SchoolAdapter(Context context, Map<String, String> map) {
        mData = new ArrayList<Object>();
        mData.addAll(map.entrySet());
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @SuppressWarnings("unchecked")
	@Override
    public Map.Entry<String, String> getItem(int position) {
        return (Entry<String, String>) mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	final Context context = parent.getContext();
    	if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.school_list_item, parent, false);
    	}
    	Typeface regular = Typeface.createFromAsset(context.getAssets(), Constants.REGULAR);
    	
        Map.Entry<String, String> item = getItem(position);
        final TextView school = ((TextView) convertView.findViewById(R.id.school));
        school.setTextColor(Color.BLACK);
        school.setText(item.getKey());
        school.setTag(item.getValue());
        school.setOnClickListener(new OnClickListener(){
			public void onClick(View view) {
				((SchoolActivity) context).sendBackToAPI((String) school.getText(), (String) school.getTag());
			}
        });
        school.setTypeface(regular);
    	return convertView;
    }
}
