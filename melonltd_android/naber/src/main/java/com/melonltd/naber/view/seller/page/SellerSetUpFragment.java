package com.melonltd.naber.view.seller.page;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.melonltd.naber.BuildConfig;
import com.melonltd.naber.R;
import com.melonltd.naber.model.constant.NaberConstant;
import com.melonltd.naber.model.service.SPService;
import com.melonltd.naber.view.factory.PageType;
import com.melonltd.naber.view.seller.SellerMainActivity;

public class SellerSetUpFragment extends Fragment implements View.OnClickListener {
    //    private static final String TAG = SellerSetUpFragment.class.getSimpleName();
    public static SellerSetUpFragment FRAGMENT = null;

    private TextView toSellerEdit, toAboutUsText, accountNumberText, versionName;
    public static int TO_SELLER_DETAIL_INDEX = -1;
    public static int TO_SELLER_SIMPLE_INFO_INDEX = -1;
//    private Bundle bundle = new Bundle();

    public SellerSetUpFragment() {
    }

    public Fragment getInstance(Bundle bundle) {
        if (FRAGMENT == null) {
            FRAGMENT = new SellerSetUpFragment();
            TO_SELLER_DETAIL_INDEX = -1;
            TO_SELLER_SIMPLE_INFO_INDEX = -1;
        }
        if (bundle != null) {
            FRAGMENT.setArguments(bundle);
        }
        return FRAGMENT;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_seller_set_up, container, false);
        getViews(v);
        return v;
    }

    private void getViews(View v) {
        toSellerEdit = v.findViewById(R.id.toAccountEdit);
        toAboutUsText = v.findViewById(R.id.toAboutUsText);
        accountNumberText = v.findViewById(R.id.accountNumberText);
        versionName = v.findViewById(R.id.versionName);
        toSellerEdit.setOnClickListener(this);
        toAboutUsText.setOnClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        versionName.setText("V"+ BuildConfig.VERSION_NAME);
        SellerMainActivity.changeTabAndToolbarStatus();
        SellerMainActivity.lockDrawer(true);
        if (TO_SELLER_DETAIL_INDEX > 0) {
            SellerMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.SELLER_DETAIL, null);
        } else if (TO_SELLER_SIMPLE_INFO_INDEX > 0) {
            Bundle bundle = new Bundle();
            bundle.putStringArrayList(NaberConstant.SIMPLE_INFO, Lists.newArrayList("ABOUT_US", "APPLY_OF_SELLER"));
            SellerMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.SELLER_SIMPLE_INFO, bundle);
        } else {
            accountNumberText.setText(SPService.getAccout());
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.toAccountEdit:
                TO_SELLER_DETAIL_INDEX = 1;
                SellerMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.SELLER_DETAIL, null);
                break;
            case R.id.toAboutUsText:
                Bundle bundle = new Bundle();
                bundle.putStringArrayList(NaberConstant.SIMPLE_INFO, Lists.newArrayList("ABOUT_US", "APPLY_OF_SELLER"));
                TO_SELLER_SIMPLE_INFO_INDEX = 1;
                SellerMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.SELLER_SIMPLE_INFO, bundle);
                break;
        }

    }
}
