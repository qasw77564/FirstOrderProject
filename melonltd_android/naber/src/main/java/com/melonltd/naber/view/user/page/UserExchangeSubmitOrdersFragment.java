package com.melonltd.naber.view.user.page;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectChangeListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.melonltd.naber.R;
import com.melonltd.naber.model.api.ApiManager;
import com.melonltd.naber.model.api.ThreadCallback;
import com.melonltd.naber.model.service.SPService;
import com.melonltd.naber.model.type.BillingType;
import com.melonltd.naber.model.type.Delivery;
import com.melonltd.naber.util.Tools;
import com.melonltd.naber.util.UiUtil;
import com.melonltd.naber.view.factory.PageType;
import com.melonltd.naber.view.user.UserMainActivity;
import com.melonltd.naber.vo.ActivitiesVo;
import com.melonltd.naber.vo.DemandsItemVo;
import com.melonltd.naber.vo.ItemVo;
import com.melonltd.naber.vo.OrderDetail;
import com.melonltd.naber.vo.ReqData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserExchangeSubmitOrdersFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = UserExchangeSubmitOrdersFragment.class.getSimpleName();
    public static UserExchangeSubmitOrdersFragment FRAGMENT = null;
    private TextView selectDateText, userNameText, userPhoneNumberText,mealText,exchangeTitle;
    private ListView exchangeListView;
    private EditText userMessageEdit;
    private TimePickerView timePickerView;
    private CheckBox readRuleCheckBtn;


    private  ActivitiesVo activities;
    private  OrderDetail detail;
    private List<String> options2Items = Lists.newArrayList("外帶", "內用");
    private Handler handler = new Handler();

    public UserExchangeSubmitOrdersFragment() {

    }

    public Fragment getInstance(Bundle bundle) {
        UserExchangeSubmitOrdersFragment fragment = new UserExchangeSubmitOrdersFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_exchange_submit_orders, container, false);
        getViews(v);
        return v;
    }

    private void getViews(View v) {
        selectDateText = v.findViewById(R.id.selectDateText);
        userNameText = v.findViewById(R.id.userNameText);
        userPhoneNumberText = v.findViewById(R.id.userPhoneText);
        userMessageEdit = v.findViewById(R.id.userMessageEdit);
        readRuleCheckBtn = v.findViewById(R.id.readRuleCheckBtn);
        mealText = v.findViewById(R.id.mealText);
        exchangeTitle = v.findViewById(R.id.exchangeTitle);
        exchangeListView = v.findViewById(R.id.exchangeDatas);
        exchangeListView.setAdapter(new OrderAdapter());
        // TODO
        exchangeListView.setClickable(false);
        exchangeListView.setItemsCanFocus(false);
        exchangeListView.dispatchSetSelected(false);

        Button submitOrdersBtn = v.findViewById(R.id.submitOrdersBtn);
        UserExchangeSubmitOrdersFragment.HideKeyboard hideKeyboard = new UserExchangeSubmitOrdersFragment.HideKeyboard();
        mealText.setOnFocusChangeListener(hideKeyboard);
        mealText.setOnClickListener(new UserExchangeSubmitOrdersFragment.pickMeal());
        selectDateText.setOnClickListener(this);
        submitOrdersBtn.setOnClickListener(this);
        readRuleCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readRuleCheckBtn.setChecked(false);

                View extView = getLayoutInflater().inflate(R.layout.common_intro, null);
                TextView title = extView.findViewById(R.id.title);
                TextView body = extView.findViewById(R.id.body);
                title.setText("使用者規範");
                body.setText("1.您同意無論任何理由，您都會在所填選擇之取餐時間到場取餐。\n" +
                        "2.您非常同意，取餐時間、外送地址是由您本人自行填寫。\n" +
                        "3.當您使用外送服務時，您全權擔保您所填寫的地址，可以聯繫到您本人。\n" +
                        "4.當我們接獲店家投訴，您棄單造成店家損失，我們可能會與您聯繫。\n" +
                        "5.當您棄單造成店家損失，我們可能會依照法律途徑處理。");

                new AlertView.Builder()
                        .setContext(getContext())
                        .setStyle(AlertView.Style.Alert)
                        .setOthers(new String[]{"我知道了"})
                        .setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(Object o, int position) {
                                readRuleCheckBtn.setChecked(true);
                            }
                        })
                        .build()
                        .addExtView(extView)
                        .setCancelable(false)
                        .show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        UserMainActivity.changeTabAndToolbarStatus();
        if (UserMainActivity.toolbar != null) {
            UserMainActivity.navigationIconDisplay(true, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UserShoppingCartFragment.TO_SUBMIT_ORDERS_PAGE_INDEX = -1;
                    UserMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.USER_BONUS_EXCHANGE, null);
                    UserMainActivity.navigationIconDisplay(false, null);
                }
            });
        }

        Bundle bundle = this.getArguments();

        activities = (ActivitiesVo)bundle.getSerializable("ACTIVITIES");
        detail = (OrderDetail)bundle.getSerializable("ORDER_DETAIL");
        BaseAdapter adapter = (BaseAdapter) exchangeListView.getAdapter();
        adapter.notifyDataSetChanged();
        UiUtil.setListViewHeightBasedOnChildren(exchangeListView);

        // TODO set activities.title to TextView
        mealText.setText("外帶");
        exchangeTitle.setText(activities.title);
        userNameText.setText(SPService.getUserName());
        userPhoneNumberText.setText(SPService.getUserPhone());
        userMessageEdit.setText("");
        readRuleCheckBtn.setChecked(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        UserMainActivity.navigationIconDisplay(false, null);
        selectDateText.setText("");
        userMessageEdit.setText("");
    }

    private void showTimePicker() {
        long minutes_20 = 1000 * 60 * 20L;
        long day_3 = 1000 * 60 * 60 * 24 * 3L;
        Calendar now = Calendar.getInstance();
        final Calendar startDate = Calendar.getInstance();
        startDate.setTimeInMillis(now.getTime().getTime() + minutes_20);
        final Calendar endDate = Calendar.getInstance();
        endDate.setTimeInMillis(now.getTime().getTime() + day_3);

        timePickerView = new TimePickerBuilder(getContext(),
                new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        selectDateText.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date));
                        selectDateText.setTag(Tools.FORMAT.formatDate(date));
                    }
                })
                .setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
                    @Override
                    public void onTimeSelectChanged(Date date) {
                        if (startDate.getTime().getTime() > date.getTime()) {
                            selectDateText.setTag(Tools.FORMAT.formatDate(date));
                            setDate(startDate);
                        }
                        if (endDate.getTime().getTime() < date.getTime()) {
                            selectDateText.setTag(Tools.FORMAT.formatDate(date));
                            setDate(endDate);
                        }
                    }
                }).setType(new boolean[]{true, true, true, true, true, false})
                .setTitleSize(20)
                .setCancelText("取消")
                .setSubmitText("確定")
                .setOutSideCancelable(true)
                .isCyclic(false)
                .setTitleBgColor(getResources().getColor(R.color.naber_dividing_line_gray))
                .setCancelColor(getResources().getColor(R.color.naber_dividing_gray))
                .setSubmitColor(getResources().getColor(R.color.naber_dividing_gray))
                .setDate(startDate)
                .setRangDate(startDate, endDate)
                .isCenterLabel(false)
                .isDialog(false)
                .build();

        timePickerView.show();
    }

    /**
     * 重要function 因為無法六及聯動，強制返回時間範圍限制
     *
     * @param date
     */
    private void setDate(Calendar date) {
        timePickerView.setDate(date);
        timePickerView.returnData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        switch (view.getId()) {
            case R.id.selectDateText:
                showTimePicker();
                break;
            case R.id.submitOrdersBtn:
                // TODO
                if (Strings.isNullOrEmpty(selectDateText.getText().toString())) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            new AlertView.Builder()
                                    .setContext(getContext())
                                    .setTitle("")
                                    .setMessage("請選擇取餐時間，才可以送出訂單。")
                                    .setStyle(AlertView.Style.Alert)
                                    .setOthers(new String[]{"我知道了"})
                                    .build()
                                    .show();
                        }
                    }, 500);
                } else if (!readRuleCheckBtn.isChecked()) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            new AlertView.Builder()
                                    .setContext(getContext())
                                    .setTitle("")
                                    .setMessage("請先閱讀\"使用者規範\"，再提交訂單。")
                                    .setStyle(AlertView.Style.Alert)
                                    .setOthers(new String[]{"我知道了"})
                                    .build()
                                    .show();
                        }
                    }, 500);
                } else {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            new AlertView.Builder()
                                    .setContext(getContext())
                                    .setTitle("確認訂單")
                                    .setMessage("取餐時間: " + selectDateText.getText().toString())
                                    .setStyle(AlertView.Style.Alert)
                                    .setOthers(new String[]{"取消", "確認"})
                                    .setOnItemClickListener(new OnItemClickListener() {
                                        @Override
                                        public void onItemClick(Object o, int position) {
                                            if (position == 1) {
                                                detail.order_type = new OrderDetail.OrderType();
                                                detail.order_type.billing = BillingType.COUPON;
                                                detail.order_type.delivery = mealText.getText().toString().equals("外帶") ? Delivery.OUT : Delivery.IN;
                                                detail.fetch_date = (String)selectDateText.getTag();
                                                detail.use_bonus = "0";
                                                detail.can_discount = "";
                                                detail.user_name = SPService.getUserName();
                                                detail.user_phone = SPService.getUserPhone();
                                                detail.user_message = userMessageEdit.getText().toString();
                                                Log.i(TAG,detail.toString());
                                                ReqData req = new ReqData();
                                                req.uuid = activities.act_uuid;
                                                req.data = Tools.JSONPARSE.toJson(detail);
                                                Log.i(TAG,req.toString());
                                                ApiManager.resEventSubmit(req, new ThreadCallback(getContext()) {
                                                    @Override
                                                    public void onSuccess(String responseBody) {
                                                        UserMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.USER_BONUS_EXCHANGE, null);
                                                    }
                                                    @Override
                                                    public void onFail(Exception error, final String msg) {
                                                        handler.postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                new AlertView.Builder()
                                                                        .setContext(getContext())
                                                                        .setTitle("")
                                                                        .setMessage(msg)
                                                                        .setStyle(AlertView.Style.Alert)
                                                                        .setOthers(new String[]{"我知道了"})
                                                                        .build()
                                                                        .setCancelable(false)
                                                                        .show();
                                                            }
                                                        }, 500);
                                                    }
                                                });
                                            }
                                        }
                                    })
                                    .build()
                                    .setCancelable(false)
                                    .show();
                        }
                    }, 500);
                }
                break;

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

    public int STATUS = -1;
    class pickMeal implements View.OnClickListener{

        @Override
        public void onClick(final View view) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            OptionsPickerView pvOptions = new OptionsPickerBuilder(getContext(), new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int index1, int option2, int options3, View v) {
                    STATUS = 1;
                    String meal = options2Items.get(index1);
                    mealText.setText(meal);
                }
            }) .setTitleSize(20)
                    .setSubmitText("選擇取餐方式")//确定按钮文字
                    .setCancelText("取消")//取消按钮文字
                    .setTitleBgColor(getResources().getColor(R.color.naber_dividing_line_gray))
                    .setCancelColor(getResources().getColor(R.color.naber_dividing_gray))
                    .setSubmitColor(getResources().getColor(R.color.naber_dividing_gray))
                    .build();

            pvOptions.setOnDismissListener(new com.bigkoo.pickerview.listener.OnDismissListener() {
                @Override
                public void onDismiss(Object o) {
                    if(STATUS == 1){
                        STATUS = -1;
                        // TODO 代表按了確認
                    } else {
                        mealText.setText("");
                    }
                }
            });
            pvOptions.setPicker(options2Items);
            pvOptions.show();
        }
    }

    class OrderAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return detail != null ? detail.orders.size() : 0 ;
        }

        @Override
        public Object getItem(int i) {
            return detail.orders.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            // TODO
            ViewHolder holder;
            if (view == null){
                view = LayoutInflater.from(getContext()).inflate(R.layout.order_detail_datas_item,viewGroup, false);
                holder = new ViewHolder(view);
                view.setTag(holder);
            }else {
                holder = (ViewHolder) view.getTag();
            }

            OrderDetail.OrderData data = detail.orders.get(i);
            holder.countText.setText("X " + data.count);
            holder.foodNameText.setText(data.item.food_name);
            String datas = "";
            datas += "規格 :" ;
            if (!data.item.scopes.isEmpty()){
                for(int ii=0; ii<data.item.scopes.size(); ii++){
                    datas += data.item.scopes.get(ii).name + ", ";
                }
                datas += "\n";
            }else {
                datas += "預設, ";
                datas += "\n";
            }
            if (!data.item.opts.isEmpty()){
                datas += "附加 :" ;
                for(int ii=0; ii<data.item.opts.size(); ii++){
                    datas += data.item.opts.get(ii).name + ", ";
                }
                datas += "\n";
            }
            if(!data.item.demands.isEmpty()){
                datas += "需求: ";
                for(int ii=0; ii< data.item.demands.size(); ii++){
                    datas += data.item.demands.get(ii).name + " ：";
                    for(int jj=0; jj< data.item.demands.get(ii).datas.size(); jj++){
                        datas += data.item.demands.get(ii).datas.get(jj).name;
                    }
                    datas +="\n";
                }
            }
            holder.datasText.setText(datas);
            return view;
        }

        class ViewHolder {
            TextView foodNameText, countText, datasText;
            ViewHolder(View v){
                // TODO
                v.setClickable(false);
                this.foodNameText = v.findViewById(R.id.nameEdit);
                this.countText = v.findViewById(R.id.countText);
                this.datasText = v.findViewById(R.id.datasText);
            }
        }
    }
}
