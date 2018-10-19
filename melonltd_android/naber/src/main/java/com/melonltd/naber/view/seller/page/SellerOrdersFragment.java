package com.melonltd.naber.view.seller.page;


import android.content.Context;
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
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.melonltd.naber.R;
import com.melonltd.naber.model.api.ApiManager;
import com.melonltd.naber.model.api.ThreadCallback;
import com.melonltd.naber.model.bean.Model;
import com.melonltd.naber.model.constant.NaberConstant;
import com.melonltd.naber.model.type.Delivery;
import com.melonltd.naber.model.type.OrderStatus;
import com.melonltd.naber.util.Tools;
import com.melonltd.naber.view.seller.SellerMainActivity;
import com.melonltd.naber.view.seller.adapter.SellerOrdersAdapter;
import com.melonltd.naber.vo.OrderDetail;
import com.melonltd.naber.vo.OrderVo;
import com.melonltd.naber.vo.ReqData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

import static com.melonltd.naber.model.type.OrderStatus.CAN_FETCH;
import static com.melonltd.naber.model.type.OrderStatus.FINISH;
import static com.melonltd.naber.model.type.OrderStatus.LIVE;
import static com.melonltd.naber.model.type.OrderStatus.PROCESSING;
import static com.melonltd.naber.model.type.OrderStatus.UNFINISH;


public class SellerOrdersFragment extends Fragment {
        private static final String TAG = SellerOrdersFragment.class.getSimpleName();
    public static SellerOrdersFragment FRAGMENT = null;
    private TextView searchDateText;
    private TextView liveText, untreatedText, processingText, canFetchText;
    private ReqData unReq = new ReqData(), prReq = new ReqData(), canReq = new ReqData();
    private static OrderStatus STATUS_TAG = OrderStatus.LIVE;
    private static SellerOrdersAdapter adapter;
    private static Handler handler;
    private static LiveOrderRun liveOrderRun;
    private static Context context;

    public SellerOrdersFragment() {

    }

    public Fragment getInstance(Bundle bundle) {
        if (FRAGMENT == null) {
            FRAGMENT = new SellerOrdersFragment();
        }
        if (bundle != null) {
            FRAGMENT.setArguments(bundle);
        }
        return FRAGMENT;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        unReq.search_type = OrderStatus.UNFINISH.name();
        prReq.search_type = OrderStatus.PROCESSING.name();
        canReq.search_type = OrderStatus.CAN_FETCH.name();
//        handler = new Handler();
        this.context = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SellerMainActivity.navigationIconDisplay(false, null);
        if (container.getTag(R.id.seller_orders_main_page) == null) {
            View v = inflater.inflate(R.layout.fragment_seller_orders, container, false);
            getViews(v);
            container.setTag(R.id.seller_orders_main_page, v);
            return v;
        }
        return (View) container.getTag(R.id.seller_orders_main_page);
    }

    @Override
    public void onResume() {
        super.onResume();
        SellerMainActivity.changeTabAndToolbarStatus();
        SellerMainActivity.lockDrawer(false);

        SellerMainActivity.notifyDateRange();

        if (OrderStatus.LIVE.equals(STATUS_TAG)){
            loadLiveData();
        }else {
            loadData(true);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (handler != null){
            handler.removeCallbacks(liveOrderRun);
        }
    }

    private void getViews(View v) {
        BGARefreshLayout refreshLayout = v.findViewById(R.id.ordersBGARefreshLayout);
        BGANormalRefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(getContext(), true);
        refreshViewHolder.setPullDownRefreshText("Pull");
        refreshViewHolder.setRefreshingText("Pull to refresh");
        refreshViewHolder.setReleaseRefreshText("Pull to refresh");
        refreshViewHolder.setLoadingMoreText("Loading more !");

        refreshLayout.setRefreshViewHolder(refreshViewHolder);
        searchDateText = v.findViewById(R.id.searchDateText);

        liveText = v.findViewById(R.id.liveText);
        untreatedText = v.findViewById(R.id.untreatedText);
        processingText = v.findViewById(R.id.processingText);
        canFetchText = v.findViewById(R.id.canFetchText);
        RecyclerView recyclerView = v.findViewById(R.id.ordersRecyclerView);

        // setListener
        searchDateText.setOnClickListener(new SelectDateListener());
        TabClickListener tabClickListener = new TabClickListener();

        liveText.setTag(LIVE);
        untreatedText.setTag(UNFINISH);
        processingText.setTag(PROCESSING);
        canFetchText.setTag(CAN_FETCH);
        liveText.setOnClickListener(tabClickListener);
        untreatedText.setOnClickListener(tabClickListener);
        processingText.setOnClickListener(tabClickListener);
        canFetchText.setOnClickListener(tabClickListener);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new SellerOrdersAdapter(new CancelListener(), new FailureListener(), new StatusChangeClickListener());
        recyclerView.setAdapter(adapter);
        Calendar now = Calendar.getInstance();
        searchDateText.setVisibility(View.GONE);
        searchDateText.setText(new SimpleDateFormat("yyyy-MM-dd").format(now.getTime()));
        searchDateText.setTag(Tools.FORMAT.formatDate(now.getTime()));

        refreshLayout.setDelegate(new BGARefreshLayout.BGARefreshLayoutDelegate() {
            @Override
            public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
                refreshLayout.endRefreshing();

                if (OrderStatus.LIVE.equals(STATUS_TAG)){
                    loadLiveData();
                }else {
                    loadData(true);
                }
            }

            @Override
            public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
                refreshLayout.endLoadingMore();

                boolean loadingMore = false;
                switch (STATUS_TAG) {
                    case UNFINISH:
                        loadingMore = unReq.loadingMore;
                        break;
                    case PROCESSING:
                        loadingMore = prReq.loadingMore;
                        break;
                    case CAN_FETCH:
                        loadingMore = canReq.loadingMore;
                        break;
                }
                if (loadingMore) {
                    loadData(false);
                }

                return false;
            }
        });
    }

    class SelectDateListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
//            if (orderListRun != null) {
//                handler.removeCallbacks(orderListRun);
//            }
            if (OrderStatus.LIVE.equals(STATUS_TAG)){

            }else {
                long date = 1000 * 60 * 60 * 24 * 2L;
                long dayOne = 1000 * 60 * 60 * 24 * 1L;

                Calendar now = Calendar.getInstance();
                Calendar startDate = Calendar.getInstance();
                startDate.setTimeInMillis(now.getTime().getTime() - dayOne);
                Calendar endDate = Calendar.getInstance();
                endDate.setTimeInMillis(now.getTime().getTime() + date);

                new TimePickerBuilder(getContext(),
                        new OnTimeSelectListener() {
                            @Override
                            public void onTimeSelect(Date date, View v) {
                                searchDateText.setTag(Tools.FORMAT.formatDate(date));
                                searchDateText.setText(new SimpleDateFormat("yyyy-MM-dd").format(date));
                                loadData(true);
                            }
                        })
                        .setType(new boolean[]{true, true, true, false, false, false})
                        .setTitleSize(20)
                        .setOutSideCancelable(true)
                        .isCyclic(false)
                        .setTitleBgColor(getResources().getColor(R.color.naber_dividing_line_gray))
                        .setCancelColor(getResources().getColor(R.color.naber_dividing_gray))
                        .setSubmitColor(getResources().getColor(R.color.naber_dividing_gray))
                        .setDate(now)
                        .setRangDate(startDate, endDate)
                        .isCenterLabel(false)
                        .isDialog(false)
                        .build()
                        .show();
            }

        }
    }

    class StatusChangeClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final int index = (int) v.getTag();
            final ReqData req = new ReqData();

            String alertMsg = "";
            switch (v.getId()) {
                case R.id.processingBtn:
                    req.type = PROCESSING.name();
                    alertMsg = "確認開始製作此訂單";
                    break;
                case R.id.canFetchBtn:
                    req.type = CAN_FETCH.name();
                    alertMsg = "確認此訂單已可領取";
                    break;
                case R.id.finishBtn:
                    req.type = FINISH.name();
                    alertMsg = "確認此訂單已交易完成";
                    break;
            }
            req.uuid = Model.SELLER_TMP_ORDERS_LIST.get(index).order_uuid;

            new AlertView.Builder()
                    .setContext(getContext())
                    .setStyle(AlertView.Style.Alert)
                    .setTitle("")
                    .setMessage(alertMsg)
                    .setOthers(new String[]{"返回", "確定"})
                    .setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int position) {
                            if (position == 1) {
                                new Handler().postDelayed(new StatusChangeRun(index, req), 300);
                            }
                        }
                    })
                    .build()
                    .setCancelable(true)
                    .show();
        }
    }


    class StatusChangeRun implements Runnable {
        private ReqData req;
        private int index;

        StatusChangeRun(int index, ReqData req) {
            this.index = index;
            this.req = req;
        }

        @Override
        public void run() {
//            req.uuid = Model.SELLER_TMP_ORDERS_LIST.get(index).order_uuid;
            sellerChangeOrder(this.req, this.index);
        }
    }

    private void sellerChangeOrder(ReqData req, final int index) {
        if (handler != null){
            handler.removeCallbacks(liveOrderRun);
        }
        ApiManager.sellerChangeOrder(req, new ThreadCallback(getContext()) {
            @Override
            public void onSuccess(String responseBody) {
                Model.SELLER_TMP_ORDERS_LIST.remove(index);
                switch (STATUS_TAG) {
                    case UNFINISH:
                        Model.SELLER_UNFINISH_ORDERS_LIST.remove(index);
                        adapter.notifyDataSetChanged();
                        break;
                    case PROCESSING:
                        Model.SELLER_PROCESSING_ORDERS_LIST.remove(index);
                        adapter.notifyDataSetChanged();
                        break;
                    case CAN_FETCH:
                        Model.SELLER_CAN_FETCH_ORDERS_LIST.remove(index);
                        adapter.notifyDataSetChanged();
                        break;
                    case LIVE:
                        adapter.notifyDataSetChanged();
                        handler.post(liveOrderRun);
                        break;
                }
            }

            @Override
            public void onFail(Exception error, String msg) {
                if (OrderStatus.LIVE.equals(STATUS_TAG)){
                    if (handler != null){
                        handler.removeCallbacks(liveOrderRun);
                    }
                    handler.post(liveOrderRun);
                }
            }
        });
    }

    class CancelListenerView implements View.OnClickListener {
        private View view;
        private TextView defText1;
        private TextView defText2;
        private EditText customEdit;
        private String msg = "";

        CancelListenerView() {
            this.view = getLayoutInflater().inflate(R.layout.seller_cancel_order_notifiy_to_user_message, null);
            this.defText1 = view.findViewById(R.id.nameEdit);
            this.defText2 = view.findViewById(R.id.priceEdit);
            this.customEdit = view.findViewById(R.id.customEdit);
            // 先帶入預設一
            this.msg = defText1.getText().toString();
            setListener();
        }

        private void setListener() {
            this.defText1.setOnClickListener(CancelListenerView.this);
            this.defText2.setOnClickListener(CancelListenerView.this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.nameEdit:
                    v.setBackgroundResource(R.drawable.naber_reverse_orange_button_style);
                    this.defText2.setBackgroundResource(R.drawable.naber_reverse_gary_button_style);
                    this.msg = defText1.getText().toString();
                    break;
                case R.id.priceEdit:
                    v.setBackgroundResource(R.drawable.naber_reverse_orange_button_style);
                    this.defText1.setBackgroundResource(R.drawable.naber_reverse_gary_button_style);
                    this.msg = defText2.getText().toString();
                    break;
            }
        }

        private View getView() {
            return this.view;
        }

        private String getMessage() {
            if (!Strings.isNullOrEmpty(customEdit.getText().toString()) && customEdit.getText().toString().length() > 0) {
                return customEdit.getText().toString();
            }
            return this.msg;
        }
    }

    class CancelListener implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            final int index = (int) v.getTag();
            final ReqData req = new ReqData();
            req.uuid = Model.SELLER_TMP_ORDERS_LIST.get(index).order_uuid;
            req.type = OrderStatus.CANCEL.name();
            final CancelListenerView extView = new CancelListenerView();
            new AlertView.Builder()
                    .setContext(getContext())
                    .setStyle(AlertView.Style.Alert)
                    .setOthers(new String[]{"返回", "送出"})
                    .setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int position) {
                            if (position == 1) {
                                req.message = extView.getMessage();
                                sellerChangeOrder(req, index);
                            }
                        }
                    })
                    .build()
                    .addExtView(extView.getView())
                    .setCancelable(true)
                    .show();
        }
    }


    class FailureListener implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            final int index = (int) v.getTag();
            final ReqData req = new ReqData();
            req.uuid = Model.SELLER_TMP_ORDERS_LIST.get(index).order_uuid;
            req.type = OrderStatus.FAIL.name();
            final String msg = "確定客戶跑單嗎？\n會影響客戶點餐的權益以及紅利點數";
            new AlertView.Builder()
                    .setContext(getContext())
                    .setStyle(AlertView.Style.Alert)
                    .setTitle("")
                    .setMessage(msg)
                    .setOthers(new String[]{"返回", "送出"})
                    .setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int position) {
                            if (position == 1) {
                                req.message = "你的商品超過時間未領取，記點一次，請注意往後的訂餐權利！";
                                sellerChangeOrder(req, index);
                            }
                        }
                    })
                    .build()
                    .setCancelable(true)
                    .show();
        }
    }


    public static void loadLiveData() {
        if (OrderStatus.LIVE.equals(STATUS_TAG)){
            if (handler == null) {
                handler = new Handler();
            }

            if (liveOrderRun == null) {
                liveOrderRun = new LiveOrderRun(context);
            }

            if (handler != null && liveOrderRun!= null){
                handler.removeCallbacks(liveOrderRun);
            }

            Model.SELLER_TMP_ORDERS_LIST.clear();
            handler.post(liveOrderRun);
        }
    }


    static class LiveOrderRun implements Runnable {
        private Context context;
        LiveOrderRun (Context context){
            this.context = context;
        }
        @Override
        public void run() {
            Model.SELLER_TMP_ORDERS_LIST.clear();
            adapter.notifyDataSetChanged();
            ApiManager.sellerOrderLive(new ThreadCallback(this.context) {
                @Override
                public void onSuccess(String responseBody) {
                    List<OrderVo> list = Tools.JSONPARSE.fromJsonList(responseBody, OrderVo[].class);
                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).order_detail = Tools.JSONPARSE.fromJson(list.get(i).order_data, OrderDetail.class);
                    }
                    Model.SELLER_TMP_ORDERS_LIST.addAll(list);
                    adapter.notifyDataSetChanged();
                    handler.postDelayed(liveOrderRun, NaberConstant.SELLER_LIVE_ORDER_REFRESH_TIMER);
                }

                @Override
                public void onFail(Exception error, String msg) {
                    handler.postDelayed(liveOrderRun, NaberConstant.SELLER_LIVE_ORDER_REFRESH_TIMER);
                }
            });
        }
    }

    private void loadData(boolean isRefresh) {
        if (isRefresh) {
            Model.SELLER_TMP_ORDERS_LIST.clear();
            switch (STATUS_TAG) {
                case UNFINISH:
                    unReq.page = 0;
                    Model.SELLER_UNFINISH_ORDERS_LIST.clear();
                    break;
                case PROCESSING:
                    prReq.page = 0;
                    Model.SELLER_PROCESSING_ORDERS_LIST.clear();
                    break;
                case CAN_FETCH:
                    canReq.page = 0;
                    Model.SELLER_CAN_FETCH_ORDERS_LIST.clear();
                    break;
            }
        }

        ReqData req = new ReqData();
        switch (STATUS_TAG) {
            case UNFINISH:
                unReq.page++;
                req = unReq;
                break;
            case PROCESSING:
                prReq.page++;
                req = prReq;
                break;
            case CAN_FETCH:
                canReq.page++;
                req = canReq;
                break;
        }
        req.date = (String) searchDateText.getTag();
        loadDataNotPost(req);
    }

    private void loadDataNotPost(final ReqData req) {
        ApiManager.sellerOrderList(req, new ThreadCallback(getContext()) {
            @Override
            public void onSuccess(String responseBody) {
                Model.SELLER_TMP_ORDERS_LIST.clear();
                List<OrderVo> list = Tools.JSONPARSE.fromJsonList(responseBody, OrderVo[].class);
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).order_detail = Tools.JSONPARSE.fromJson(list.get(i).order_data, OrderDetail.class);
                }

                boolean loadingMore = list.size() % NaberConstant.PAGE == 0 && list.size() != 0;
                switch (STATUS_TAG) {
                    case UNFINISH:
                        unReq.loadingMore = loadingMore;
                        Model.SELLER_UNFINISH_ORDERS_LIST.addAll(list);
                        Model.SELLER_TMP_ORDERS_LIST.addAll(Model.SELLER_UNFINISH_ORDERS_LIST);
                        break;
                    case PROCESSING:
                        prReq.loadingMore = loadingMore;
                        Model.SELLER_PROCESSING_ORDERS_LIST.addAll(list);
                        Model.SELLER_TMP_ORDERS_LIST.addAll(Model.SELLER_PROCESSING_ORDERS_LIST);
                        break;
                    case CAN_FETCH:
                        canReq.loadingMore = loadingMore;
                        Model.SELLER_CAN_FETCH_ORDERS_LIST.addAll(list);
                        Model.SELLER_TMP_ORDERS_LIST.addAll(Model.SELLER_CAN_FETCH_ORDERS_LIST);
                        break;
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(Exception error, String msg) {

            }
        });
    }

    class TabClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (handler != null){
                handler.removeCallbacks(liveOrderRun);
            }

            OrderStatus status = (OrderStatus) view.getTag();
            if (STATUS_TAG.equals(status)) {
                return;
            }
            STATUS_TAG = status;
            changeTab((TextView) view, status);
        }

        private void changeTab(TextView textView, OrderStatus status) {
            List<TextView> views = Lists.newArrayList(liveText, untreatedText, processingText, canFetchText);
            for (TextView tv : views) {
                tv.setTextColor(getResources().getColor(android.R.color.black));
                tv.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                if (tv.equals(textView)) {
                    tv.setBackgroundColor(getResources().getColor(R.color.naber_basis));
                    tv.setTextColor(getResources().getColor(android.R.color.white));
                }
            }
            if (OrderStatus.LIVE.equals(STATUS_TAG)) {
                loadLiveData();
                searchDateText.setVisibility(View.GONE);
            } else {
                searchDateText.setVisibility(View.VISIBLE);
                loadData(true);
            }
        }
    }

}
