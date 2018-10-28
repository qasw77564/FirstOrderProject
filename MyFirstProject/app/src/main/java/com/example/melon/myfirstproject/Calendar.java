package com.example.melon.myfirstproject;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


/**
 * A simple {@link Fragment} subclass.
 */
public class Calendar extends Fragment {

    private String TAG = Calendar.class.getSimpleName();
    private GridView gridView;
    private List<CalendarDate> list = Lists.<CalendarDate>newArrayList();
    private BaseAdapter adapter;

    public Calendar() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);
        getViews(v);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int dayOfWeek = java.util.Calendar.getInstance(TimeZone.getTimeZone("Asia/Taipei"), Locale.TAIWAN).get(java.util.Calendar.DAY_OF_WEEK);
        for(int i = 0; i < dayOfWeek - 1; i++){
            list.add(CalendarDate.of("", null));
        }

        Date now = new Date();
        long day = 1000 * 60 * 60 * 24L;
        SimpleDateFormat format = new SimpleDateFormat("dd");
        for(int i = 0;i<30; i++){
            String date = format.format(now);
            list.add(CalendarDate.of(date, now));
//            now.setTime(now.getTime() + day);
            now = new Date(now.getTime() + day);

        }
//        clickItem = new ClickItem();
        adapter = new CalendarAdapter(list,getContext(), new ClickItem());
        adapter.notifyDataSetChanged();
    }
    private void getViews(View v){
        gridView = v.findViewById(R.id.daysGrid);
        gridView.setAdapter(adapter);
    }

    class CalendarAdapter extends BaseAdapter {

        private List<CalendarDate> list;
        private Context context;
        private ClickItem clickItem;

        CalendarAdapter(List<CalendarDate> list, Context context, ClickItem clickItem) {
            this.context = context;
            this.list = list;
            this.clickItem = clickItem;
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
            View v = LayoutInflater.from(context).inflate(R.layout.fragment_calendar_detail, null);
//            TextView textView = new TextView(this.context);
//            textView.setText(list.get(position));
            TextView textView = v.findViewById(R.id.daysText);
            CalendarDate cDate = list.get(position);
            if (cDate.date == null || Strings.isNullOrEmpty(cDate.day)) {
                textView.setText("");
//                v.setBackgroundColor(Color.WHITE);
            } else {
                textView.setText(cDate.day);
//                v.setBackgroundColor(Color.RED);
                v.setTag(cDate.date);
                v.setOnClickListener(this.clickItem);

            }
            return v;
        }
    }
    static class CalendarDate {

        private String day;
        private Date date;

        CalendarDate (String day, Date date){
            this.day = day;
            this.date = date;
        }

        public static CalendarDate of (String day, Date date){
            return new CalendarDate(day, date);
        }

    }


    class ClickItem implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            Date date = (Date)v.getTag();

            Log.i(TAG, "on click : ->  " + date.getTime());
        }
    }

}
