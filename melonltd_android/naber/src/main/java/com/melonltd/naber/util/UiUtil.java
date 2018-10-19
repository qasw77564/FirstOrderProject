package com.melonltd.naber.util;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class UiUtil {

//    private static final String TAG = UiUtil.class.getSimpleName();
//    public static void setListViewHeightBasedOnChildren(Activity activity, final ListView listView) {
//        final ListAdapter listAdapter = listView.getAdapter();
//        if (listAdapter == null) {
//            return;
//        }
//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
//                for (int i = 0; i < listAdapter.getCount(); i++) {
//                    View listItem = listAdapter.getView(i, null, listView);
//                    if (listItem instanceof ViewGroup) {
//                        listItem.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, AbsListView.LayoutParams.WRAP_CONTENT));
//                    }
//                    listItem.measure(0, 0);
//                    totalHeight += listItem.getMeasuredHeight();
//                }
//
//                ViewGroup.LayoutParams params = listView.getLayoutParams();
//                params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
//                listView.setLayoutParams(params);
//            }
//        });
//    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        if (listView == null) return;

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = (totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)));
        listView.setLayoutParams(params);
    }
}
