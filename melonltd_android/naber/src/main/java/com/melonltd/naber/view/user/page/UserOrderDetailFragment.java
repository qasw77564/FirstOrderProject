package com.melonltd.naber.view.user.page;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.melonltd.naber.R;
import com.melonltd.naber.model.constant.NaberConstant;
import com.melonltd.naber.util.IntegerTools;
import com.melonltd.naber.util.Tools;
import com.melonltd.naber.util.UiUtil;
import com.melonltd.naber.view.factory.PageType;
import com.melonltd.naber.view.user.UserMainActivity;
import com.melonltd.naber.vo.OrderDetail;
import com.melonltd.naber.vo.OrderVo;

import java.util.List;

public class UserOrderDetailFragment extends Fragment {
//    private static final String TAG = UserOrderDetailFragment.class.getSimpleName();
    public static UserOrderDetailFragment FRAGMENT = null;
    private ViewHolder holder;

    private List<OrderDetail.OrderData> orders = Lists.newArrayList();
    public UserOrderDetailFragment() {
    }

    public Fragment getInstance(Bundle bundle) {
        if (FRAGMENT == null) {
            FRAGMENT = new UserOrderDetailFragment();
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
        if (container.getTag(R.id.user_order_detail_page) == null) {
            View v = inflater.inflate(R.layout.fragment_user_order_detail, container, false);
            holder = new ViewHolder(v);
            setValue();
            container.setTag(R.id.user_order_detail_page, v);
            return v;
        }
        setValue();
        return (View) container.getTag(R.id.user_order_detail_page);
    }

    public void setValue() {
        OrderVo vo = (OrderVo) getArguments().getSerializable(NaberConstant.ORDER_INFO);
        if (vo != null){
            orders.clear();
            OrderDetail orderDetail = Tools.JSONPARSE.fromJson(vo.order_data, OrderDetail.class);
            orders.addAll(orderDetail.orders);
            new Handler().post(new Runnable(){
                @Override
                public void run() {
                    UiUtil.setListViewHeightBasedOnChildren(holder.orderDatas);
                }
            });

            holder.sellerNameText.setText(orderDetail.restaurant_name);
            int use_bonus = IntegerTools.parseInt(vo.use_bonus,0);
            if(use_bonus > 0){
                int price = IntegerTools.parseInt(vo.order_price,0);
                holder.priceText.setText("$ " + (price - (use_bonus/10*3)));
            } else {
                holder.priceText.setText("$ " + vo.order_price);
            }
            holder.addressText.setText(orderDetail.restaurant_address);
            holder.messageText.setText(vo.user_message);
            holder.bonusText.setText(vo.order_bonus);
            holder.usebonusText.setText(""+use_bonus);
            holder.orderingTimeText.setText(Tools.FORMAT.format(NaberConstant.DATE_FORMAT_PATTERN, "dd日 HH時 mm分", vo.create_date));
            holder.fetchDateText.setText(Tools.FORMAT.format(NaberConstant.DATE_FORMAT_PATTERN, "dd日 HH時 mm分", vo.fetch_date));

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        UserMainActivity.changeTabAndToolbarStatus();
        if (UserMainActivity.toolbar != null) {
            UserMainActivity.navigationIconDisplay(true, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UserOrderHistoryFragment.TO_ORDER_DETAIL_INDEX = -1;
                    UserMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.USER_ORDER_HISTORY, null);
                    UserMainActivity.navigationIconDisplay(false, null);
                }
            });
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

    class ViewHolder {
        TextView sellerNameText, priceText, addressText, messageText, bonusText, orderingTimeText, fetchDateText,usebonusText;
        ListView orderDatas;
        ViewHolder(View v) {
            this.orderDatas = v.findViewById(R.id.orderDatas);
            this.orderDatas.setAdapter(new OrderAdapter());
            this.sellerNameText = v.findViewById(R.id.sellerNameText);
            this.priceText = v.findViewById(R.id.priceEdit);
            this.addressText = v.findViewById(R.id.addressText);
            this.messageText = v.findViewById(R.id.messageText);
            this.bonusText = v.findViewById(R.id.bonusText);
            this.orderingTimeText = v.findViewById(R.id.orderingTimeText);
            this.fetchDateText = v.findViewById(R.id.fetchDateText);
            this.usebonusText = v.findViewById(R.id.use_bonusText);
        }
    }

    class OrderAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return orders.size();
        }

        @Override
        public Object getItem(int i) {
            return orders.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null){
                view = LayoutInflater.from(getContext()).inflate(R.layout.order_detail_datas_item,viewGroup, false);
                holder = new ViewHolder(view);
                view.setTag(holder);
            }else {
                holder = (ViewHolder)view.getTag();
            }

            holder.countText.setText("X " + orders.get(i).count);
            holder.foodNameText.setText(orders.get(i).item.food_name);
            String datas = "";
            datas += "規格 :" ;
            if (!orders.get(i).item.scopes.isEmpty()){
                for(int ii=0; ii<orders.get(i).item.scopes.size(); ii++){
                    datas += orders.get(i).item.scopes.get(ii).name + ", ";
                }
                datas += "\n";
            }else {
                datas += "預設, ";
                datas += "\n";
            }

            if (!orders.get(i).item.opts.isEmpty()){
                datas += "追加 :" ;
                for(int ii=0; ii<orders.get(i).item.opts.size(); ii++){
                    datas += orders.get(i).item.opts.get(ii).name + ", ";
                }
                datas += "\n";
            }
            for(int ii=0; ii<orders.get(i).item.demands.size(); ii++){
                datas += orders.get(i).item.demands.get(ii).name + " ：";
                for(int jj=0; jj< orders.get(i).item.demands.get(ii).datas.size(); jj++){
                    datas += orders.get(i).item.demands.get(ii).datas.get(jj).name;
                }
                datas +="\n";
            }
            holder.datasText.setText(datas);
            return view;
        }

        class ViewHolder {
            TextView foodNameText, countText, datasText;
            ViewHolder(View v){
                this.foodNameText = v.findViewById(R.id.nameEdit);
                this.countText = v.findViewById(R.id.countText);
                this.datasText = v.findViewById(R.id.datasText);
            }
        }
    }

}
