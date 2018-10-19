package com.melonltd.naber.view.seller.page;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectChangeListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.melonltd.naber.R;
import com.melonltd.naber.model.api.ApiManager;
import com.melonltd.naber.model.api.ThreadCallback;
import com.melonltd.naber.model.bean.Model;
import com.melonltd.naber.model.constant.NaberConstant;
import com.melonltd.naber.util.Tools;
import com.melonltd.naber.view.factory.PageType;
import com.melonltd.naber.view.seller.SellerMainActivity;
import com.melonltd.naber.view.seller.adapter.SellerOrdersLogsAdapter;
import com.melonltd.naber.vo.OrderDetail;
import com.melonltd.naber.vo.OrderVo;
import com.melonltd.naber.vo.ReqData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

public class SellerOrdersLogsFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = SellerOrdersLogsFragment.class.getSimpleName();
    public static SellerOrdersLogsFragment FRAGMENT = null;

    private TextView startTimeText, endTimeText;
    private TimePickerView timePickerView;
    private ReqData req;
    private SellerOrdersLogsAdapter adapter;
    public static int TO_ORDERS_LOGS_DETAIL_INDEX = -1;

    public SellerOrdersLogsFragment() {
    }

    public Fragment getInstance(Bundle bundle) {
        if (FRAGMENT == null) {
            FRAGMENT = new SellerOrdersLogsFragment();
            TO_ORDERS_LOGS_DETAIL_INDEX = -1;
        }
        if (bundle != null) {
            FRAGMENT.setArguments(bundle);
        }
        return FRAGMENT;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        req = new ReqData();
        adapter = new SellerOrdersLogsAdapter(new ItemOnClickListener());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SellerMainActivity.toolbar.setNavigationIcon(null);
//        if (container.getTag(R.id.seller_orders_logs_page) == null) {
        View v = inflater.inflate(R.layout.fragment_seller_orders_logs, container, false);
        getViews(v);
//            container.setTag(R.id.seller_orders_logs_page, v);
        return v;
//        }
//        return (View) container.getTag(R.id.seller_orders_logs_page);
    }

    private void getViews(View v) {
        BGARefreshLayout refreshLayout = v.findViewById(R.id.sellerOrdersLogsBGARefreshLayout);
        final RecyclerView recyclerView = v.findViewById(R.id.sellerOrdersLogsRecyclerView);
        startTimeText = v.findViewById(R.id.startTimeText);
        endTimeText = v.findViewById(R.id.endTimeText);

        BGANormalRefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(getContext(), true);
        refreshViewHolder.setPullDownRefreshText("Pull");
        refreshViewHolder.setRefreshingText("Pull to refresh");
        refreshViewHolder.setReleaseRefreshText("Pull to refresh");
        refreshViewHolder.setLoadingMoreText("Loading more !");

        refreshLayout.setRefreshViewHolder(refreshViewHolder);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        Calendar now = Calendar.getInstance();
        startTimeText.setText(new SimpleDateFormat("yyyy-MM-dd").format(now.getTime()));
        startTimeText.setTag(Tools.FORMAT.formatDate(now.getTime()));
        endTimeText.setText(new SimpleDateFormat("yyyy-MM-dd").format(now.getTime()));
        endTimeText.setTag(Tools.FORMAT.formatDate(now.getTime()));

        startTimeText.setOnClickListener(this);
        endTimeText.setOnClickListener(this);

        recyclerView.setAdapter(adapter);
        refreshLayout.setDelegate(new BGARefreshLayout.BGARefreshLayoutDelegate() {
            @Override
            public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
                refreshLayout.endRefreshing();
                loadData(true);
            }

            @Override
            public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
                refreshLayout.endLoadingMore();
                if (req.loadingMore) {
                    loadData(false);
                }
                return false;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Model.SELLER_STAT_LOGS.size() == 0) {
            loadData(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SellerMainActivity.changeTabAndToolbarStatus();

        if (SellerMainActivity.toolbar != null) {
            SellerMainActivity.navigationIconDisplay(true, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SellerStatFragment.TO_SELLER_ORDERS_LOGS_INDEX = -1;
                    SellerMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.SELLER_STAT, null);
                    SellerMainActivity.navigationIconDisplay(false, null);
                }
            });
        }

        if (TO_ORDERS_LOGS_DETAIL_INDEX >= 0) {
            SellerMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.SELLER_ORDERS_LOGS_DETAIL, null);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        SellerMainActivity.navigationIconDisplay(false, null);
    }

    @Override
    public void onClick(final View view) {
        final int viewId = view.getId();
        // 2月記錄
        long oneYears = 60L * 1000 * 60 * 60 * 24L;
        final Calendar now = Calendar.getInstance();
        final Calendar startDate = Calendar.getInstance();
        startDate.setTimeInMillis(now.getTime().getTime() - oneYears);

        timePickerView = new TimePickerBuilder(getContext(),
                new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        if (viewId == R.id.startTimeText) {
                            startTimeText.setTag(Tools.FORMAT.formatDate(date));
                            startTimeText.setText(new SimpleDateFormat("yyyy-MM-dd").format(date));
                            loadData(true);
                        } else if (viewId == R.id.endTimeText) {
                            endTimeText.setTag(Tools.FORMAT.formatDate(date));
                            endTimeText.setText(new SimpleDateFormat("yyyy-MM-dd").format(date));
                            loadData(true);
                        }
                    }
                })
                .setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
                    @Override
                    public void onTimeSelectChanged(Date date) {
                        // 開始時間不可以大於結束時間
                        if (viewId == R.id.startTimeText) {
                            if (Tools.FORMAT.formatDate(date).compareTo(endTimeText.getTag().toString()) > 0) {
                                startTimeText.setTag(Tools.FORMAT.formatDate(date));
                                setDate(startDate);
                            }
                        } else if (viewId == R.id.endTimeText) {
                            if (Tools.FORMAT.formatDate(date).compareTo(startTimeText.getTag().toString()) < 0) {
                                endTimeText.setTag(Tools.FORMAT.formatDate(date));
                                setDate(now);
                            }
                        }
                    }
                }).setType(new boolean[]{true, true, true, false, false, false})
                .setTitleSize(20)
                .setOutSideCancelable(true)
                .isCyclic(false)
                .setTitleBgColor(getResources().getColor(R.color.naber_dividing_line_gray))
                .setCancelColor(getResources().getColor(R.color.naber_dividing_gray))
                .setSubmitColor(getResources().getColor(R.color.naber_dividing_gray))
                .setDate(viewId == R.id.startTimeText ? startDate : now)
                .setRangDate(startDate, now)
                .isCenterLabel(false)
                .isDialog(false)
                .build();

        timePickerView.show();
    }

    private void setDate(Calendar date) {
        timePickerView.setDate(date);
        timePickerView.returnData();
    }


    private void loadData(boolean isRefresh) {
        if (isRefresh) {
            Model.SELLER_STAT_LOGS.clear();
            req.page = 0;
            req.loadingMore = true;
        }
        req.page++;
        req.start_date = startTimeText.getTag().toString();
        req.end_date = endTimeText.getTag().toString();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ApiManager.sellerStatLog(req, new ThreadCallback(getContext()) {
                    @Override
                    public void onSuccess(String responseBody) {
                        List<OrderVo> list = Tools.JSONPARSE.fromJsonList(responseBody, OrderVo[].class);
                        for (int i = 0; i < list.size(); i++) {
                            list.get(i).order_detail = Tools.JSONPARSE.fromJson(list.get(i).order_data, OrderDetail.class);
                        }
                        req.loadingMore = list.size() % NaberConstant.PAGE == 0 && list.size() != 0;
                        Model.SELLER_STAT_LOGS.addAll(list);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFail(Exception error, String msg) {

                    }
                });
            }
        }, 300);
    }

    class ItemOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int index = (int) v.getTag();
            TO_ORDERS_LOGS_DETAIL_INDEX = index;
            Bundle bundle = new Bundle();
            bundle.putSerializable(NaberConstant.SELLER_STAT_LOGS_DETAIL, Model.SELLER_STAT_LOGS.get(index));
            SellerMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.SELLER_ORDERS_LOGS_DETAIL, bundle);
        }
    }

}
