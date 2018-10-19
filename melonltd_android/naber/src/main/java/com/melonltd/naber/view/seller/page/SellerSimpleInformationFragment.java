package com.melonltd.naber.view.seller.page;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.melonltd.naber.R;
import com.melonltd.naber.model.bean.Model;
import com.melonltd.naber.model.constant.NaberConstant;
import com.melonltd.naber.view.factory.PageType;
import com.melonltd.naber.view.seller.SellerMainActivity;

import java.util.List;

public class SellerSimpleInformationFragment extends Fragment {
//    private static final String TAG = SellerSimpleInformationFragment.class.getSimpleName();
    public static SellerSimpleInformationFragment FRAGMENT = null;
    private LinearLayout bulletinContent;

    public SellerSimpleInformationFragment() {
    }

    public Fragment getInstance(Bundle bundle) {
        if (FRAGMENT == null) {
            FRAGMENT = new SellerSimpleInformationFragment();
        }
        if (bundle != null) {
            FRAGMENT.setArguments(bundle);
        }
        return FRAGMENT;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container.getTag(R.id.seller_simple_information_page) == null) {
            View v = inflater.inflate(R.layout.fragment_seller_simple_information, container, false);
            bulletinContent = v.findViewById(R.id.bulletinContent);
            container.setTag(R.id.seller_simple_information_page, v);
            return v;
        }
        return (View) container.getTag(R.id.seller_simple_information_page);
    }

    @Override
    public void onResume() {
        super.onResume();
        SellerMainActivity.changeTabAndToolbarStatus();
        if (SellerMainActivity.toolbar != null) {
            SellerMainActivity.navigationIconDisplay(true, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SellerSetUpFragment.TO_SELLER_SIMPLE_INFO_INDEX = -1;
                    SellerMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.SELLER_SET_UP, null);
                    SellerMainActivity.navigationIconDisplay(false, null);
                }
            });
        }

        bulletinContent.removeAllViews();
        List<String> list = getArguments().getStringArrayList(NaberConstant.SIMPLE_INFO);
        if (!Model.BULLETIN_VOS.isEmpty()){
            for(int i=0; i<list.size(); i++){
                View v = LayoutInflater.from(getContext()).inflate(R.layout.common_bulletin_template, null);
                TextView title = v.findViewById(R.id.titleText);
                TextView msg = v.findViewById(R.id.messageText);
                title.setText(Model.BULLETIN_VOS.get(list.get(i)).get("title"));
                msg.setText(Model.BULLETIN_VOS.get(list.get(i)).get("content_text"));
                bulletinContent.addView(v);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        SellerMainActivity.navigationIconDisplay(false, null);
    }
}
