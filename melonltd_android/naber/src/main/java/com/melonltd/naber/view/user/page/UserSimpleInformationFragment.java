package com.melonltd.naber.view.user.page;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.melonltd.naber.model.bean.Model;
import com.melonltd.naber.model.constant.NaberConstant;
import com.melonltd.naber.view.factory.PageType;
import com.melonltd.naber.view.user.UserMainActivity;
import com.melonltd.naber.R;

import java.util.List;

public class UserSimpleInformationFragment extends Fragment {
//    private static final String TAG = UserSimpleInformationFragment.class.getSimpleName();
    public static UserSimpleInformationFragment FRAGMENT = null;

    private LinearLayout bulletinContent;

    public UserSimpleInformationFragment() {
    }

    public Fragment getInstance(Bundle bundle) {
        if (FRAGMENT == null) {
            FRAGMENT = new UserSimpleInformationFragment();
        }
        if (bundle != null){
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
        if (container.getTag(R.id.user_simple_information_page) == null) {
            View v = inflater.inflate(R.layout.fragment_user_simple_information, container, false);
            bulletinContent = v.findViewById(R.id.bulletinContent);
            container.setTag(R.id.user_simple_information_page, v);
            return v;
        }
        return (View) container.getTag(R.id.user_simple_information_page);
    }

    @Override
    public void onResume() {
        super.onResume();
        UserMainActivity.changeTabAndToolbarStatus();

        if (UserMainActivity.toolbar != null) {
            UserMainActivity.navigationIconDisplay(true, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UserSetUpFragment.TO_SIMPLE_INFO_INDEX = -1;
                    UserMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.USER_ACCOUNT, null);
                    UserMainActivity.navigationIconDisplay(false, null);
                }
            });
        }

        bulletinContent.removeAllViews();
        UserMainActivity.toolbar.setTitle(getArguments().getString(NaberConstant.TOOLBAR_TITLE));
        List<String> list = getArguments().getStringArrayList(NaberConstant.SIMPLE_INFO);
        for(int i=0; i<list.size(); i++){
            View v = LayoutInflater.from(getContext()).inflate(R.layout.common_bulletin_template, null);
            TextView title = v.findViewById(R.id.titleText);
            TextView msg = v.findViewById(R.id.messageText);
            title.setText(Model.BULLETIN_VOS.get(list.get(i)).get("title"));
            msg.setText(Model.BULLETIN_VOS.get(list.get(i)).get("content_text"));
            bulletinContent.addView(v);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        UserMainActivity.navigationIconDisplay(false, null);
    }
}
