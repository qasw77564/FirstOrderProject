package com.melonltd.naber.view.common;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.google.common.base.Strings;
import com.melonltd.naber.BuildConfig;
import com.melonltd.naber.R;
import com.melonltd.naber.model.api.ApiManager;
import com.melonltd.naber.model.api.ThreadCallback;
import com.melonltd.naber.model.bean.Model;
import com.melonltd.naber.model.constant.NaberConstant;
import com.melonltd.naber.model.service.SPService;
import com.melonltd.naber.model.type.Identity;
import com.melonltd.naber.util.Tools;
import com.melonltd.naber.view.common.page.LoginFragment;
import com.melonltd.naber.view.common.page.RecoverPasswordFragment;
import com.melonltd.naber.view.common.page.RegisteredFragment;
import com.melonltd.naber.view.common.page.RegisteredSellerFragment;
import com.melonltd.naber.view.factory.PageFragmentFactory;
import com.melonltd.naber.view.factory.PageType;
import com.melonltd.naber.view.seller.SellerMainActivity;
import com.melonltd.naber.view.user.UserMainActivity;
import com.melonltd.naber.vo.AppVersionLogVo;

import java.util.Date;

public class BaseActivity extends BaseCore {
    //    private static final String TAG = BaseActivity.class.getSimpleName();
    public static Context context;
    public static Toolbar toolbar;
    public static FragmentManager FM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        context = this;
        UserMainActivity.clearAllFragment();
        FM = getSupportFragmentManager();
        getViews();
    }

    private void getViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (FRAGMENT_TAG.equals("VERIFY_SMS")) {
            return;
        }

        // 檢查app 版本 機制
        new Handler().postDelayed(new StartUse(), 300);
    }




    class StartUse implements Runnable {
        @Override
        public void run() {
            String identity = SPService.getIdentity();
            if (Strings.isNullOrEmpty(identity)) {
                SPService.removeAll();
                FRAGMENT_TAG = PageType.LOGIN.name();
                Fragment fragment = PageFragmentFactory.of(PageType.LOGIN, null);
                getSupportFragmentManager().beginTransaction().replace(R.id.baseContainer, fragment, PageType.LOGIN.toClass().getSimpleName()).addToBackStack(fragment.toString()).commit();
            }else {
                if (Identity.getUserValues().contains(Identity.of(identity))) {
//                    Model.USER_CACHE_SHOPPING_CART = SPService.getUserCacheShoppingCarData();
                    startActivity(new Intent(context, UserMainActivity.class));
                } else if (Identity.SELLERS.equals(Identity.of(identity))) {
                    startActivity(new Intent(context, SellerMainActivity.class));
                } else {
                    SPService.removeAll();
                    FRAGMENT_TAG = PageType.LOGIN.name();
                    Fragment fragment = PageFragmentFactory.of(PageType.LOGIN, null);
                    getSupportFragmentManager().beginTransaction().replace(R.id.baseContainer, fragment, PageType.LOGIN.toClass().getSimpleName()).addToBackStack(fragment.toString()).commit();
                }
            }

//            long limit = SPService.getLoginLimit();
//            long now = new Date().getTime();
//            if (now - NaberConstant.REMEMBER_DAY < limit) {
//                String identity = SPService.getIdentity();
//                if (Identity.getUserValues().contains(Identity.of(identity))) {
//                    Model.USER_CACHE_SHOPPING_CART = SPService.getUserCacheShoppingCarData();
//                    startActivity(new Intent(context, UserMainActivity.class));
//                } else if (Identity.SELLERS.equals(Identity.of(identity))) {
//                    startActivity(new Intent(context, SellerMainActivity.class));
//                } else {
//                    SPService.removeAll();
//                    FRAGMENT_TAG = PageType.LOGIN.name();
//                    Fragment fragment = PageFragmentFactory.of(PageType.LOGIN, null);
//                    getSupportFragmentManager().beginTransaction().replace(R.id.baseContainer, fragment, PageType.LOGIN.toClass().getSimpleName()).addToBackStack(fragment.toString()).commit();
//                }
//            } else {
//                FRAGMENT_TAG = PageType.LOGIN.name();
//                Fragment fragment = PageFragmentFactory.of(PageType.LOGIN, null);
//                getSupportFragmentManager().beginTransaction().replace(R.id.baseContainer, fragment).addToBackStack(fragment.toString()).commit();
//            }

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        LoginFragment.FRAGMENT = null;
        RecoverPasswordFragment.FRAGMENT = null;
        RegisteredFragment.FRAGMENT = null;
        RegisteredSellerFragment.FRAGMENT = null;
//        VerifySMSFragment.FRAGMENT = null;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static void navigationIconDisplay(boolean show, View.OnClickListener listener) {
        if (!show) {
            toolbar.setNavigationIcon(null);
        } else {
            toolbar.setNavigationIcon(context.getResources().getDrawable(R.drawable.naber_back_icon));
        }
        toolbar.setNavigationOnClickListener(listener);
    }

    public static void changeToolbarStatus() {
        toolbar.setTitle(context.getResources().getString(PageType.equalsIdByName(FRAGMENT_TAG)));
    }


    public static void removeAndReplaceWhere(Fragment fm, PageType pageType, Bundle bundle) {
        FRAGMENT_TAG = pageType.name();
        Fragment fragment = PageFragmentFactory.of(pageType, bundle);
        FM.beginTransaction()
                .remove(fm)
                .replace(R.id.baseContainer, fragment)
                .addToBackStack(pageType.toClass().getSimpleName())
                .commit();
    }
}
