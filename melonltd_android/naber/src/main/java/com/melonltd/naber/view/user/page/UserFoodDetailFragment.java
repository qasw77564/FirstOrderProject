package com.melonltd.naber.view.user.page;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.google.common.collect.Lists;
import com.melonltd.naber.R;
import com.melonltd.naber.model.api.ApiManager;
import com.melonltd.naber.model.api.ThreadCallback;
import com.melonltd.naber.model.constant.NaberConstant;
import com.melonltd.naber.model.service.SPService;
import com.melonltd.naber.util.DensityUtil;
import com.melonltd.naber.util.Tools;
import com.melonltd.naber.view.customize.NaberCheckButton;
import com.melonltd.naber.view.customize.NaberRadioButton;
import com.melonltd.naber.view.factory.PageType;
import com.melonltd.naber.view.user.UserMainActivity;
import com.melonltd.naber.vo.DemandsItemVo;
import com.melonltd.naber.vo.FoodVo;
import com.melonltd.naber.vo.ItemVo;
import com.melonltd.naber.vo.OrderDetail;
import com.melonltd.naber.vo.RestaurantInfoVo;

import java.util.List;


public class UserFoodDetailFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = UserFoodDetailFragment.class.getSimpleName();
    public static UserFoodDetailFragment FRAGMENT = null;
    private TextView totalAmountText;
    private TextView quantityEditText;
    private LinearLayout contentLayout;
    private int RADIO_BUTTON_WIDTH = 0;
    private int RADIO_BUTTON_HIGH = 0;
    private OrderDetail.OrderData orderData = new OrderDetail.OrderData();
    private static List<OrderDetail> cacheShoppingCar = Lists.<OrderDetail>newArrayList();

    public UserFoodDetailFragment() {
    }

    public Fragment getInstance(Bundle bundle) {
        if (FRAGMENT == null) {
            FRAGMENT = new UserFoodDetailFragment();
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
        if (container.getTag(R.id.user_menu_detail_page) == null) {
            View v = inflater.inflate(R.layout.fragment_user_food_detail, container, false);
            getViews(v);
            container.setTag(R.id.user_menu_detail_page, v);
            return v;
        }
        return (View) container.getTag(R.id.user_menu_detail_page);
    }

    private void getViews(View v) {
        contentLayout = v.findViewById(R.id.menuDetailContentLinearLayout);
        quantityEditText = v.findViewById(R.id.quantityEditText);
        totalAmountText = v.findViewById(R.id.priceEdit);

        Button addToShopCartBtn = v.findViewById(R.id.addToShopCartBtn);
        ImageButton addBtn = v.findViewById(R.id.addBtn);
        ImageButton minusBtn = v.findViewById(R.id.minusBtn);

        // setListener
        addBtn.setOnClickListener(new AddMinusClickListener());
        minusBtn.setOnClickListener(new AddMinusClickListener());
        addToShopCartBtn.setOnClickListener(this);

        RADIO_BUTTON_WIDTH = UserMainActivity.LAYOUT_WIDTH - DensityUtil.dip2px(getContext(), 32);
        RADIO_BUTTON_HIGH = DensityUtil.dip2px(getContext(), 32);
    }

    @Override
    public void onResume() {
        super.onResume();

        this.cacheShoppingCar = SPService.getUserCacheShoppingCarData();
        UserMainActivity.changeTabAndToolbarStatus();
        if (UserMainActivity.toolbar != null) {
            UserMainActivity.navigationIconDisplay(true, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UserFoodListFragment.TO_MENU_DETAIL_INDEX = -1;
                    UserMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.USER_FOOD_LIST, null);
                    UserMainActivity.navigationIconDisplay(false, null);
                }
            });
        }

        contentLayout.removeAllViews();
//        totalAmount = 0;

        final FoodVo vo = (FoodVo) getArguments().getSerializable(NaberConstant.FOOD_INFO);

        if (vo != null) {
            UserMainActivity.toolbar.setTitle(vo.food_name);
            contentLayout.removeAllViews();
            orderData = new OrderDetail.OrderData();
            orderData.count = 1;
            orderData.category_uuid = vo.category_uuid;
            orderData.item.food_uuid = vo.food_uuid;
            ApiManager.restaurantFoodDetail(vo.food_uuid, new ThreadCallback(getContext()) {
                @Override
                public void onSuccess(String responseBody) {
                    FoodVo food = Tools.JSONPARSE.fromJson(responseBody, FoodVo.class);
                    getArguments().putString("FOOD_PHOTO", food.photo);
                    setScopeView(food.food_data.scopes);
                    setDemandView(food.food_data.demands);
                    setOptView(food.food_data.opts);
                    countTotalAmount();
                }

                @Override
                public void onFail(Exception error, String msg) {
                }
            });
        }
    }

    // 計算價格
    private void countTotalAmount() {
        int scopes = Integer.parseInt(orderData.item.scopes.get(0).price);
        int optPrice = 0;
        for (ItemVo opt : orderData.item.opts) {
            optPrice += Integer.parseInt(opt.price);
        }
        quantityEditText.setText(orderData.count + "");
        totalAmountText.setText(((scopes + optPrice) * orderData.count) + "");
    }

    private void setScopeView(final List<ItemVo> scopes) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.menu_detail_scope, null);
        final RadioGroup group = v.findViewById(R.id.scopeRadioGroup);

        for (int i = 0; i < scopes.size(); i++) {
            if (i == 0) {
                orderData.item.scopes.add(scopes.get(i));
            }
            RadioButton radio = new NaberRadioButton().Builder(getContext())
                    .setTitle(scopes.get(i).name)
                    .setPrice(scopes.get(i).price)
                    .setSymbol("$")
                    .setTag(scopes.get(i))
                    .setId(i + 669696)
                    .setChecked(i == 0 ? true : false)
                    .build();
            if (UserMainActivity.LAYOUT_WIDTH != 0 && RADIO_BUTTON_HIGH != 0) {
                group.addView(radio, RADIO_BUTTON_WIDTH, RADIO_BUTTON_HIGH);
            } else {
                group.addView(radio);
            }
        }

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radio = group.findViewById(checkedId);
                orderData.item.scopes.clear();
                orderData.item.scopes.add((ItemVo) radio.getTag());
                countTotalAmount();
            }
        });
        contentLayout.addView(v);
    }

    private void setDemandView(final List<DemandsItemVo> demands) {

        for (int i = 0; i < demands.size(); i++) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.menu_detail_demand, null);
            TextView demandText = v.findViewById(R.id.demandText);
            demandText.setText(demands.get(i).name);
            final RadioGroup group = v.findViewById(R.id.demandRadioGroup);

            final DemandsItemVo demand = new DemandsItemVo();
            for (int j = 0; j < demands.get(i).datas.size(); j++) {
                if (j == 0) {
                    demand.name = demands.get(i).name;
                    demand.datas.add(demands.get(i).datas.get(j));
                    orderData.item.demands.add(demand);
                }

                RadioButton radio = new NaberRadioButton().Builder(getContext())
                        .setTitle(demands.get(i).datas.get(j).name)
                        .setTag(demands.get(i).datas.get(j))
                        .setPrice("\u3000")
                        .setSymbol("")
                        .setId(j + 369646)
                        .setChecked(j == 0 ? true : false)
                        .build();
                if (UserMainActivity.LAYOUT_WIDTH != 0 && RADIO_BUTTON_HIGH != 0) {
                    group.addView(radio, RADIO_BUTTON_WIDTH, 80);
                } else {
                    group.addView(radio);
                }
            }
            group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    orderData.item.demands.remove(demand);
                    RadioButton radio = group.findViewById(checkedId);
                    demand.datas.clear();
                    demand.datas.add((ItemVo) radio.getTag());
                    orderData.item.demands.add(demand);
                }
            });
            contentLayout.addView(v);
        }
    }

    private void setOptView(final List<ItemVo> opts) {
        if (opts.size() > 0) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.menu_detail_optional, null);
            final LinearLayout optLayout = v.findViewById(R.id.optsLayout);
            for (int i = 0; i < opts.size(); i++) {

                final CheckBox box = new NaberCheckButton().Builder(getContext())
                        .setTitle(opts.get(i).name)
                        .setTag(opts.get(i))
                        .setSymbol("$")
                        .setPrice(opts.get(i).price)
                        .setChecked(false)
                        .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    orderData.item.opts.add((ItemVo) buttonView.getTag());
                                } else {
                                    orderData.item.opts.remove(buttonView.getTag());
                                }
                                countTotalAmount();
                            }
                        }).build();

                box.post(new Runnable() {
                    @Override
                    public void run() {
                        int width = box.getWidth();
                        int high = box.getHeight();
                        Log.i("RADIO_BUTTON_HIGH", RADIO_BUTTON_HIGH + "");
                        Log.i("View width", width + ":" + high);
                    }
                });
                optLayout.addView(box);
            }
            contentLayout.addView(v);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        UserMainActivity.navigationIconDisplay(false, null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        RestaurantInfoVo restaurantInfo = (RestaurantInfoVo) getArguments().getSerializable(NaberConstant.RESTAURANT_INFO);
        String categortName = getArguments().getString(NaberConstant.CATEGORY_NAME);
        orderData.item.price = totalAmountText.getText().toString();

        OrderDetail.OrderData data = new OrderDetail.OrderData();
        data = orderData;

//        data.restaurant_address = restaurantInfo.address;
//        data.restaurant_name = restaurantInfo.name;
//        data.user_name = SPService.getUserName();
        data.item.category_name = categortName;
        data.item.price = totalAmountText.getText().toString();
        data.item.food_name = UserMainActivity.toolbar.getTitle().toString();
        data.item.food_photo = getArguments().getString("FOOD_PHOTO");
        boolean has = false;
        for (OrderDetail o : this.cacheShoppingCar) {
            if (restaurantInfo.restaurant_uuid.equals(o.restaurant_uuid)) {
                o.restaurant_name = restaurantInfo.name;
                o.order_type = OrderDetail.OrderType.setDefault();
                o.restaurant_address = restaurantInfo.address;
                o.can_discount = restaurantInfo.can_discount;
                o.user_name = SPService.getUserName();
                o.user_phone = SPService.getUserPhone();
                o.orders.add(0, data);
                has = true;
            }
        }
        if (!has) {
            OrderDetail orderDetail = OrderDetail.ofOrders(Lists.newArrayList(data));
            orderDetail.restaurant_uuid = restaurantInfo.restaurant_uuid;
            orderDetail.restaurant_name = restaurantInfo.name;
            orderDetail.order_type = OrderDetail.OrderType.setDefault();
            orderDetail.restaurant_address = restaurantInfo.address;
            orderDetail.can_discount = restaurantInfo.can_discount;
            orderDetail.user_name = SPService.getUserName();
            orderDetail.user_phone = SPService.getUserPhone();
            this.cacheShoppingCar.add(0, orderDetail);
        }
        String msg = "規格：";
        msg += orderData.item.scopes.get(0).name + "\n";

        for (int i = 0; i < orderData.item.demands.size(); i++) {
            msg += orderData.item.demands.get(i).name + ":";
            msg += orderData.item.demands.get(i).datas.get(0).name + "\n";
        }

        if (orderData.item.opts.size() > 0) {
            msg += "追加項目：";
            for (int i = 0; i < orderData.item.opts.size(); i++) {
                msg += orderData.item.opts.get(i).name + ",";
            }
            msg += "\n";
        }
        msg += "數量：" + orderData.count + "\n";
        msg += "金額：" + totalAmountText.getText().toString() + "\n";

        new AlertView.Builder()
                .setContext(getContext())
                .setStyle(AlertView.Style.Alert)
                .setTitle("已成功加入購物車")
                .setMessage(msg)
                .setOthers(new String[]{"前往購物車", "繼續購物"})
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, int position) {
                        SPService.setUserCacheShoppingCarData(cacheShoppingCar);
                        if (position == 0) {
                            UserFoodListFragment.TO_MENU_DETAIL_INDEX = -1;
                            UserMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.USER_SHOPPING_CART, null);
                        } else {
                            UserRestaurantDetailFragment.TO_CATEGORY_MENU_INDEX = -1;
                            UserFoodListFragment.TO_MENU_DETAIL_INDEX = -1;
                            UserMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.USER_RESTAURANT_DETAIL, null);
                        }
                    }
                })
                .build()
                .setCancelable(false)
                .show();
    }

    class AddMinusClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.addBtn:
                    orderData.count++;
                    break;
                case R.id.minusBtn:
                    orderData.count--;
                    break;
            }

            if (orderData.count <= 0) {
                orderData.count = 1;
            } else if (orderData.count > 50) {
                orderData.count = 50;
            }
            quantityEditText.setText(orderData.count + "");
            countTotalAmount();
        }
    }

}
