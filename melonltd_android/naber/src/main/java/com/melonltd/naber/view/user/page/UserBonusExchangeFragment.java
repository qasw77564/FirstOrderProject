package com.melonltd.naber.view.user.page;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
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
import com.google.common.collect.Lists;
import com.melonltd.naber.R;
import com.melonltd.naber.model.api.ApiManager;
import com.melonltd.naber.model.api.ThreadCallback;
import com.melonltd.naber.util.Tools;
import com.melonltd.naber.view.common.BaseCore;
import com.melonltd.naber.view.factory.PageType;
import com.melonltd.naber.view.user.UserMainActivity;
import com.melonltd.naber.view.user.adapter.UserBonusExchangeAdapter;
import com.melonltd.naber.vo.ActivitiesVo;
import com.melonltd.naber.vo.OrderDetail;
import com.melonltd.naber.vo.ReqData;

import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserBonusExchangeFragment extends Fragment {
    private static final String TAG = UserBonusExchangeFragment.class.getSimpleName();
    public static UserBonusExchangeFragment FRAGMENT = null;
    private EditText serialChangeEdit;
    private UserBonusExchangeAdapter adapter;
    private List<ActivitiesVo> list = Lists.newArrayList();

    public UserBonusExchangeFragment() {
    }

    public Fragment getInstance(Bundle bundle) {
        if (FRAGMENT == null) {
            FRAGMENT = new UserBonusExchangeFragment();
            FRAGMENT.setArguments(bundle);
        }
        return FRAGMENT;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new UserBonusExchangeAdapter(list);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container.getTag(R.id.user_bonus_exchange_page) == null) {
            View v = inflater.inflate(R.layout.fragment_user_bonus_exchange, container, false);
            getViews(v);
            container.setTag(R.id.user_bonus_exchange_page, v);
            return v;
        }
        return (View) container.getTag(R.id.user_bonus_exchange_page);

    }

    private void getViews (View v){
        final Button sendBtn = v.findViewById(R.id.sendBtn);
        serialChangeEdit = v.findViewById(R.id.serialChangeEdit);
        serialChangeEdit.setFilters(new InputFilter[]{new InputFilter.AllCaps(), new InputFilter.LengthFilter(8)});

        TextView description = v.findViewById(R.id.descriptionText);
        description.setText("凡是透過NABER訂餐，\n一律回饋消費金額之10%紅利點數\n" +
                            "，並能兌換NABER所提供之獎勵。\n\n" +
                            "* 10月起 開放兌換獎勵及現金折抵\n" +
                            "* 消費10元獲得1點紅利點數\n");

        final BGARefreshLayout bgaRefreshLayout = v.findViewById(R.id.bonusExchangeBGARefreshLayout);
        RecyclerView recyclerView = v.findViewById(R.id.bonusExchangeRecyclerView);

        BGANormalRefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(getContext(), true);
        refreshViewHolder.setPullDownRefreshText("Pull");
        refreshViewHolder.setRefreshingText("Pull to refresh");
        refreshViewHolder.setReleaseRefreshText("Pull to refresh");
        refreshViewHolder.setLoadingMoreText("Loading more !");

        bgaRefreshLayout.setRefreshViewHolder(refreshViewHolder);

        bgaRefreshLayout.setDelegate(new BGARefreshLayout.BGARefreshLayoutDelegate() {
            @Override
            public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
                bgaRefreshLayout.endRefreshing();
                doLoadData();
            }

            @Override
            public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
                bgaRefreshLayout.endLoadingMore();
                return false;
            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serialSend(sendBtn);

            }
        });

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        // setListener
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.setOnItemClickListener(new ItemClick());
    }

    private void doLoadData (){
        list.clear();
        adapter.notifyDataSetChanged();


        ApiManager.getAllActivities(new ThreadCallback(getContext()) {
            @Override
            public void onSuccess(String responseBody) {
                List<ActivitiesVo> activitiesList = Tools.JSONPARSE.fromJsonList(responseBody,ActivitiesVo[].class);
                list.addAll(activitiesList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(Exception error, String msg) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        serialChangeEdit.setText("");

        UserMainActivity.changeTabAndToolbarStatus();
        if (UserMainActivity.toolbar != null) {
            UserMainActivity.navigationIconDisplay(true, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UserMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.USER_ACCOUNT, null);
                    UserMainActivity.navigationIconDisplay(false, null);
                }
            });
        }

        this.doLoadData();

    }

    class ItemClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "item tag : " + v.getTag() );
            int index = (int)v.getTag();
            ActivitiesVo data = list.get(index);
            Log.i(TAG, "data "  + data );
            Bundle bundle = new Bundle();
            bundle.putSerializable("BONUS_DETAIL", data);
            UserMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.USER_BONUS_EXCHANGE_DETAIL, bundle);

        }
    }
    private void serialSend(final View v){

        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        if(!Strings.isNullOrEmpty(serialChangeEdit.getText().toString()) && serialChangeEdit.getText().toString().length() == 8){

            final ReqData req = new ReqData();
            req.data = serialChangeEdit.getText().toString().toUpperCase();

            ApiManager.serialSubmit(req, new ThreadCallback(getContext()) {
                @Override
                public void onSuccess(String responseBody) {
                    ActivitiesVo activities =  Tools.JSONPARSE.fromJson(responseBody, ActivitiesVo.class);

                    if (activities.act_category.equals("RES_EVENT")){
                        // 到送單畫面
                        OrderDetail detail = Tools.JSONPARSE.fromJson(activities.data, OrderDetail.class);
                        if (detail != null){
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("ACTIVITIES", activities);
                            bundle.putSerializable("ORDER_DETAIL", detail);
                            UserMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.USER_EXCHANGE_SUBMIT_ORDER, bundle);
                        }else {
                            new AlertView.Builder()
                                    .setContext(getContext())
                                    .setTitle("兌換成功")
                                    .setMessage("該項目已結束兌換。")
                                    .setStyle(AlertView.Style.Alert)
                                    .setOthers(new String[]{"我知道了"})
                                    .setOnItemClickListener(new OnItemClickListener() {
                                        @Override
                                        public void onItemClick(Object o, int position) {
                                            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                                            serialChangeEdit.setText("");
                                        }
                                    })
                                    .build()
                                    .setCancelable(true)
                                    .show();
                        }
                    }else if (activities.act_category.equals("TICKET")) {
                        // Alert 成功充值紅利
                        new AlertView.Builder()
                                .setContext(getContext())
                                .setTitle("兌換成功")
                                .setMessage(activities.data + "紅利兌換成功。")
                                .setStyle(AlertView.Style.Alert)
                                .setOthers(new String[]{"我知道了"})
                                .setOnItemClickListener(new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(Object o, int position) {
                                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                                        serialChangeEdit.setText("");
                                    }
                                })
                                .build()
                                .setCancelable(true)
                                .show();

                    }
                }

                @Override
                public void onFail(Exception error, String msg) {
                    new AlertView.Builder()
                            .setContext(getContext())
                            .setTitle("兌換失敗")
                            .setMessage(msg)
                            .setStyle(AlertView.Style.Alert)
                            .setOthers(new String[]{"我知道了"})
                            .setOnItemClickListener(new OnItemClickListener() {
                                @Override
                                public void onItemClick(Object o, int position) {
                                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                                    serialChangeEdit.setText("");
                                }
                            })
                            .build()
                            .setCancelable(true)
                            .show();
                }
            });


        } else {
            new AlertView.Builder()
                    .setContext(getContext())
                    .setTitle("系統提示")
                    .setMessage("請正確輸入8碼兌換序號")
                    .setStyle(AlertView.Style.Alert)
                    .setOthers(new String[]{"我知道了"})
                    .setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int position) {
                            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            serialChangeEdit.setText("");
                        }
                    })
                    .build()
                    .setCancelable(true)
                    .show();
        }


    }
}
