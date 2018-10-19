package com.melonltd.naber.view.user.page;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.collect.Lists;
import com.melonltd.naber.R;
import com.melonltd.naber.model.api.ApiManager;
import com.melonltd.naber.model.api.ThreadCallback;
import com.melonltd.naber.model.constant.NaberConstant;
import com.melonltd.naber.util.Tools;
import com.melonltd.naber.view.factory.PageType;
import com.melonltd.naber.view.user.UserMainActivity;
import com.melonltd.naber.view.user.adapter.UserOrderHistoryAdapter;
import com.melonltd.naber.vo.OrderVo;
import com.melonltd.naber.vo.ReqData;

import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

public class UserOrderHistoryFragment extends Fragment {
//    private static final String TAG = UserHomeFragment.class.getSimpleName();
    public static UserOrderHistoryFragment FRAGMENT = null;

    private UserOrderHistoryAdapter adapter;
    private ReqData reqData = new ReqData();
    private List<OrderVo> orderHistoryList = Lists.newArrayList();
    public static int TO_ORDER_DETAIL_INDEX = -1;

    public UserOrderHistoryFragment() {
    }

    public Fragment getInstance(Bundle bundle) {
        if (FRAGMENT == null) {
            FRAGMENT = new UserOrderHistoryFragment();
            TO_ORDER_DETAIL_INDEX = -1;
        }
        if (bundle != null){
            FRAGMENT.setArguments(bundle);
        }
        return FRAGMENT;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new UserOrderHistoryAdapter(orderHistoryList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container.getTag(R.id.user_history_page) == null) {
            View v = inflater.inflate(R.layout.fragment_user_order_history, container, false);
            getViews(v);
            container.setTag(R.id.user_history_page, v);
            return v;
        } else {
            return (View) container.getTag(R.id.user_history_page);
        }
    }

    private void getViews(View v) {

        final BGARefreshLayout bgaRefreshLayout = v.findViewById(R.id.historyBGARefreshLayout);
        RecyclerView recyclerView = v.findViewById(R.id.historyRecyclerView);

        BGANormalRefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(getContext(), true);
        refreshViewHolder.setPullDownRefreshText("Pull");
        refreshViewHolder.setRefreshingText("Pull to refresh");
        refreshViewHolder.setReleaseRefreshText("Pull to refresh");
        refreshViewHolder.setLoadingMoreText("Loading more !");

        bgaRefreshLayout.setRefreshViewHolder(refreshViewHolder);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        //setListener
        adapter.setListener(new ItemOnClickListener());
        recyclerView.setAdapter(adapter);
        bgaRefreshLayout.setDelegate(new BGARefreshLayout.BGARefreshLayoutDelegate() {
            @Override
            public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
                bgaRefreshLayout.endRefreshing();
                reqData.loadingMore = true;
                doLoadData(true);
            }

            @Override
            public boolean
            onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
                bgaRefreshLayout.endLoadingMore();
                if (!reqData.loadingMore) {
                    return false;
                }
                reqData.page ++;
                doLoadData(false);
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        UserMainActivity.changeTabAndToolbarStatus();
        if (TO_ORDER_DETAIL_INDEX >= 0) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(NaberConstant.ORDER_INFO, orderHistoryList.get(TO_ORDER_DETAIL_INDEX));
            UserMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.USER_ORDER_DETAIL, bundle);
        } else {
            doLoadData(true);
        }
    }

    private void doLoadData(boolean isRefresh) {
        if (isRefresh) {
            orderHistoryList.clear();
            reqData.page = 1;
        }
//        reqData.page = 1;
        ApiManager.userOrderHistory(reqData,new ThreadCallback(getContext()) {
            @Override
            public void onSuccess(String responseBody) {
                List<OrderVo> list = Tools.JSONPARSE.fromJsonList(responseBody, OrderVo[].class);
                reqData.loadingMore = list.size() % NaberConstant.PAGE == 0 && list.size() != 0;
                orderHistoryList.addAll(list);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(Exception error, String msg) {

            }
        });
    }

    class ItemOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            TO_ORDER_DETAIL_INDEX = (int) view.getTag();
            Bundle bundle = new Bundle();
            bundle.putSerializable(NaberConstant.ORDER_INFO, orderHistoryList.get(TO_ORDER_DETAIL_INDEX));
            UserMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.USER_ORDER_DETAIL, bundle);
        }
    }
}
