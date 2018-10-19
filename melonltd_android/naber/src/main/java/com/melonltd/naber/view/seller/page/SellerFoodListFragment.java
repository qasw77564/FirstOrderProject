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
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.common.base.Strings;
import com.melonltd.naber.R;
import com.melonltd.naber.model.api.ApiManager;
import com.melonltd.naber.model.api.ThreadCallback;
import com.melonltd.naber.model.bean.Model;
import com.melonltd.naber.model.constant.NaberConstant;
import com.melonltd.naber.model.type.SwitchStatus;
import com.melonltd.naber.util.IntegerTools;
import com.melonltd.naber.util.Tools;
import com.melonltd.naber.view.customize.SwitchButton;
import com.melonltd.naber.view.factory.PageType;
import com.melonltd.naber.view.seller.SellerMainActivity;
import com.melonltd.naber.view.seller.adapter.SellerFoodAdapter;
import com.melonltd.naber.vo.FoodItemVo;
import com.melonltd.naber.vo.FoodVo;
import com.melonltd.naber.vo.ItemVo;
import com.melonltd.naber.vo.ReqData;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

public class SellerFoodListFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = SellerFoodListFragment.class.getSimpleName();
    public static SellerFoodListFragment FRAGMENT = null;

    private TextView categoryNameText;
    private SellerFoodAdapter adapter;
    private ReqData req;
    public static int TO_MENU_EDIT_PAGE_INDEX = -1;

    public SellerFoodListFragment() {
    }

    public Fragment getInstance(Bundle bundle) {
        if (FRAGMENT == null) {
            FRAGMENT = new SellerFoodListFragment();
            TO_MENU_EDIT_PAGE_INDEX = -1;
        }
        if (bundle != null) {
            FRAGMENT.setArguments(bundle);
        }
        return FRAGMENT;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new SellerFoodAdapter(new SwitchListener(), new DeleteListener(), new EditListener());
        Fresco.initialize(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container.getTag(R.id.seller_restaurant_food_list_page) == null) {
            View v = inflater.inflate(R.layout.fragment_seller_food_list, container, false);
            getViews(v);
            container.setTag(R.id.seller_restaurant_food_list_page, v);
            return v;
        }
        return (View) container.getTag(R.id.seller_restaurant_food_list_page);
    }


    private void getViews(View v) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
        categoryNameText = v.findViewById(R.id.categoryNameText);
        Button newMenuBtn = v.findViewById(R.id.newMenuBtn);

        BGARefreshLayout refreshLayout = v.findViewById(R.id.menuBGARefreshLayout);
        BGANormalRefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(getContext(), true);
        refreshViewHolder.setPullDownRefreshText("Pull");
        refreshViewHolder.setRefreshingText("Pull to refresh");
        refreshViewHolder.setReleaseRefreshText("Pull to refresh");
        refreshViewHolder.setLoadingMoreText("Loading more !");
        refreshLayout.setRefreshViewHolder(refreshViewHolder);

        RecyclerView recyclerView = v.findViewById(R.id.menuRecyclerView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        // setListener
        recyclerView.setAdapter(adapter);
        newMenuBtn.setOnClickListener(this);
        refreshLayout.setDelegate(new BGARefreshLayout.BGARefreshLayoutDelegate() {
            @Override
            public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
                refreshLayout.endRefreshing();
                loadData();
            }

            @Override
            public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
                refreshLayout.endLoadingMore();
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        SellerMainActivity.changeTabAndToolbarStatus();

        if (TO_MENU_EDIT_PAGE_INDEX >= 0) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(NaberConstant.SELLER_FOOD_INFO, Model.SELLER_FOOD_LIST.get(TO_MENU_EDIT_PAGE_INDEX));
            SellerMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.SELLER_FOOD_EDIT, bundle);
        } else {
            req = new ReqData();
            req.name = getArguments().getString(NaberConstant.SELLER_CATEGORY_NAME);
            req.uuid = getArguments().getString(NaberConstant.SELLER_CATEGORY_UUID);
            categoryNameText.setText(req.name);
            loadData();
        }

        if (SellerMainActivity.toolbar != null) {
            SellerMainActivity.navigationIconDisplay(true, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SellerCategoryListFragment.TO_CATEGORY_LIST_PAGE_INDEX = -1;
                    SellerMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.SELLER_CATEGORY_LIST, null);
                    SellerMainActivity.navigationIconDisplay(false, null);
                }
            });
        }
        if (SellerMainActivity.sortBtn != null) {
            SellerMainActivity.sortBtn.setVisibility(View.VISIBLE);
            SellerMainActivity.sortBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SellerMainActivity.sortBtn.getText().equals("編輯排序")){
                        adapter.setSortEdit(true).notifyDataSetChanged();
                        SellerMainActivity.sortBtn.setText("儲存排序");
                    }else {
                        SellerMainActivity.sortBtn.setText("編輯排序");
                        adapter.setSortEdit(false);

                        new AlertView.Builder()
                                .setTitle("")
                                .setMessage("確認排序結果")
                                .setContext(getContext())
                                .setStyle(AlertView.Style.Alert)
                                .setOthers(new String[]{"取消", "確定"})
                                .setOnItemClickListener(new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(Object o, int position) {
                                        if (position == 0) {
                                            loadData();
                                        } else if(position == 1){
                                            ApiManager.sellerFoodSort(Model.SELLER_FOOD_LIST, new ThreadCallback(getContext()) {
                                                @Override
                                                public void onSuccess(String responseBody) {
                                                    Model.SELLER_FOOD_LIST.clear();
                                                    adapter.notifyDataSetChanged();
                                                    List<FoodVo> foodVo = Tools.JSONPARSE.fromJsonList(responseBody,FoodVo[].class);
                                                    Collections.sort(foodVo, new Comparator<FoodVo>() {
                                                        public int compare(FoodVo o1, FoodVo o2) {
                                                            return IntegerTools.parseInt(o1.top,0) - IntegerTools.parseInt(o2.top,0);
                                                        }
                                                    });
                                                    Model.SELLER_FOOD_LIST.addAll(foodVo);
                                                    adapter.notifyDataSetChanged();
                                                }
                                                @Override
                                                public void onFail(Exception error, String msg) {

                                                }
                                            });
                                        }
                                    }
                                })
                                .build()
                                .setCancelable(false)
                                .show();
                    }
                }
            });
        }
    }

    private void loadData() {
        Model.SELLER_FOOD_LIST.clear();
        adapter.notifyDataSetChanged();
        ApiManager.sellerFoodList(req, new ThreadCallback(getContext()) {
            @Override
            public void onSuccess(String responseBody) {
                List<FoodVo> foodVos =   Tools.JSONPARSE.fromJsonList(responseBody, FoodVo[].class);
                Collections.sort(foodVos, new Comparator<FoodVo>() {
                    public int compare(FoodVo o1, FoodVo o2) {
                        return IntegerTools.parseInt(o1.top,0) - IntegerTools.parseInt(o2.top,0);
                    }
                });

                Model.SELLER_FOOD_LIST.addAll(foodVos);
                adapter.setSortEdit(false).notifyDataSetChanged();
            }

            @Override
            public void onFail(Exception error, String msg) {

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        SellerMainActivity.navigationIconDisplay(false, null);
        if (SellerMainActivity.sortBtn != null) {
            adapter.setSortEdit(false);
            SellerMainActivity.sortBtn.setVisibility(View.GONE);
            SellerMainActivity.sortBtn.setText("編輯排序");
        }
    }

    @Override
    public void onClick(final View view) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        final FoodAddEdit extView = new FoodAddEdit();
        new AlertView.Builder()
                .setContext(getContext())
                .setStyle(AlertView.Style.Alert)
                .setOthers(new String[]{"確定新增", "取消"})
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, int position) {
                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        if (position == 0) {
                            if (!Strings.isNullOrEmpty(extView.getName()) && !Strings.isNullOrEmpty(extView.getPrice())) {
                                if (Integer.parseInt(extView.getPrice()) <= 0){
                                    Toast.makeText(getContext(), getContext().getString(R.string.app_name) + "提醒，新增失敗，請注意品項價格不可為0!!", Toast.LENGTH_LONG).show();
                                }else{
                                    new Handler().postDelayed(new FoodAddRun(req.uuid, extView.getName(), extView.getPrice()), 300);
                                }
                            }else {
                                Toast.makeText(getContext(), getContext().getString(R.string.app_name) + "提醒，新增失敗，請注意品項名稱與價格不可空白，且價格不可為0!!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                })
                .build()
                .addExtView(extView.getView())
                .setCancelable(false)
                .show();
    }

    class SwitchListener implements SwitchButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(final SwitchButton view, boolean isChecked) {
            final int index = (int) view.getTag();
            final SwitchStatus status = SwitchStatus.of(isChecked);
            if (!status.equals(Model.SELLER_FOOD_LIST.get(index).status)) {
                FoodVo req = Model.SELLER_FOOD_LIST.get(index);
                req.status = status;
                ApiManager.sellerFoodUpdate(req, new ThreadCallback(getContext()) {
                    @Override
                    public void onSuccess(String responseBody) {
                        Model.SELLER_FOOD_LIST.get(index).status = status;
                    }

                    @Override
                    public void onFail(Exception error, String msg) {
                        view.setChecked(Model.SELLER_FOOD_LIST.get(index).status.getStatus());
                    }
                });
            }

        }
    }

    class DeleteListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            final int index = (int) view.getTag();
            new AlertView.Builder()
                    .setTitle("")
                    .setMessage("刪除後將無法找回，\n您確定要刪除嗎？")
                    .setContext(getContext())
                    .setStyle(AlertView.Style.Alert)
                    .setOthers(new String[]{"確定刪除", "取消"})
                    .setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int position) {
                            if (position == 0) {
                                new Handler().postDelayed(new FoodDeleteRun(index), 300);
                            }
                        }
                    })
                    .build()
                    .setCancelable(true)
                    .show();
        }
    }

    class FoodAddRun implements Runnable {
        private String category_uuid;
        private String name;
        private String price;

        FoodAddRun(String category_uuid, String name, String price) {
            this.category_uuid = category_uuid;
            this.name = name;
            this.price = price;
        }

        @Override
        public void run() {
            FoodVo req = new FoodVo();
            req.category_uuid = this.category_uuid;
            req.food_name = this.name;
            req.default_price = this.price;
            req.food_data = new FoodItemVo();
            ItemVo item = new ItemVo();
            item.name = "統一規格";
            item.price = this.price;
            req.food_data.scopes.add(item);

            ApiManager.sellerFoodAdd(req, new ThreadCallback(getContext()) {
                @Override
                public void onSuccess(String responseBody) {
                    FoodVo vo = Tools.JSONPARSE.fromJson(responseBody, FoodVo.class);
                    Model.SELLER_FOOD_LIST.add(0, vo);
                    TO_MENU_EDIT_PAGE_INDEX = 0;
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(NaberConstant.SELLER_FOOD_INFO, Model.SELLER_FOOD_LIST.get(0));
                    SellerMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.SELLER_FOOD_EDIT, bundle);
                }

                @Override
                public void onFail(Exception error, String msg) {

                }
            });
        }
    }

    class FoodDeleteRun implements Runnable {
        private int index;

        FoodDeleteRun(int index) {
            this.index = index;
        }

        @Override
        public void run() {
            ReqData req = new ReqData();
            req.uuid = Model.SELLER_FOOD_LIST.get(index).food_uuid;
            ApiManager.sellerFoodDelete(req, new ThreadCallback(getContext()) {
                @Override
                public void onSuccess(String responseBody) {
                    Model.SELLER_FOOD_LIST.remove(index);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onFail(Exception error, String msg) {
                }
            });
        }
    }

    class EditListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            final int index = (int) view.getTag();
            TO_MENU_EDIT_PAGE_INDEX = index;
            Bundle bundle = new Bundle();
            bundle.putSerializable(NaberConstant.SELLER_FOOD_INFO, Model.SELLER_FOOD_LIST.get(index));
            SellerMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.SELLER_FOOD_EDIT, bundle);
        }
    }

    class FoodAddEdit implements View.OnFocusChangeListener {
        private View v;
        private EditText nameEdit, priceEdit;

        FoodAddEdit() {
            this.v = getLayoutInflater().inflate(R.layout.seller_add_food_edit, null);
            this.nameEdit = this.v.findViewById(R.id.nameEdit);
            this.priceEdit = this.v.findViewById(R.id.priceEdit);
            this.nameEdit.setOnFocusChangeListener(this);
            this.priceEdit.setOnFocusChangeListener(this);
        }

        public View getView() {
            return this.v;
        }

        public String getName() {
            return this.nameEdit.getText().toString();
        }

        public String getPrice() {
            return this.priceEdit.getText().toString();
        }

        @Override
        public void onFocusChange(View view, boolean b) {
            if (!b) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

}
