package com.melonltd.naber.view.user.page;


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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnDismissListener;
import com.bigkoo.alertview.OnItemClickListener;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectChangeListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.melonltd.naber.R;
import com.melonltd.naber.model.api.ApiManager;
import com.melonltd.naber.model.api.ThreadCallback;
import com.melonltd.naber.model.constant.NaberConstant;
import com.melonltd.naber.model.service.SPService;
import com.melonltd.naber.model.type.BillingType;
import com.melonltd.naber.model.type.Delivery;
import com.melonltd.naber.util.IntegerTools;
import com.melonltd.naber.util.Tools;
import com.melonltd.naber.view.factory.PageType;
import com.melonltd.naber.view.user.UserMainActivity;
import com.melonltd.naber.vo.AccountInfoVo;
import com.melonltd.naber.vo.OrderDetail;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


public class UserSubmitOrdersFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = UserSubmitOrdersFragment.class.getSimpleName();
    public static UserSubmitOrdersFragment FRAGMENT = null;
    private TextView selectDateText, userNameText, userPhoneNumberText, ordersPriceText, ordersBonusText, bounsChooseText,mealText;
    private EditText userMessageEdit;
    private TimePickerView timePickerView;
    private CheckBox readRuleCheckBtn;
//    private int dataIndex = -1;
    private List<String> options1Items = Lists.newArrayList();
    private List<String> options2Items = Lists.newArrayList();
    private Handler handler = new Handler();
    private int useBonus = -1;

    private List<OrderDetail> cacheSoppingCar = Lists.<OrderDetail>newArrayList();
    private OrderDetail orderDetail = OrderDetail.getDefInstance();

    public UserSubmitOrdersFragment() {

    }

    public Fragment getInstance(Bundle bundle) {
        if (FRAGMENT == null) {
            FRAGMENT = new UserSubmitOrdersFragment();
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
        if (container.getTag(R.id.user_submit_order_page) == null) {
            View v = inflater.inflate(R.layout.fragment_user_submit_orders, container, false);
            getViews(v);
//            initOptionData();
            container.setTag(R.id.user_submit_order_page, v);
        }
        return (View) container.getTag(R.id.user_submit_order_page);
    }

    private void getViews(View v) {
        selectDateText = v.findViewById(R.id.selectDateText);
        userNameText = v.findViewById(R.id.userNameText);
        userPhoneNumberText = v.findViewById(R.id.userPhoneText);
        ordersPriceText = v.findViewById(R.id.ordersPriceText);
        ordersBonusText = v.findViewById(R.id.ordersBonusText);
        userMessageEdit = v.findViewById(R.id.userMessageEdit);
        readRuleCheckBtn = v.findViewById(R.id.readRuleCheckBtn);
        bounsChooseText = v.findViewById(R.id.bounsChooseText);
        mealText = v.findViewById(R.id.mealText);
        Button submitOrdersBtn = v.findViewById(R.id.submitOrdersBtn);
        HideKeyboard hideKeyboard = new HideKeyboard();

        bounsChooseText.setOnFocusChangeListener(hideKeyboard);
        mealText.setOnFocusChangeListener(hideKeyboard);
        options2Items.add("內用");
        options2Items.add("外帶");

        bounsChooseText.setOnClickListener(new pickBounsChoose());
        mealText.setOnClickListener(new pickMeal());
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
        useBonus = -1;
        bounsChooseText.setText("");
        UserMainActivity.changeTabAndToolbarStatus();
        if (UserMainActivity.toolbar != null) {
            UserMainActivity.navigationIconDisplay(true, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UserShoppingCartFragment.TO_SUBMIT_ORDERS_PAGE_INDEX = -1;
                    UserMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.USER_SHOPPING_CART, null);
                    UserMainActivity.navigationIconDisplay(false, null);
                }
            });
        }

        int dataIndex = getArguments().getInt(NaberConstant.ORDER_DETAIL_INDEX);
        this.cacheSoppingCar = SPService.getUserCacheShoppingCarData();

        if (dataIndex < this.cacheSoppingCar.size()){
            this.orderDetail = cacheSoppingCar.get(dataIndex);
        }

        ApiManager.userFindAccountInfo(new ThreadCallback(getContext()) {
            @Override
            public void onSuccess(String responseBody) {
                AccountInfoVo account = Tools.JSONPARSE.fromJson(responseBody, AccountInfoVo.class);
                options1Items.clear();
                int userBonus = IntegerTools.parseInt(account.bonus, 0);
                int useBonus = IntegerTools.parseInt(account.use_bonus, 0);
                int canBonus = (userBonus - useBonus)/10;
                int price = 0;
//                for (int i = 0; i < Model.USER_CACHE_SHOPPING_CART.get(dataIndex).orders.size(); i++) {
//                    price += Integer.parseInt(Model.USER_CACHE_SHOPPING_CART.get(dataIndex).orders.get(i).item.price);
//                }

//                if count <= 0 {
//                    self.selectBnonus.placeholder = "紅利不足折抵"
//                } else if price < 3 {
//                    self.selectBnonus.placeholder = "該品項無法折抵"
//                } else if count > 0 {
                for (int i = 0; i < orderDetail.orders.size(); i++) {
                    price += Integer.parseInt(orderDetail.orders.get(i).item.price);
                }
                if(canBonus < 1){
                    //TODO 代表無點數可折抵 ordersBonusText.setText("")
                    bounsChooseText.setText("紅利點數不足");
                    bounsChooseText.setEnabled(false);
                } else if( useBonus >= userBonus){
                    //TODO 因多端登入，有可能超出以使用所得 ordersBonusText.setText("")
                    bounsChooseText.setText("紅利點數不足");
                    bounsChooseText.setEnabled(false);
                } else if(price < 3){
                    //TODO 因為訂單價格不滿3元 ordersBonusText.setText("")
                    bounsChooseText.setText("價格不滿3元");
                    bounsChooseText.setEnabled(false);
                }else {
                    for (int i = 1; i < canBonus+1; i++) {
                        if( i > (price/3)){

                        } else {
                            options1Items.add((i * 10) + "點紅利,折抵" + (i * 3) + "元");
                            bounsChooseText.setEnabled(true);
                        }
                    }
                }
            }
            @Override
            public void onFail(Exception error, String msg) {

            }
        });
        mealText.setText("外帶");
//        Model.USER_CACHE_SHOPPING_CART.get(dataIndex).fetch_date = "";
        this.orderDetail.fetch_date = "";
        userNameText.setText(SPService.getUserName());
        userPhoneNumberText.setText(SPService.getUserPhone());
//        userMessageEdit.setText(Model.USER_CACHE_SHOPPING_CART.get(dataIndex).user_message);
        userMessageEdit.setText(this.orderDetail.user_message);
        int amount = 0;
//        for (int i = 0; i < Model.USER_CACHE_SHOPPING_CART.get(dataIndex).orders.size(); i++) {
//            amount += Integer.parseInt(Model.USER_CACHE_SHOPPING_CART.get(dataIndex).orders.get(i).item.price);
//        }
        for (int i = 0; i < this.orderDetail.orders.size(); i++) {
            amount += Integer.parseInt(this.orderDetail.orders.get(i).item.price);
        }
        readRuleCheckBtn.setChecked(false);
        ordersPriceText.setText("$ " + amount);

        if (this.orderDetail.can_discount.equals("N")) {
            ordersBonusText.setText("該店家不提供紅利");
        } else {
            ordersBonusText.setText("應得紅利 " + ((int) Math.floor(amount / 10d)) + "");
        }
//        if (Model.USER_CACHE_SHOPPING_CART.get(dataIndex).can_discount.equals("N")) {
//            ordersBonusText.setText("該店家不提供紅利");
//        } else {
//            ordersBonusText.setText("應得紅利 " + ((int) Math.floor(amount / 10d)) + "");
//        }
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
                        orderDetail.fetch_date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSS'Z'").format(date);
//                        Model.USER_CACHE_SHOPPING_CART.get(dataIndex).fetch_date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSS'Z'").format(date);
                        selectDateText.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date));
                    }
                })
                .setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
                    @Override
                    public void onTimeSelectChanged(Date date) {
                        if (startDate.getTime().getTime() > date.getTime()) {
//                            Model.USER_CACHE_SHOPPING_CART.get(dataIndex).fetch_date = "";
                            orderDetail.fetch_date = "";
                            setDate(startDate);
                        }
                        if (endDate.getTime().getTime() < date.getTime()) {
//                            Model.USER_CACHE_SHOPPING_CART.get(dataIndex).fetch_date = "";
                            orderDetail.fetch_date = "";
                            setDate(endDate);
                        }
                    }
                }).setType(new boolean[]{true, true, true, true, true, false})
                .setTitleSize(20)
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

    class OnResponseAlert implements Runnable {
        private String msg;
        private boolean isSuccess;

        OnResponseAlert(String msg, boolean isSuccess) {
            this.msg = msg;
            this.isSuccess = isSuccess;
        }

        @Override
        public void run() {
            if (this.isSuccess) {
//                cacheSoppingCar.remove(dataIndex);
                cacheSoppingCar.remove(orderDetail);
                SPService.setUserCacheShoppingCarData(cacheSoppingCar);
//                Model.USER_CACHE_SHOPPING_CART.remove(dataIndex);
//                SPService.setUserCacheShoppingCarData(Model.USER_CACHE_SHOPPING_CART);
            }

            new AlertView.Builder()
                    .setTitle("")
                    .setMessage(this.msg)
                    .setContext(getContext())
                    .setStyle(AlertView.Style.Alert)
                    .setOthers(new String[]{"我知道了"})
                    .build()
                    .setOnDismissListener(new OnDismissListener() {
                        @Override
                        public void onDismiss(Object o) {
                            if (isSuccess) {
                                UserShoppingCartFragment.TO_SUBMIT_ORDERS_PAGE_INDEX = -1;
                                UserMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.USER_ORDER_HISTORY, null);
                            }
                        }
                    })
                    .show();
        }
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
                this.orderDetail.user_message = userMessageEdit.getText().toString();
//                Model.USER_CACHE_SHOPPING_CART.get(dataIndex).user_message = userMessageEdit.getText().toString();
//                if (Strings.isNullOrEmpty(Model.USER_CACHE_SHOPPING_CART.get(dataIndex).fetch_date)) {
                if (Strings.isNullOrEmpty(this.orderDetail.fetch_date)) {
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
//                                                OrderDetail orderDetail = Model.USER_CACHE_SHOPPING_CART.get(dataIndex);
                                                IntegerTools.parseInt(orderDetail.use_bonus,0);

                                                if(useBonus > 0){
                                                    orderDetail.order_type.billing = BillingType.DISCOUNT;
                                                    orderDetail.use_bonus = String.valueOf(useBonus);
                                                } else {
                                                    orderDetail.order_type.billing = BillingType.ORIGINAL;
                                                    orderDetail.use_bonus = "0";
                                                }

                                                orderDetail.order_type.delivery = mealText.getText().toString().equals("外帶") ? Delivery.OUT : Delivery.IN;
//                                                ApiManager.userOrderSubmit(Model.USER_CACHE_SHOPPING_CART.get(dataIndex), new ThreadCallback(getContext()) {
                                                ApiManager.userOrderSubmit(orderDetail, new ThreadCallback(getContext()) {

                                                    @Override
                                                    public void onSuccess(String responseBody) {
                                                        handler.postDelayed(new OnResponseAlert(
                                                                "商家已看到您的訂單囉！\n" +
                                                                        "你可前往訂單頁面查看商品狀態，\n" +
                                                                        "提醒您，商品只保留至取餐時間後20分鐘。",
                                                                true), 500);
                                                    }

                                                    @Override
                                                    public void onFail(Exception error, String msg) {
                                                        Iterator<String> iterator = Splitter.on("$split").split(msg).iterator();
                                                        String content_text = "";
                                                        while (iterator.hasNext()) {
                                                            content_text += iterator.next() + "\n";
                                                        }
                                                        handler.postDelayed(new OnResponseAlert(content_text, false), 500);
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
//                submitOrder();
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
    class pickBounsChoose implements View.OnClickListener {
        @Override
        public void onClick(final View view) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            OptionsPickerView pvOptions = new OptionsPickerBuilder(getContext(), new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int index1, int option2, int options3, View v) {
                    STATUS = 1;
                    useBonus =(index1+1)*10;
                    int countBonus = (index1+1)*3;
                        int amount = 0;
//                        for (int i = 0; i < Model.USER_CACHE_SHOPPING_CART.get(dataIndex).orders.size(); i++) {
//                            amount += Integer.parseInt(Model.USER_CACHE_SHOPPING_CART.get(dataIndex).orders.get(i).item.price);
//                        }
                        for (int i = 0; i < orderDetail.orders.size(); i++) {
                            amount += Integer.parseInt(orderDetail.orders.get(i).item.price);
                        }
                        ordersPriceText.setText("$ " + (amount- countBonus));
//                        if (Model.USER_CACHE_SHOPPING_CART.get(dataIndex).can_discount.equals("N")) {
                        if (orderDetail.can_discount.equals("N")) {
                            ordersBonusText.setText("該店家不提供紅利");
                        } else {
                            ordersBonusText.setText("應得紅利 " + ((int) Math.floor((amount - countBonus) / 10d)) + "");
                        }
                    bounsChooseText.setText(options1Items.get(index1));
                }
            }) .setTitleSize(20)
                    .setSubmitText("選擇紅利")//确定按钮文字
                    .setCancelText("取消折抵")//取消按钮文字
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
                        useBonus = -1 ;
                        bounsChooseText.setText("");
                        int amount = 0;
                        for (int i = 0; i < orderDetail.orders.size(); i++) {
                            amount += Integer.parseInt(orderDetail.orders.get(i).item.price);
                        }
                        ordersPriceText.setText("$ " + amount);

                        if (orderDetail.can_discount.equals("N")) {
                            ordersBonusText.setText("該店家不提供紅利");
                        } else {
                            ordersBonusText.setText("應得紅利 " + ((int) Math.floor( amount / 10d)) + "");
                        }

//                        for (int i = 0; i < Model.USER_CACHE_SHOPPING_CART.get(dataIndex).orders.size(); i++) {
//                            amount += Integer.parseInt(Model.USER_CACHE_SHOPPING_CART.get(dataIndex).orders.get(i).item.price);
//                        }
//                        ordersPriceText.setText("$ " + amount);
//
//                        if (Model.USER_CACHE_SHOPPING_CART.get(dataIndex).can_discount.equals("N")) {
//                            ordersBonusText.setText("該店家不提供紅利");
//                        } else {
//                            ordersBonusText.setText("應得紅利 " + ((int) Math.floor( amount / 10d)) + "");
//                        }
                    }
                }
            });
            pvOptions.setPicker(options1Items);
            pvOptions.show();
        }
    }
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
}


