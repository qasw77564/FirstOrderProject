package com.melonltd.naber.view.user.page;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.melonltd.naber.BuildConfig;
import com.melonltd.naber.R;
import com.melonltd.naber.model.api.ApiManager;
import com.melonltd.naber.model.api.ThreadCallback;
import com.melonltd.naber.model.constant.NaberConstant;
import com.melonltd.naber.model.service.SPService;
import com.melonltd.naber.util.IntegerTools;
import com.melonltd.naber.util.Tools;
import com.melonltd.naber.view.customize.SwitchButton;
import com.melonltd.naber.view.factory.PageType;
import com.melonltd.naber.view.user.UserMainActivity;
import com.melonltd.naber.vo.AccountInfoVo;


public class UserSetUpFragment extends Fragment{
    private static final String TAG = UserHomeFragment.class.getSimpleName();
    public static UserSetUpFragment FRAGMENT = null;
    public static int TO_ACCOUNT_DETAIL_INDEX = -1;
    public static int TO_SIMPLE_INFO_INDEX = -1;
    private ViewHolder holder;

    public UserSetUpFragment() {
    }

    public Fragment getInstance(Bundle bundle) {
        if (FRAGMENT == null) {
            FRAGMENT = new UserSetUpFragment();
            TO_ACCOUNT_DETAIL_INDEX = -1;
            TO_SIMPLE_INFO_INDEX = -1;
        }
        if (bundle != null){
            FRAGMENT.setArguments(bundle);
        }
        return FRAGMENT;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container.getTag(R.id.user_set_up_page) == null) {
            View v = inflater.inflate(R.layout.fragment_user_set_up, container, false);
            holder = new ViewHolder(v);
            container.setTag(R.id.user_set_up_page, v);
            return v;
        }
        return (View) container.getTag(R.id.user_set_up_page);
    }

    @Override
    public void onResume() {
        super.onResume();

        UserMainActivity.changeTabAndToolbarStatus();
        if (TO_ACCOUNT_DETAIL_INDEX >= 0) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(NaberConstant.ACCOUNT_INFO, holder.accountInfoVo);
            UserMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.USER_ACCOUNT_DETAIL, bundle);
        } else if (TO_SIMPLE_INFO_INDEX >= 0) {
            UserMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.USER_SIMPLE_INFO, holder.bundle);
        }else {
            ApiManager.userFindAccountInfo(new ThreadCallback(getContext()) {
                @Override
                public void onSuccess(String responseBody) {
                holder.accountInfoVo = Tools.JSONPARSE.fromJson(responseBody, AccountInfoVo.class);
                holder.accountText.setText(holder.accountInfoVo.phone);
                int use_bonus = IntegerTools.parseInt(holder.accountInfoVo.use_bonus,0);
                if(use_bonus > 0){
                    int bonus = IntegerTools.parseInt(holder.accountInfoVo.bonus,0);
                    int newBonus = bonus - use_bonus;
                    holder.bonusText.setText(""+newBonus);
                } else {
                    holder.bonusText.setText(holder.accountInfoVo.bonus);
                }
                if (!Strings.isNullOrEmpty(holder.accountInfoVo.photo)){
                    holder.accountPotoh.setImageURI(Uri.parse(holder.accountInfoVo.photo));
                }else {
                    ImageRequest request = ImageRequestBuilder.newBuilderWithResourceId(R.drawable.naber_icon_logo).build();
                    holder.accountPotoh.setImageURI(request.getSourceUri());
                }
            }

                @Override
                public void onFail(Exception error, String msg) {
                }
            });
        }
    }

    class SetUpClick implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.toAccountEdit:
                    // 因個人紅利問題，每次訪問該頁面從新抓取使用者資訊，不可停留在細節畫面
//                    TO_ACCOUNT_DETAIL_INDEX = 1;
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(NaberConstant.ACCOUNT_INFO, holder.accountInfoVo);
                    UserMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.USER_ACCOUNT_DETAIL, bundle);
                    break;
                case R.id.toBonusExchange:
                    UserMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.USER_BONUS_EXCHANGE, null);
                    break;
                case R.id.toAboutUsText:
//                    TO_SIMPLE_INFO_INDEX = 1;
                    holder.bundle.putString(NaberConstant.TOOLBAR_TITLE, ((TextView)view).getText().toString());
                    holder.bundle.putStringArrayList(NaberConstant.SIMPLE_INFO, Lists.newArrayList("ABOUT_US","APPLY_OF_SELLER"));
                    UserMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.USER_SIMPLE_INFO, holder.bundle);
                    break;
                case R.id.toHelpText:
//                    TO_SIMPLE_INFO_INDEX = 1;
                    holder.bundle.putString(NaberConstant.TOOLBAR_TITLE, ((TextView)view).getText().toString());
                    holder.bundle.putStringArrayList(NaberConstant.SIMPLE_INFO, Lists.newArrayList("FAQ","CONTACT_US"));
                    UserMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.USER_SIMPLE_INFO, holder.bundle);
                    break;
                case R.id.toTeachingText:
//                    TO_SIMPLE_INFO_INDEX = 1;
                    holder.bundle.putString(NaberConstant.TOOLBAR_TITLE, ((TextView)view).getText().toString());
                    holder.bundle.putStringArrayList(NaberConstant.SIMPLE_INFO, Lists.newArrayList("TEACHING"));
                    UserMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.USER_SIMPLE_INFO, holder.bundle);
                    break;
            }
        }
    }

    class NotifySetUpListener implements SwitchButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(SwitchButton view, boolean isChecked) {
            switch (view.getId()) {
                case R.id.soundSwitch:
                    SPService.setNotifyShake(isChecked);
                    break;
                case R.id.shakeSwitch:
                    SPService.setNotifyShake(isChecked);
                    break;
            }
        }
    }

    class ViewHolder {
        private TextView accountText, bonusText, versionName;
        private SimpleDraweeView accountPotoh;
        private AccountInfoVo accountInfoVo;
        private Bundle bundle = new Bundle();
        ViewHolder(View v){
            this.accountPotoh = v.findViewById(R.id.accountPotoh);
            this.accountText = v.findViewById(R.id.accountText);
            this.bonusText = v.findViewById(R.id.bonusText);
            this.versionName = v.findViewById(R.id.versionName);
            this.versionName.setText("V"+ BuildConfig.VERSION_NAME);

            SwitchButton soundSwitch = v.findViewById(R.id.soundSwitch);
            SwitchButton shakeSwitch = v.findViewById(R.id.shakeSwitch);
            TextView toAccountEdit = v.findViewById(R.id.toAccountEdit);
            TextView toBonusExchange = v.findViewById(R.id.toBonusExchange);
            TextView toAboutUsText = v.findViewById(R.id.toAboutUsText);
            TextView toHelpText = v.findViewById(R.id.toHelpText);
            TextView toTeachingText = v.findViewById(R.id.toTeachingText);

            soundSwitch.setChecked(SPService.getNotifySound());
            shakeSwitch.setChecked(SPService.getNotifyShake());

            NotifySetUpListener notifySetUpListener = new NotifySetUpListener();
            soundSwitch.setOnCheckedChangeListener(notifySetUpListener);
            shakeSwitch.setOnCheckedChangeListener(notifySetUpListener);

            SetUpClick click = new SetUpClick();
            toAccountEdit.setOnClickListener(click);
            toBonusExchange.setOnClickListener(click);
            toAboutUsText.setOnClickListener(click);
            toHelpText.setOnClickListener(click);
            toTeachingText.setOnClickListener(click);
        }
    }
}
