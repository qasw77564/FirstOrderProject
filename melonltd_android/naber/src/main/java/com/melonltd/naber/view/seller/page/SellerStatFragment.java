package com.melonltd.naber.view.seller.page;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.melonltd.naber.R;
import com.melonltd.naber.model.api.ApiManager;
import com.melonltd.naber.model.api.ThreadCallback;
import com.melonltd.naber.model.bean.Model;
import com.melonltd.naber.model.constant.NaberConstant;
import com.melonltd.naber.util.Tools;
import com.melonltd.naber.view.factory.PageType;
import com.melonltd.naber.view.seller.SellerMainActivity;
import com.melonltd.naber.vo.SellerStatVo;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

public class SellerStatFragment extends Fragment implements View.OnClickListener {
//    private static final String TAG = SellerStatFragment.class.getSimpleName();
    public static SellerStatFragment FRAGMENT = null;
    public static int TO_SELLER_ORDERS_LOGS_INDEX = -1;

    private Handler handler;
    private TextView finishOrderText, yearIncomeText, monthIncomeText, dayIncomeText, finishCountText, statusDateText, unFinishCountText, processingCount, canFetchCountText, cancelCountText;
    private LoadDataRun loadDataRun;

    public SellerStatFragment() {
    }

    public Fragment getInstance(Bundle bundle) {
        if (FRAGMENT == null) {
            FRAGMENT = new SellerStatFragment();
            TO_SELLER_ORDERS_LOGS_INDEX = -1;
        }
        if (bundle != null) {
            FRAGMENT.setArguments(bundle);
        }
        return FRAGMENT;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_seller_stat, container, false);
        getViews(v);
        return v;
    }

    private void getViews(View v) {

        BGARefreshLayout refreshLayout = v.findViewById(R.id.seller_stat_main_page);
        BGANormalRefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(getContext(), true);
        refreshViewHolder.setPullDownRefreshText("Pull");
        refreshViewHolder.setRefreshingText("Pull to refresh");
        refreshViewHolder.setReleaseRefreshText("Pull to refresh");
        refreshViewHolder.setLoadingMoreText("Loading more !");
        refreshLayout.setRefreshViewHolder(refreshViewHolder);

        finishOrderText = v.findViewById(R.id.finishOrderText);
        yearIncomeText = v.findViewById(R.id.yearIncomeText);
        monthIncomeText = v.findViewById(R.id.monthIncomeText);
        dayIncomeText = v.findViewById(R.id.dayIncomeText);
        finishCountText = v.findViewById(R.id.finishCountText);
        statusDateText = v.findViewById(R.id.statusDateText);
        unFinishCountText = v.findViewById(R.id.unFinishCountText);
        processingCount = v.findViewById(R.id.processingCount);
        canFetchCountText = v.findViewById(R.id.canFetchCountText);
        cancelCountText = v.findViewById(R.id.cancelCountText);

        finishOrderText.setOnClickListener(this);
        refreshLayout.setDelegate(new BGARefreshLayout.BGARefreshLayoutDelegate() {
            @Override
            public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
                refreshLayout.endRefreshing();
                handler.removeCallbacks(loadDataRun);
                handler.post(loadDataRun);
            }

            @Override
            public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
                return false;
            }
        });
    }

    class LoadDataRun implements Runnable {
        @Override
        public void run() {
            ApiManager.sellerStat(new ThreadCallback(getContext()) {
                @Override
                public void onSuccess(String responseBody) {
                    Model.SELLER_STAT = Tools.JSONPARSE.fromJson(responseBody, SellerStatVo.class);

                    yearIncomeText.setText("$ " + Model.SELLER_STAT.year_income);
                    monthIncomeText.setText("$ " + Model.SELLER_STAT.month_income);
                    dayIncomeText.setText("$ " + Model.SELLER_STAT.day_income);
                    finishCountText.setText(Model.SELLER_STAT.finish_count);
                    String dates = "( ";
                    for (int i = 0; i < Model.SELLER_STAT.status_dates.length; i++) {
                        dates += Tools.FORMAT.format(NaberConstant.DATE_FORMAT_PATTERN, "yyyy-MM-dd", Model.SELLER_STAT.status_dates[i]);
                        if (i + 1 < Model.SELLER_STAT.status_dates.length) {
                            dates += " ~ ";
                        }
                    }
                    dates += " )";

                    statusDateText.setText(dates);
                    unFinishCountText.setText(Model.SELLER_STAT.unfinish_count);
                    processingCount.setText(Model.SELLER_STAT.processing_count);
                    canFetchCountText.setText(Model.SELLER_STAT.can_fetch_count);
                    cancelCountText.setText(Model.SELLER_STAT.cancel_count);

                    handler.postDelayed(loadDataRun, NaberConstant.SELLER_STAT_REFRESH_TIMER);
                }

                @Override
                public void onFail(Exception error, String msg) {

                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SellerMainActivity.changeTabAndToolbarStatus();
        SellerMainActivity.lockDrawer(true);
        if (TO_SELLER_ORDERS_LOGS_INDEX >= 0) {
            TO_SELLER_ORDERS_LOGS_INDEX = 1;
            SellerMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.SELLER_ORDERS_LOGS, null);
        }
        loadDataRun = new LoadDataRun();
        handler.post(loadDataRun);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (handler != null) {
            handler.removeCallbacks(loadDataRun);
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onClick(View view) {
        TO_SELLER_ORDERS_LOGS_INDEX = 1;
        SellerMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.SELLER_ORDERS_LOGS, null);
    }
}
