package com.melonltd.naber.view.seller;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.melonltd.naber.R;
import com.melonltd.naber.model.api.ApiManager;
import com.melonltd.naber.model.api.ThreadCallback;
import com.melonltd.naber.model.bean.Model;
import com.melonltd.naber.model.type.SwitchStatus;
import com.melonltd.naber.util.Tools;
import com.melonltd.naber.view.common.BaseCore;
import com.melonltd.naber.view.customize.NaberTab;
import com.melonltd.naber.view.customize.SwitchButton;
import com.melonltd.naber.view.factory.PageFragmentFactory;
import com.melonltd.naber.view.factory.PageType;
import com.melonltd.naber.view.seller.adapter.SellerTimeRangeAdapter;
import com.melonltd.naber.view.seller.page.SellerCategoryListFragment;
import com.melonltd.naber.view.seller.page.SellerDetailFragment;
import com.melonltd.naber.view.seller.page.SellerFoodEditFragment;
import com.melonltd.naber.view.seller.page.SellerFoodListFragment;
import com.melonltd.naber.view.seller.page.SellerOrderLogsDetailFragment;
import com.melonltd.naber.view.seller.page.SellerOrdersFragment;
import com.melonltd.naber.view.seller.page.SellerOrdersLogsFragment;
import com.melonltd.naber.view.seller.page.SellerSearchFragment;
import com.melonltd.naber.view.seller.page.SellerSetUpFragment;
import com.melonltd.naber.view.seller.page.SellerSimpleInformationFragment;
import com.melonltd.naber.view.seller.page.SellerStatFragment;
import com.melonltd.naber.vo.BulletinVo;
import com.melonltd.naber.vo.DateRangeVo;
import com.melonltd.naber.vo.RestaurantInfoVo;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

public class SellerMainActivity extends BaseCore implements TabLayout.OnTabSelectedListener, SwitchButton.OnCheckedChangeListener {
//    private static final String TAG = SellerMainActivity.class.getSimpleName();
    private static Context context;
    public static Toolbar toolbar;
    public static Button sortBtn;

    private static Drawable defaultIcon;
    private static DrawerLayout drawer;
    private static SellerTimeRangeAdapter adapter;
    private static FragmentManager FM;

    public static List<View> tabViews = Lists.<View>newArrayList();

    private static final List<PageType> SELLER_MAIN_PAGE = Lists.newArrayList(PageType.SELLER_SEARCH, PageType.SELLER_ORDERS, PageType.SELLER_STAT, PageType.SELLER_CATEGORY_LIST, PageType.SELLER_SET_UP);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller);
        FM = getSupportFragmentManager();
        context = this;

        SellerFoodListFragment.FRAGMENT = null;
        SellerDetailFragment.FRAGMENT = null;
        SellerFoodEditFragment.FRAGMENT = null;
        SellerOrderLogsDetailFragment.FRAGMENT = null;
        SellerOrdersFragment.FRAGMENT = null;
        SellerOrdersLogsFragment.FRAGMENT = null;
        SellerCategoryListFragment.FRAGMENT = null;
        SellerSearchFragment.FRAGMENT = null;
        SellerSetUpFragment.FRAGMENT = null;
        SellerSimpleInformationFragment.FRAGMENT = null;
        SellerStatFragment.FRAGMENT = null;
        getViews();

        removeAndReplaceWhere(null, PageType.SELLER_SEARCH, null);
    }

    private void getViews() {
        toolbar = findViewById(R.id.toolbar);
        sortBtn = findViewById(R.id.sortBtn);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.seller_drawer_layout);
        final BGARefreshLayout refreshLayout = findViewById(R.id.sellerGARefreshLayout);

        BGANormalRefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(context, true);
        refreshViewHolder.setPullDownRefreshText("Pull");
        refreshViewHolder.setRefreshingText("Pull to refresh");
        refreshViewHolder.setReleaseRefreshText("Pull to refresh");
        refreshViewHolder.setLoadingMoreText("Loading more !");

        refreshLayout.setRefreshViewHolder(refreshViewHolder);

        RecyclerView recyclerView = findViewById(R.id.sellerRecyclerView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        TabLayout tabLayout = findViewById(R.id.sellerMenuTabLayout);
        tabLayout.addOnTabSelectedListener(this);
        tabLayout.removeAllTabs();
        View v0 = new NaberTab(context).Builder().setIcon(R.drawable.naber_tab_search_icon).setTitle(R.string.seller_menu_search_btn).build();
        View v1 = new NaberTab(context).Builder().setIcon(R.drawable.naber_tab_history_icon).setTitle(R.string.seller_menu_orders_btn).build();
        View v2 = new NaberTab(context).Builder().setIcon(R.drawable.naber_tab_stat_icon).setTitle(R.string.seller_menu_stat_btn).build();
        View v3 = new NaberTab(context).Builder().setIcon(R.drawable.naber_tab_restaurant_icon).setTitle(R.string.seller_menu_category_list_btn).build();
        View v4 = new NaberTab(context).Builder().setIcon(R.drawable.naber_tab_set_up_icon).setTitle(R.string.seller_menu_set_up_btn).build();
        tabViews = Lists.<View>newArrayList(v0, v1, v2, v3, v4);
        tabLayout.addTab(tabLayout.newTab().setCustomView(v0).setTag(R.string.seller_menu_search_btn), false);
        tabLayout.addTab(tabLayout.newTab().setCustomView(v1).setTag(R.string.seller_menu_orders_btn), false);
        tabLayout.addTab(tabLayout.newTab().setCustomView(v2).setTag(R.string.seller_menu_stat_btn), false);
        tabLayout.addTab(tabLayout.newTab().setCustomView(v3).setTag(R.string.seller_menu_category_list_btn), false);
        tabLayout.addTab(tabLayout.newTab().setCustomView(v4).setTag(R.string.seller_menu_set_up_btn), false);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        defaultIcon = toolbar.getNavigationIcon();

        adapter = new SellerTimeRangeAdapter(this);
        recyclerView.setAdapter(adapter);
        refreshLayout.setDelegate(new BGARefreshLayout.BGARefreshLayoutDelegate() {
            @Override
            public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
                refreshLayout.endRefreshing();
                doLoadData();
            }

            @Override
            public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        doLoadData();
    }


    public static void notifyDateRange(){
        if(adapter != null){
            adapter.notifyDataSetChanged();
        }
    }

    public void doLoadData() {
        Model.SELLER_BUSINESS_TIME_RANGE.clear();
        ApiManager.sellerBusinessTime(new ThreadCallback(context) {
            @Override
            public void onSuccess(String responseBody) {
                Model.SELLER_BUSINESS_TIME_RANGE = Tools.JSONPARSE.fromJsonList(responseBody, DateRangeVo[].class);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(Exception error, String msg) {

            }
        });

        // 取得全部公告
        ApiManager.bulletin(new ThreadCallback(context) {
            @Override
            public void onSuccess(String responseBody) {
                List<BulletinVo> list = Tools.JSONPARSE.fromJsonList(responseBody, BulletinVo[].class);
                for (int i = 0; i < list.size(); i++) {
                    Iterator<String> iterator = Splitter.on("$split").split(list.get(i).content_text).iterator();
                    String content_text = "";
                    while (iterator.hasNext()) {
                        content_text += iterator.next() + "\n";
                    }
                    Map<String, String> m = Maps.newHashMap();
                    m.put("title", list.get(i).title);
                    m.put("content_text", content_text);
                    Model.BULLETIN_VOS.put(list.get(i).bulletin_category, m);
                }
            }

            @Override
            public void onFail(Exception error, String msg) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_CODE:
                if (BaseCore.checkGrantResults(grantResults)){
                    if (BaseCore.FRAGMENT_TAG.equals(PageType.SELLER_FOOD_EDIT.name())){
                        SellerFoodEditFragment.FRAGMENT.intentToCamera();
                    }
                }
                break;
            case IO_STREAM_CODE:
                if (BaseCore.checkGrantResults(grantResults)){
                    if (BaseCore.FRAGMENT_TAG.equals(PageType.SELLER_FOOD_EDIT.name())){
                        SellerFoodEditFragment.FRAGMENT.intentToPick();
                    }
                }
                break;
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int index = Integer.parseInt(tab.getTag().toString());
        if (SELLER_MAIN_PAGE.contains(PageType.ofId(index))) {
            Fragment fragment = PageFragmentFactory.of(PageType.ofId(Integer.parseInt(tab.getTag().toString())), null);
            BaseCore.FRAGMENT_TAG = PageType.ofId(Integer.parseInt(tab.getTag().toString())).name();
            getSupportFragmentManager().beginTransaction().addToBackStack(fragment.toString()).replace(R.id.sellerFrameContainer, fragment).commit();
        }
        View v = tab.getCustomView();
        ImageView icon = v.findViewById(R.id.tabIcon);
        TextView text = v.findViewById(R.id.tabTitle);
        icon.setColorFilter(getResources().getColor(R.color.naber_basis));
        text.setTextColor(getResources().getColor(R.color.naber_basis));
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        View v = tab.getCustomView();
        ImageView icon = v.findViewById(R.id.tabIcon);
        TextView text = v.findViewById(R.id.tabTitle);
        icon.setColorFilter(getResources().getColor(R.color.naber_tab_default_color));
        text.setTextColor(getResources().getColor(R.color.naber_tab_default_color));
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        int index = Integer.parseInt(tab.getTag().toString());
        if (!BaseCore.FRAGMENT_TAG.equals(PageType.ofId(index).name())) {
            if (SELLER_MAIN_PAGE.contains(PageType.ofId(index))) {
                Fragment fragment = PageFragmentFactory.of(PageType.ofId(Integer.parseInt(tab.getTag().toString())), null);
                BaseCore.FRAGMENT_TAG = PageType.ofId(Integer.parseInt(tab.getTag().toString())).name();
                getSupportFragmentManager().beginTransaction().addToBackStack(fragment.toString()).replace(R.id.sellerFrameContainer, fragment).commit();
            }

            View v = tab.getCustomView();
            ImageView icon = v.findViewById(R.id.tabIcon);
            TextView text = v.findViewById(R.id.tabTitle);
            icon.setColorFilter(getResources().getColor(R.color.naber_basis));
            text.setTextColor(getResources().getColor(R.color.naber_basis));
        }
    }


    public static void changeTabAndToolbarStatus() {
        int position = PageType.equalsPositionByName(BaseCore.FRAGMENT_TAG);


            toolbar.setTitle(context.getResources().getString(PageType.equalsIdByName(FRAGMENT_TAG)));

            for (View tab : tabViews) {
                ImageView icon = tab.findViewById(R.id.tabIcon);
                TextView text = tab.findViewById(R.id.tabTitle);
                icon.setColorFilter(context.getResources().getColor(R.color.naber_tab_default_color));
                text.setTextColor(context.getResources().getColor(R.color.naber_tab_default_color));
                if (tabViews.indexOf(tab) == position) {
                    icon.setColorFilter(context.getResources().getColor(R.color.naber_basis));
                    text.setTextColor(context.getResources().getColor(R.color.naber_basis));
                }
            }
        }



    public static void navigationIconDisplay(boolean show, View.OnClickListener listener) {
        if (!show) {
            toolbar.setNavigationIcon(defaultIcon);
            toolbar.setNavigationOnClickListener(null);
            drawer.addDrawerListener(new ActionBarDrawerToggle((Activity) context, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close));
        } else {
            toolbar.setNavigationIcon(context.getResources().getDrawable(R.drawable.naber_back_icon));
            toolbar.setNavigationOnClickListener(listener);
        }
    }

    @Override
    public void onCheckedChanged(SwitchButton v, boolean isChecked) {
        final int index = (int) v.getTag();
        SwitchStatus switchStatus = SwitchStatus.of(isChecked);
        if (!Model.SELLER_BUSINESS_TIME_RANGE.get(index).status.equals(switchStatus.name())) {
            Model.SELLER_BUSINESS_TIME_RANGE.get(index).status = switchStatus.name();
            RestaurantInfoVo vo = new RestaurantInfoVo();
            vo.can_store_range = Model.SELLER_BUSINESS_TIME_RANGE;
            ApiManager.sellerChangeBusinessTime(vo, new ThreadCallback(context) {
                @Override
                public void onSuccess(String responseBody) {
                    Model.SELLER_BUSINESS_TIME_RANGE.clear();
                    Model.SELLER_BUSINESS_TIME_RANGE = Tools.JSONPARSE.fromJsonList(responseBody, DateRangeVo[].class);
                    if (Model.SELLER_BUSINESS_TIME_RANGE.size() > index){
                        adapter.notifyItemChanged(index);
                    }
                }
                @Override
                public void onFail(Exception error, String msg) {

                }
            });
        }
    }

    public static void removeAndReplaceWhere(Fragment fm, PageType pageType, Bundle bundle) {
        FRAGMENT_TAG = pageType.name();
        Fragment fragment = PageFragmentFactory.of(pageType, bundle);
        if (fm != null) {
            FM.beginTransaction()
                    .remove(fm)
                    .replace(R.id.sellerFrameContainer, fragment)
                    .addToBackStack(pageType.toClass().getSimpleName())
                    .commit();
        } else {
            FM.beginTransaction()
                    .replace(R.id.sellerFrameContainer, fragment)
                    .addToBackStack(pageType.toClass().getSimpleName())
                    .commit();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public static void lockDrawer(boolean lock) {
        if (lock) {
            SellerMainActivity.toolbar.setNavigationIcon(null);
            drawer.closeDrawers();
        }
        drawer.setDrawerLockMode(lock ? DrawerLayout.LOCK_MODE_LOCKED_CLOSED : DrawerLayout.LOCK_MODE_UNDEFINED);
    }

    public static void clearAllFragment() {
        SellerFoodListFragment.FRAGMENT = null;
        SellerDetailFragment.FRAGMENT = null;
        SellerFoodEditFragment.FRAGMENT = null;
        SellerOrderLogsDetailFragment.FRAGMENT = null;
        SellerOrdersFragment.FRAGMENT = null;
        SellerOrdersLogsFragment.FRAGMENT = null;
        SellerCategoryListFragment.FRAGMENT = null;
        SellerSearchFragment.FRAGMENT = null;
        SellerSetUpFragment.FRAGMENT = null;
        SellerSimpleInformationFragment.FRAGMENT = null;
        SellerStatFragment.FRAGMENT = null;
    }
}
