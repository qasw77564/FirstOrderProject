package com.melonltd.naber.view.seller.page;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.melonltd.naber.R;
import com.melonltd.naber.model.api.ApiManager;
import com.melonltd.naber.model.api.ThreadCallback;
import com.melonltd.naber.model.bean.Model;
import com.melonltd.naber.model.type.OrderStatus;
import com.melonltd.naber.util.Tools;
import com.melonltd.naber.view.seller.SellerMainActivity;
import com.melonltd.naber.view.seller.adapter.SellerQuickSearchAdapter;
import com.melonltd.naber.vo.OrderDetail;
import com.melonltd.naber.vo.OrderVo;
import com.melonltd.naber.vo.ReqData;

import java.util.List;
import java.util.Map;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

public class SellerSearchFragment extends Fragment implements View.OnClickListener {
//    private static final String TAG = SellerSearchFragment.class.getSimpleName();
    public static SellerSearchFragment FRAGMENT = null;

    private EditText phoneEditText;
    private SellerQuickSearchAdapter adapter;
    private Button phoneSearchBtn;

    public SellerSearchFragment() {
    }

    public Fragment getInstance(Bundle bundle) {
        if (FRAGMENT == null) {
            FRAGMENT = new SellerSearchFragment();
            FRAGMENT.setArguments(bundle);
        }
        return FRAGMENT;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new SellerQuickSearchAdapter(new StatusChangeClickListener(), new FailureListener(), new CancelListener());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container.getTag(R.id.seller_search_main_page) == null) {
            View v = inflater.inflate(R.layout.fragment_seller_search, container, false);
            getViews(v);
            container.setTag(R.id.seller_search_main_page, v);
            return v;
        }
        return (View) container.getTag(R.id.seller_search_main_page);
    }

    @Override
    public void onResume() {
        super.onResume();
        SellerMainActivity.lockDrawer(true);
        SellerMainActivity.changeTabAndToolbarStatus();
    }

    private void getViews(View v) {
        phoneEditText = v.findViewById(R.id.phoneEditText);
        phoneSearchBtn = v.findViewById(R.id.phoneSearchBtn);
        final BGARefreshLayout searchRefreshLayout = v.findViewById(R.id.sellerSearchRefreshLayout);
        BGANormalRefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(getContext(), true);
        refreshViewHolder.setPullDownRefreshText("Pull");
        refreshViewHolder.setRefreshingText("Pull to refresh");
        refreshViewHolder.setReleaseRefreshText("Pull to refresh");
        refreshViewHolder.setLoadingMoreText("加載");

        searchRefreshLayout.setRefreshViewHolder(refreshViewHolder);
        RecyclerView recyclerView = v.findViewById(R.id.searchRecyclerView);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        // setListener
        phoneSearchBtn.setOnClickListener(this);
        v.setOnClickListener(this);
        recyclerView.setOnClickListener(this);
        recyclerView.setAdapter(adapter);
        searchRefreshLayout.setDelegate(new BGARefreshLayout.BGARefreshLayoutDelegate() {
            @Override
            public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
                searchRefreshLayout.endRefreshing();
                phoneSearchBtn.callOnClick();
            }

            @Override
            public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
                searchRefreshLayout.endLoadingMore();
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        if (view.getId() == R.id.phoneSearchBtn) {
            loadData();
        }
    }

    private void loadData() {
        Model.SELLER_QUICK_SEARCH_ORDERS.clear();
        adapter.notifyDataSetChanged();
        if (phoneEditText.getText().toString().length() < 4) {
            new AlertView.Builder()
                    .setTitle("")
                    .setMessage("輸入號碼未滿4位數，請正確輸入。")
                    .setContext(getContext())
                    .setStyle(AlertView.Style.Alert)
                    .setCancelText("確定")
                    .build()
                    .setCancelable(true)
                    .show();

        } else {
            Map<String, String> req = Maps.newHashMap();
            req.put("phone", phoneEditText.getText().toString());
            ApiManager.sellerQuickSearch(req, new ThreadCallback(getContext()) {
                @Override
                public void onSuccess(String responseBody) {
                    List<OrderVo> list = Tools.JSONPARSE.fromJsonList(responseBody, OrderVo[].class);
                    if (list.size() == 0) {
                        new AlertView.Builder()
                                .setTitle("")
                                .setMessage("目前查無此訂單，\n請確認取餐時間，\n或手機號碼是否正確！！")
                                .setContext(getContext())
                                .setStyle(AlertView.Style.Alert)
                                .setCancelText("確定")
                                .build()
                                .setCancelable(true)
                                .show();
                    } else {
                        for (int i = 0; i < list.size(); i++) {
                            list.get(i).order_detail = Tools.JSONPARSE.fromJson(list.get(i).order_data, OrderDetail.class);
                        }
                        Model.SELLER_QUICK_SEARCH_ORDERS.addAll(list);
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFail(Exception error, String msg) {

                }
            });
        }
    }

    class CancelListener implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            final int index = (int) v.getTag();
            final ReqData req = new ReqData();
            req.uuid = Model.SELLER_QUICK_SEARCH_ORDERS.get(index).order_uuid;
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
                                ApiManager.sellerChangeOrder(req, new ThreadCallback(getContext()) {
                                    @Override
                                    public void onSuccess(final String responseBody) {
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
//                                                Log.i(TAG, responseBody);
                                                phoneSearchBtn.callOnClick();
                                                adapter.notifyDataSetChanged();
                                            }
                                        } ,500);

                                    }
                                    @Override
                                    public void onFail(Exception error, String msg) {

                                    }
                                });
                            }
                        }
                    })
                    .build()
                    .addExtView(extView.getView())
                    .setCancelable(true)
                    .show();
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
                    req.type = OrderStatus.PROCESSING.name();
                    alertMsg = "確認開始製作此訂單";
                    break;
                case R.id.canFetchBtn:
                    req.type = OrderStatus.CAN_FETCH.name();
                    alertMsg = "確認此訂單已可領取";
                    break;
                case R.id.finishBtn:
                    req.type = OrderStatus.FINISH.name();
                    alertMsg = "確認此訂單已交易完成";
                    break;
            }

            req.uuid = Model.SELLER_QUICK_SEARCH_ORDERS.get(index).order_uuid;

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
                                new Handler().postDelayed(new StatusChangeRun(req), 300);
                            }
                        }
                    })
                    .build()
                    .setCancelable(true)
                    .show();
        }
    }

    class StatusChangeRun implements Runnable{
        private ReqData req;
        StatusChangeRun(ReqData req){
            this.req = req;
        }
        @Override
        public void run() {
            ApiManager.sellerChangeOrder(req, new ThreadCallback(getContext()) {
                @Override
                public void onSuccess(String responseBody) {
//                    Log.i(TAG, responseBody);
                    phoneSearchBtn.callOnClick();
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onFail(Exception error, String msg) {

                }
            });
        }
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

    class FailureListener implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            final int index = (int) v.getTag();
            final ReqData req = new ReqData();
            req.uuid = Model.SELLER_QUICK_SEARCH_ORDERS.get(index).order_uuid;
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
                                ApiManager.sellerChangeOrder(req, new ThreadCallback(getContext()) {
                                    @Override
                                    public void onSuccess(final String responseBody) {
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
//                                                Log.i(TAG, responseBody);
                                                phoneSearchBtn.callOnClick();
                                                adapter.notifyDataSetChanged();
                                            }
                                        } ,500);

                                    }
                                    @Override
                                    public void onFail(Exception error, String msg) {

                                    }
                                });
                            }
                        }
                    })
                    .build()
                    .setCancelable(true)
                    .show();
        }
    }

}
