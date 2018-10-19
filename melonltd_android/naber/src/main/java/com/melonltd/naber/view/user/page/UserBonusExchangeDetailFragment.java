package com.melonltd.naber.view.user.page;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.melonltd.naber.R;
import com.melonltd.naber.model.api.ApiManager;
import com.melonltd.naber.model.api.ThreadCallback;
import com.melonltd.naber.model.bean.Model;
import com.melonltd.naber.util.IntegerTools;
import com.melonltd.naber.util.Tools;
import com.melonltd.naber.util.VerifyUtil;
import com.melonltd.naber.view.common.BaseActivity;
import com.melonltd.naber.view.factory.PageType;
import com.melonltd.naber.view.user.UserMainActivity;
import com.melonltd.naber.vo.AccountInfoVo;
import com.melonltd.naber.vo.ActivitiesVo;
import com.melonltd.naber.vo.ContactInfo;
import com.melonltd.naber.vo.ReqData;
import com.melonltd.naber.vo.SubjectionRegionVo;

import java.util.List;

import static com.melonltd.naber.model.constant.NaberConstant.SUBJECTION_REGION;

public class UserBonusExchangeDetailFragment extends Fragment {
    private static final String TAG = UserBonusExchangeDetailFragment.class.getSimpleName();
    public static UserBonusExchangeDetailFragment FRAGMENT = null;
    public ViewHolder holder;
    private List<SubjectionRegionVo> subjectionRegionVo = Lists.newArrayList();

    private int CITY_INDEX = 0;
    private int AREA_INDEX = 0;

    public Fragment getInstance(Bundle bundle) {
        if (FRAGMENT == null) {
            FRAGMENT = new UserBonusExchangeDetailFragment();
            FRAGMENT.setArguments(bundle);
        }
        return FRAGMENT;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subjectionRegionVo.clear();
        Fresco.initialize(getContext());
        ApiManager.getSubjectionRegions(new ThreadCallback(getContext()) {
            @Override
            public void onSuccess(String responseBody) {
                List<SubjectionRegionVo> subjectionRegionVos = Tools.JSONPARSE.fromJsonList(responseBody, SubjectionRegionVo[].class);
                subjectionRegionVo.addAll(subjectionRegionVos);
            }

            @Override
            public void onFail(Exception error, String msg) {
                subjectionRegionVo.addAll(Tools.JSONPARSE.fromJsonList(SUBJECTION_REGION, SubjectionRegionVo[].class));
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container.getTag(R.id.user_bonus_exchange_detail_page) == null) {
            View v = inflater.inflate(R.layout.fragment_user_bonus_exchange_detail, container, false);
            getViews(v);
            container.setTag(R.id.user_bonus_exchange_detail_page, v);
            return v;
        }
        return (View) container.getTag(R.id.user_bonus_exchange_detail_page);
    }

    private void getViews(View v) {
        holder = new ViewHolder(v);
    }

    @Override
    public void onResume() {
        super.onResume();

        UserMainActivity.changeTabAndToolbarStatus();
        if (UserMainActivity.toolbar != null) {
            UserMainActivity.navigationIconDisplay(true, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UserMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.USER_BONUS_EXCHANGE, null);
                }
            });
        }

        holder.emailEdit.setText("");
        holder.cityEdit.setText("");
        holder.areaEdit.setText("");
        holder.addressEdit.setText("");

        holder.activitiesVo = (ActivitiesVo) getArguments().getSerializable("BONUS_DETAIL");
        holder.titleText.setText(holder.activitiesVo.title);
        holder.needBonusText.setText("所需紅利(" + holder.activitiesVo.need_bonus + ")");

        if (!Strings.isNullOrEmpty(holder.activitiesVo.photo)) {
            holder.photoImage.setImageURI(Uri.parse(holder.activitiesVo.photo));
        } else {
            ImageRequest request = ImageRequestBuilder.newBuilderWithResourceId(R.drawable.naber_default_image).build();
            holder.photoImage.setImageURI(request.getSourceUri());
        }

        holder.serial_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean errMsg = verifyInput();
                if(errMsg){
                    new AlertView.Builder()
                            .setContext(getContext())
                            .setStyle(AlertView.Style.Alert)
                            .setTitle("系統提示")
                            .setMessage("確定使用" + holder.activitiesVo.need_bonus + "紅利, 兌換" + holder.activitiesVo.title )
                            .setOthers(new String[]{"確定","取消"})
                            .setOnItemClickListener(new OnItemClickListener() {
                                @Override
                                public void onItemClick(Object o, int position) {
                                    if(position == 0){
                                        //TODO
                                        final ReqData req = new ReqData();
                                        req.uuid = holder.activitiesVo.act_uuid;

                                        ContactInfo contact = new ContactInfo();
                                        contact.name = holder.nameEdit.getText().toString();
                                        contact.phone = holder.phoneEdit.getText().toString();
                                        contact.email = holder.emailEdit.getText().toString();
                                        contact.city = holder.cityEdit.getText().toString();
                                        contact.area = holder.areaEdit.getText().toString();
                                        contact.code = subjectionRegionVo.get(CITY_INDEX).areas.get(AREA_INDEX).postal_code;
                                        contact.address = holder.addressEdit.getText().toString();

                                        req.data = Tools.JSONPARSE.toJson(contact);

                                    ApiManager.actSubmit(req, new ThreadCallback(getContext()) {
                                        @Override
                                        public void onSuccess(String responseBody) {
                                            new Handler().postDelayed(new RunAlert("系統提示", "兌換成功，Naber專人將與您聯", true), 500);
                                        }

                                        @Override
                                        public void onFail(Exception error, String msg) {
                                            new Handler().postDelayed(new RunAlert("系統提示", msg,false), 500);

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



        ApiManager.userFindAccountInfo(new ThreadCallback(getContext()) {
            @Override
            public void onSuccess(String responseBody) {
                holder.accountInfoVo = Tools.JSONPARSE.fromJson(responseBody, AccountInfoVo.class);
                holder.nameEdit.setText(holder.accountInfoVo.name);
                holder.phoneEdit.setText(holder.accountInfoVo.phone);
                int bonus = IntegerTools.parseInt(holder.accountInfoVo.bonus, 0);
                int use_bonus = IntegerTools.parseInt(holder.accountInfoVo.use_bonus, 0);
                holder.haveBonusText.setText("擁有紅利 " + (bonus - use_bonus));
            }

            @Override
            public void onFail(Exception error, String msg) {

            }
        });
    }


    class RunAlert implements Runnable {
        private String title, msg;
        private boolean isSuccess;

        RunAlert (String title, String msg, boolean isSuccess){
            this.title = title;
            this.msg = msg;
            this.isSuccess = isSuccess;

        }

        @Override
        public void run() {
            new AlertView.Builder()
                    .setContext(getContext())
                    .setStyle(AlertView.Style.Alert)
                    .setTitle(this.title)
                    .setMessage(this.msg)
                    .setOthers(new String[]{"我知道了"})
                    .setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int position) {
                            if (isSuccess){
                                // TODO 成功按下我知道了 需要跳到上一頁。
                                UserMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.USER_BONUS_EXCHANGE, null);
                            }
                        }
                    })
                    .build()
                    .setCancelable(false)
                    .show();
        }
    }

    class ViewHolder {
        TextView titleText, needBonusText, haveBonusText;
        EditText nameEdit, phoneEdit, emailEdit, addressEdit;
        TextView cityEdit, areaEdit;
        Button serial_btn;
        SimpleDraweeView photoImage;
        AccountInfoVo accountInfoVo;
        ActivitiesVo activitiesVo;

        ViewHolder(View v) {
            this.photoImage = v.findViewById(R.id.photoImage);
            this.titleText = v.findViewById(R.id.titleText);
            this.needBonusText = v.findViewById(R.id.needBonusText);
            this.haveBonusText = v.findViewById(R.id.haveBonusText);
            this.nameEdit = v.findViewById(R.id.nameEdit);
            this.phoneEdit = v.findViewById(R.id.phoneEdit);
            this.emailEdit = v.findViewById(R.id.emailEdit);
            this.cityEdit = v.findViewById(R.id.cityEdit);
            this.cityEdit.setOnClickListener(new pickCityChoose());
            this.areaEdit = v.findViewById(R.id.areaEdit);
            this.areaEdit.setOnClickListener(new pickAreaChoose());
            this.addressEdit = v.findViewById(R.id.addressEdit);
            this.serial_btn = v.findViewById(R.id.serial_btn);
            HideKeyboard hideKeyboard = new HideKeyboard();
            nameEdit.setOnFocusChangeListener(hideKeyboard);
            emailEdit.setOnFocusChangeListener(hideKeyboard);
            cityEdit.setOnFocusChangeListener(hideKeyboard);
            areaEdit.setOnFocusChangeListener(hideKeyboard);
            addressEdit.setOnFocusChangeListener(hideKeyboard);
            areaEdit.setVisibility(View.GONE);
        }
    }

    class pickCityChoose implements View.OnClickListener {

        @Override
        public void onClick(final View view) {
//            AREA_INDEX = 0;
//            holder.areaEdit.setText("");
//            final TextView test = (TextView)view;
            // AREA 資料要隨著改變

            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            OptionsPickerView pvOptions = new OptionsPickerBuilder(getContext(), new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int index1, int option2, int options3, View v) {
                    AREA_INDEX = 0;
                    holder.areaEdit.setText("");
                    CITY_INDEX = index1;
                    holder.cityEdit.setText(subjectionRegionVo.get(index1).city);
                    holder.areaEdit.setVisibility(View.VISIBLE);
                }
            }).setTitleSize(20)
                    .setSubmitText("確定")//确定按钮文字
                    .setCancelText("取消")//取消按钮文字
                    .setTitleBgColor(getResources().getColor(R.color.naber_dividing_line_gray))
                    .setCancelColor(getResources().getColor(R.color.naber_dividing_gray))
                    .setSubmitColor(getResources().getColor(R.color.naber_dividing_gray))
                    .build();

            pvOptions.setPicker(subjectionRegionVo);
            pvOptions.show();
        }
    }

    class pickAreaChoose implements View.OnClickListener {
        @Override
        public void onClick(final View view) {


            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            OptionsPickerView pvOptions = new OptionsPickerBuilder(getContext(), new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int index1, int option2, int options3, View v) {

                    AREA_INDEX = index1;
                    holder.areaEdit.setText(subjectionRegionVo.get(CITY_INDEX).areas.get(index1).area);

                }
            }).setTitleSize(20)
                    .setSubmitText("確定")//确定按钮文字
                    .setCancelText("取消")//取消按钮文字
                    .setTitleBgColor(getResources().getColor(R.color.naber_dividing_line_gray))
                    .setCancelColor(getResources().getColor(R.color.naber_dividing_gray))
                    .setSubmitColor(getResources().getColor(R.color.naber_dividing_gray))
                    .build();

            pvOptions.setPicker(subjectionRegionVo.get(CITY_INDEX).areas);
            pvOptions.show();
        }
    }

    class HideKeyboard implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }

    private boolean verifyInput() {
        boolean result = true;
        String message = "";
        if (Strings.isNullOrEmpty(holder.addressEdit.getText().toString())) {
            message = "請確認已輸入完整地址";
            result = false;
        }
        if (Strings.isNullOrEmpty(holder.areaEdit.getText().toString())) {
            message = "請確認已選擇區域";
            result = false;
        }
        if (Strings.isNullOrEmpty(holder.cityEdit.getText().toString())) {
            message = "請確認已選擇縣市";
            result = false;
        }
        if (!VerifyUtil.email(holder.emailEdit.getText().toString())) {
            message = "請確輸入正確Email";
            result = false;
        }

        if (Strings.isNullOrEmpty(holder.emailEdit.getText().toString())) {
            message = "請確認已輸入Email";
            result = false;
        }

        if (Strings.isNullOrEmpty(holder.nameEdit.getText().toString())) {
            message = "請確認已輸入名稱";
            result = false;
        }
        // TODO 判斷使用者所剩下紅利
        if (holder.accountInfoVo != null) {
            int bonus = IntegerTools.parseInt(holder.accountInfoVo.bonus, 0);
            int need_bonus = IntegerTools.parseInt(holder.activitiesVo.need_bonus, 0);
            Log.i(TAG,"bonus");
            Log.i(TAG,"need_bonus");
            if (need_bonus > bonus) {
                message = "紅利點數不足兌換該項目";
                result = false;
            }
        }else {
            message = "紅利點數不足兌換該項目";
            result = false;
        }
        if (!result) {
            new AlertView.Builder()
                    .setTitle("")
                    .setMessage(message)
                    .setContext(getContext())
                    .setStyle(AlertView.Style.Alert)
                    .setCancelText("我知道了")
                    .build()
                    .setCancelable(true)
                    .show();
        }
        return result;
    }

}