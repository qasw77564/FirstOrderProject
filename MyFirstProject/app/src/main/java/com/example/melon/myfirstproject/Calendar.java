package com.example.melon.myfirstproject;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.google.common.collect.Lists;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Calendar extends Fragment {

    private GridView gridView;
    private List<String> list = Lists.newArrayList();
    private BaseAdapter adapter;
    public Calendar() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for(int i = 0;i < 30;i++){
            list.add(i + "");
        }
        adapter = new CalendarAdapter(list,getContext());
        adapter.notifyDataSetChanged();
    }

    class CalendarAdapter extends BaseAdapter{
        private List<String> list;
        private Context context;
        CalendarAdapter(List<String> list,Context context){
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(this.context);
            textView.setText(list.get(position));
            return textView;
        }
    }

}
