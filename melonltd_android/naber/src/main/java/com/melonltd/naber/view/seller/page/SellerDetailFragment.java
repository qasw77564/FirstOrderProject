package com.melonltd.naber.view.seller.page;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.firebase.iid.FirebaseInstanceId;
import com.melonltd.naber.R;
import com.melonltd.naber.model.api.ApiManager;
import com.melonltd.naber.model.api.ThreadCallback;
import com.melonltd.naber.model.bean.Model;
import com.melonltd.naber.model.constant.NaberConstant;
import com.melonltd.naber.model.service.SPService;
import com.melonltd.naber.model.type.SwitchStatus;
import com.melonltd.naber.util.Tools;
import com.melonltd.naber.view.customize.SwitchButton;
import com.melonltd.naber.view.factory.PageType;
import com.melonltd.naber.view.seller.SellerMainActivity;
import com.melonltd.naber.vo.RestaurantInfoVo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SellerDetailFragment extends Fragment implements View.OnClickListener {
    //    private static final String TAG = SellerDetailFragment.class.getSimpleName();
    public static SellerDetailFragment FRAGMENT = null;
    public static int TO_RESET_PASSWORD_INDEX = -1;
    private EditText bulletinEdit;
    private TextView storeStartText, storeEndText;
    private LinearLayout businessLayout;
    private Map<String, Boolean> notBusinessData = Maps.<String, Boolean>newHashMap();

    public SellerDetailFragment() {
    }

    public Fragment getInstance(Bundle bundle) {
        if (FRAGMENT == null) {
            FRAGMENT = new SellerDetailFragment();
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
        View v = inflater.inflate(R.layout.fragment_seller_detail, container, false);
        getViews(v);
        return v;
    }

    private void getViews(View v) {
        bulletinEdit = v.findViewById(R.id.bulletinEdit);
        Button submitBtn = v.findViewById(R.id.submitBtn);
        Button logoutBtn = v.findViewById(R.id.logoutBtn);
        Button toResetPasswordBtn = v.findViewById(R.id.toResetPasswordBtn);

        storeStartText = v.findViewById(R.id.storeStartText);
        storeEndText = v.findViewById(R.id.storeEndText);
        businessLayout = v.findViewById(R.id.businessLayout);

        submitBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
        toResetPasswordBtn.setOnClickListener(this);
        storeStartText.setOnClickListener(this);
        storeEndText.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        SellerMainActivity.changeTabAndToolbarStatus();

        if (SellerMainActivity.toolbar != null) {
            SellerMainActivity.navigationIconDisplay(true, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SellerSetUpFragment.TO_SELLER_DETAIL_INDEX = -1;
                    SellerMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.SELLER_SET_UP, null);
                    SellerMainActivity.navigationIconDisplay(false, null);

                }
            });
        }

        if (TO_RESET_PASSWORD_INDEX >= 0) {
            TO_RESET_PASSWORD_INDEX = 1;
            SellerMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.SELLER_RESET_PASSWORD, null);
        } else {
            ApiManager.sellerRestaurantInfo(new ThreadCallback(getContext()) {
                @Override
                public void onSuccess(String responseBody) {
                    RestaurantInfoVo restaurant = Tools.JSONPARSE.fromJson(responseBody, RestaurantInfoVo.class);
                    storeStartText.setText(restaurant.store_start);
                    storeStartText.setTag(restaurant.store_start);
                    storeEndText.setText(restaurant.store_end);
                    storeEndText.setTag(restaurant.store_end);
                    bulletinEdit.setText(restaurant.bulletin);
                    builderThreeBusiness(restaurant.not_business);
                }

                @Override
                public void onFail(Exception error, String msg) {
                    builderThreeBusiness(Lists.<String>newArrayList());
                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        SellerMainActivity.navigationIconDisplay(false, null);
    }

    private void showDatePicker(final int id) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(bulletinEdit.getWindowToken(), 0);
        OptionsPickerView pvOptions = new OptionsPickerBuilder(getContext(),
                new OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int option2, int options3, View v) {
                        String tx = NaberConstant.HOUR_OPT.get(options1) + ":" + NaberConstant.MINUTE_OPT.get(option2);
                        if (id == R.id.storeStartText) {
                            storeStartText.setText(tx);
                            storeStartText.setTag(tx);
                        } else if (id == R.id.storeEndText) {
                            storeEndText.setText(tx);
                            storeEndText.setTag(tx);
                        }
                    }
                })
                .setTitleSize(20)
                .setTitleBgColor(getResources().getColor(R.color.naber_dividing_line_gray))
                .setCancelColor(getResources().getColor(R.color.naber_dividing_gray))
                .setSubmitColor(getResources().getColor(R.color.naber_dividing_gray))
                .build();
        pvOptions.setNPicker(NaberConstant.HOUR_OPT, NaberConstant.MINUTE_OPT, null);
        pvOptions.show();
    }


    private void builderThreeBusiness(List<String> not_busines) {
        notBusinessData = Maps.newHashMap();
        businessLayout.removeAllViews();
        Date now = new Date();
        long day = 1000 * 60 * 60 * 24L;
        SimpleDateFormat format = new SimpleDateFormat("MM/dd");
        for (int i = 0; i < 3; i++) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.seller_select_date_switch, null);
            TextView dateText = v.findViewById(R.id.dateText);
            SwitchButton switchButton = v.findViewById(R.id.sellerDateSelectSwitch);
            String date = Tools.FORMAT.formatStartDate(now, "T00:00:00.0000Z");
            switchButton.setTag(date);
            switchButton.setChecked(not_busines.contains(date) ? false : true);

            switchButton.setOnCheckedChangeListener(new BusinessChangeListener());
            dateText.setText(format.format(now));
            now.setTime(now.getTime() + day);
            notBusinessData.put(date, switchButton.isChecked());
            businessLayout.addView(v);
        }
    }


    class BusinessChangeListener implements SwitchButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(final SwitchButton view, final boolean isChecked) {
            final String date = view.getTag().toString();

            if (!isChecked) {
                new AlertView.Builder()
                        .setTitle("")
                        .setMessage("關閉營業時段，請先確認該日有無訂單，\n若有訂單請記得告知使用者")
                        .setContext(getContext())
                        .setStyle(AlertView.Style.Alert)
                        .setOthers(new String[]{"確定關閉", "取消"})
                        .setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(Object o, int position) {
                                if (position == 0) {
                                    if (notBusinessData.get(date) != isChecked) {
                                        new Handler().postDelayed(new SettingBusinessRun(date, isChecked), 300);
                                    }
                                } else if (position == 1) {
                                    view.setChecked(notBusinessData.get(date));
                                }
                            }
                        })
                        .build()
                        .setCancelable(false)
                        .show();
            } else {
                new Handler().postDelayed(new SettingBusinessRun(date, isChecked), 300);
            }
        }
    }

    class SettingBusinessRun implements Runnable {
        private String date;
        private SwitchStatus status;

        SettingBusinessRun(String date, boolean isChecked) {
            this.date = date;
            this.status = SwitchStatus.of(isChecked);
        }

        @Override
        public void run() {
            Map<String, String> req = Maps.newHashMap();
            req.put("date", this.date);
            req.put("status", this.status.name());
            ApiManager.sellerRestaurantSettingBusiness(req, new ThreadCallback(getContext()) {
                @Override
                public void onSuccess(String responseBody) {
                    notBusinessData.put(date, status.getStatus());
                }

                @Override
                public void onFail(Exception error, String msg) {
                }
            });

        }
    }

    @Override
    public void onClick(View view) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        switch (view.getId()) {
            case R.id.submitBtn:
                RestaurantInfoVo vo = new RestaurantInfoVo();
                vo.store_start = storeStartText.getTag().toString();
                vo.store_end = storeEndText.getTag().toString();
                vo.bulletin = bulletinEdit.getText().toString();
                if(bulletinEdit.getText().toString().length() > 100){
                    new AlertView.Builder()
                            .setTitle("")
                            .setMessage("公告編輯字數已超出最大範圍，\n請重新編輯至 100 字數以下")
                            .setContext(getContext())
                            .setStyle(AlertView.Style.Alert)
                            .setOthers(new String[]{"我知道了"})
                            .build()
                            .setCancelable(true)
                            .show();
                } else if (vo.store_end.equals(vo.store_start)) {
                    new AlertView.Builder()
                            .setTitle("")
                            .setMessage("接單時間開始與結束，\n不可以相同。")
                            .setContext(getContext())
                            .setStyle(AlertView.Style.Alert)
                            .setOthers(new String[]{"我知道了"})
                            .build()
                            .setCancelable(true)
                            .show();
                } else {
                    ApiManager.sellerRestaurantSetting(vo, new ThreadCallback(getContext()) {
                        @Override
                        public void onSuccess(String responseBody) {
                            RestaurantInfoVo resp = Tools.JSONPARSE.fromJson(responseBody, RestaurantInfoVo.class);
                            Model.SELLER_BUSINESS_TIME_RANGE.clear();
                            Model.SELLER_BUSINESS_TIME_RANGE.addAll(resp.can_store_range);
                        }

                        @Override
                        public void onFail(Exception error, String msg) {

                        }
                    });
                }

                break;
            case R.id.logoutBtn:
                Map<String, String> req = Maps.newHashMap();
                req.put("account_uuid" , SPService.getOauth());
                req.put("device_token" , FirebaseInstanceId.getInstance().getToken());
                req.put("device_category", "ANDROID");
                ApiManager.logout(req, new ThreadCallback(getContext()) {
                    @Override
                    public void onSuccess(String responseBody) {
                        SPService.removeAll();
                        getActivity().finish();
                    }

                    @Override
                    public void onFail(Exception error, String msg) {
                        SPService.removeAll();
                        getActivity().finish();
                    }
                });

                break;
            case R.id.storeStartText:
                showDatePicker(R.id.storeStartText);
                break;
            case R.id.storeEndText:
                showDatePicker(R.id.storeEndText);
                break;
            case R.id.toResetPasswordBtn:
                TO_RESET_PASSWORD_INDEX = 1;
                SellerMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.SELLER_RESET_PASSWORD, null);
                break;
        }
    }
}
