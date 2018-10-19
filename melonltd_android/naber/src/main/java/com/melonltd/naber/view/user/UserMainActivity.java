package com.melonltd.naber.view.user;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.multidex.MultiDex;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.melonltd.naber.R;
import com.melonltd.naber.model.api.ApiCallback;
import com.melonltd.naber.model.api.ApiManager;
import com.melonltd.naber.model.constant.NaberConstant;
import com.melonltd.naber.util.Tools;
import com.melonltd.naber.view.common.BaseCore;
import com.melonltd.naber.view.customize.NaberTab;
import com.melonltd.naber.view.factory.PageFragmentFactory;
import com.melonltd.naber.view.factory.PageType;
import com.melonltd.naber.view.user.page.UserAccountDetailFragment;
import com.melonltd.naber.view.user.page.UserFoodDetailFragment;
import com.melonltd.naber.view.user.page.UserFoodListFragment;
import com.melonltd.naber.view.user.page.UserHomeFragment;
import com.melonltd.naber.view.user.page.UserOrderDetailFragment;
import com.melonltd.naber.view.user.page.UserOrderHistoryFragment;
import com.melonltd.naber.view.user.page.UserResetPasswordFragment;
import com.melonltd.naber.view.user.page.UserRestaurantDetailFragment;
import com.melonltd.naber.view.user.page.UserRestaurantListFragment;
import com.melonltd.naber.view.user.page.UserSetUpFragment;
import com.melonltd.naber.view.user.page.UserShoppingCartFragment;
import com.melonltd.naber.view.user.page.UserSimpleInformationFragment;
import com.melonltd.naber.view.user.page.UserSubmitOrdersFragment;

import java.util.List;


public class UserMainActivity extends BaseCore implements View.OnClickListener, TabLayout.OnTabSelectedListener {
    private static final String TAG = UserMainActivity.class.getSimpleName();
    private static Context context;
    public static int LAYOUT_WIDTH = 0;
    public static Toolbar toolbar;
    public static List<View> tabViews = Lists.<View>newArrayList();
    private static FragmentManager FM;
    private static final List<PageType> MAIN_PAGE = Lists.newArrayList(PageType.USER_HOME, PageType.USER_RESTAURANT_LIST, PageType.USER_SHOPPING_CART, PageType.USER_ORDER_HISTORY, PageType.USER_ACCOUNT);

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        context = this;
        getView();
        FM = getSupportFragmentManager();
        removeAndReplaceWhere(null, PageType.USER_HOME, null);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        LAYOUT_WIDTH = dm.widthPixels;
    }

    private void getView() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TabLayout tabLayout = findViewById(R.id.bottomMenuTabLayout);
        tabLayout.addOnTabSelectedListener(this);

        tabLayout.removeAllTabs();
        View v0 = new NaberTab(context).Builder().setIcon(R.drawable.naber_tab_home_icon).setTitle(R.string.menu_home_btn).build();
        View v1 = new NaberTab(context).Builder().setIcon(R.drawable.naber_tab_restaurant_icon).setTitle(R.string.menu_restaurant_btn).build();
        View v2 = new NaberTab(context).Builder().setIcon(R.drawable.naber_tab_shopping_cart_icon).setTitle(R.string.menu_shopping_cart_btn).build();
        View v3 = new NaberTab(context).Builder().setIcon(R.drawable.naber_tab_history_icon).setTitle(R.string.menu_history_btn).build();
        View v4 = new NaberTab(context).Builder().setIcon(R.drawable.naber_tab_account_icon).setTitle(R.string.menu_account_btn).build();
        tabViews = Lists.<View>newArrayList(v0, v1, v2, v3, v4);
        tabLayout.addTab(tabLayout.newTab().setCustomView(v0).setTag(R.string.menu_home_btn), false);
        tabLayout.addTab(tabLayout.newTab().setCustomView(v1).setTag(R.string.menu_restaurant_btn), false);
        tabLayout.addTab(tabLayout.newTab().setCustomView(v2).setTag(R.string.menu_shopping_cart_btn), false);
        tabLayout.addTab(tabLayout.newTab().setCustomView(v3).setTag(R.string.menu_history_btn), false);
        tabLayout.addTab(tabLayout.newTab().setCustomView(v4).setTag(R.string.menu_account_btn), false);

    }

    @Override
    protected void onResume() {
        super.onResume();
//        ApiManager.storeCategoryList(new ApiCallback(context) {
//            @Override
//            public void onSuccess(String responseBody) {
//                List<String> categoryNames = Tools.JSONPARSE.fromJsonList(responseBody, String[].class);
//                NaberConstant.FILTER_CATEGORYS = new String[categoryNames.size()];
//                NaberConstant.FILTER_CATEGORYS = categoryNames.toArray(NaberConstant.FILTER_CATEGORYS);
//            }
//            @Override
//            public void onFail(Exception error, String msg) {
////                Log.i(TAG, msg);
//            }
//        });

//        ApiManager.storeAreaList(new ApiCallback(context) {
//            @Override
//            public void onSuccess(String responseBody) {
//                List<String> areas = Tools.JSONPARSE.fromJsonList(responseBody, String[].class);
//                NaberConstant.FILTER_AREAS = new String[areas.size()];
//                NaberConstant.FILTER_AREAS = areas.toArray(NaberConstant.FILTER_AREAS);
//            }
//
//            @Override
//            public void onFail(Exception error, String msg) {
//
//            }
//        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_CODE:
                if (BaseCore.checkGrantResults(grantResults)){
                    if (BaseCore.FRAGMENT_TAG.equals(PageType.USER_HOME.name())){
                        UserHomeFragment.FRAGMENT.doLoadData(true);
                    }
                }
                break;
            case CAMERA_CODE:
                if (BaseCore.checkGrantResults(grantResults)){
                    if (BaseCore.FRAGMENT_TAG.equals(PageType.USER_ACCOUNT_DETAIL.name())){
                        UserAccountDetailFragment.FRAGMENT.intentToCamera();
                    }
                }
                break;
            case IO_STREAM_CODE:
                if (BaseCore.checkGrantResults(grantResults)){
                    if (BaseCore.FRAGMENT_TAG.equals(PageType.USER_ACCOUNT_DETAIL.name())){
                        UserAccountDetailFragment.FRAGMENT.intentToPick();
                    }
                }
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
    }

    public static void changeTabAndToolbarStatus() {
        int position = PageType.equalsPositionByName(FRAGMENT_TAG);
        if (PageType.USER_HOME.name().equals(FRAGMENT_TAG)) {
            toolbar.setTitle("NABER");
        } else {
            toolbar.setTitle(context.getResources().getString(PageType.equalsIdByName(FRAGMENT_TAG)));
        }

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

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        int index = Integer.parseInt(tab.getTag().toString());
        if (MAIN_PAGE.contains(PageType.ofId(index))) {
            removeAndReplaceWhere(null, PageType.ofId(Integer.parseInt(tab.getTag().toString())), null);
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
        if (!FRAGMENT_TAG.equals(PageType.ofId(index).name())) {
            if (MAIN_PAGE.contains(PageType.ofId(index))) {
                removeAndReplaceWhere(null, PageType.ofId(Integer.parseInt(tab.getTag().toString())), null);
            }
            View v = tab.getCustomView();
            ImageView icon = v.findViewById(R.id.tabIcon);
            TextView text = v.findViewById(R.id.tabTitle);
            icon.setColorFilter(getResources().getColor(R.color.naber_basis));
            text.setTextColor(getResources().getColor(R.color.naber_basis));
        }
    }

    public static void navigationIconDisplay(boolean show, View.OnClickListener listener) {
        if (!show) {
            toolbar.setNavigationIcon(null);
        } else {
            toolbar.setNavigationIcon(context.getResources().getDrawable(R.drawable.naber_back_icon));
        }
        toolbar.setNavigationOnClickListener(listener);
    }

    public static void removeAndReplaceWhere(Fragment fm, PageType pageType, Bundle bundle) {
        FRAGMENT_TAG = pageType.name();
        Fragment fragment = PageFragmentFactory.of(pageType, bundle);
        if (fm != null) {
            FM.beginTransaction()
                    .remove(fm)
                    .replace(R.id.frameContainer, fragment)
                    .addToBackStack(pageType.toClass().getSimpleName())
                    .commit();
        } else {
            FM.beginTransaction()
                    .replace(R.id.frameContainer, fragment)
                    .addToBackStack(pageType.toClass().getSimpleName())
                    .commit();
        }
    }

    public static void clearAllFragment() {
        UserAccountDetailFragment.FRAGMENT = null;
        UserFoodListFragment.FRAGMENT = null;
        UserOrderHistoryFragment.FRAGMENT = null;
        UserHomeFragment.FRAGMENT = null;
        UserFoodDetailFragment.FRAGMENT = null;
        UserOrderDetailFragment.FRAGMENT = null;
        UserResetPasswordFragment.FRAGMENT = null;
        UserRestaurantDetailFragment.FRAGMENT = null;
        UserRestaurantListFragment.FRAGMENT = null;
        UserSetUpFragment.FRAGMENT = null;
        UserShoppingCartFragment.FRAGMENT = null;
        UserSimpleInformationFragment.FRAGMENT = null;
        UserSubmitOrdersFragment.FRAGMENT = null;
    }

}
