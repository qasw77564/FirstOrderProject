package com.melonltd.naber.view.seller.page;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.melonltd.naber.R;
import com.melonltd.naber.model.constant.NaberConstant;
import com.melonltd.naber.model.type.OrderStatus;
import com.melonltd.naber.util.IntegerTools;
import com.melonltd.naber.util.Tools;
import com.melonltd.naber.view.factory.PageType;
import com.melonltd.naber.view.seller.SellerMainActivity;
import com.melonltd.naber.vo.DemandsItemVo;
import com.melonltd.naber.vo.ItemVo;
import com.melonltd.naber.vo.OrderDetail;
import com.melonltd.naber.vo.OrderVo;


public class SellerOrderLogsDetailFragment extends Fragment {
//    private static final String TAG = SellerOrderLogsDetailFragment.class.getSimpleName();
    public static SellerOrderLogsDetailFragment FRAGMENT = null;


    public SellerOrderLogsDetailFragment() {
    }

    public Fragment getInstance(Bundle bundle) {
        if (FRAGMENT == null) {
            FRAGMENT = new SellerOrderLogsDetailFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {// Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_seller_order_logs_detail, container, false);
        getViews(v);
        return v;
    }


    private void getViews(View v) {
        TextView foodCountText = v.findViewById(R.id.foodCountText);
        TextView foodContentText = v.findViewById(R.id.foodContentText);

        TextView userMessageText = v.findViewById(R.id.userMessageEdit);
        TextView fetchDateText = v.findViewById(R.id.fetchDateText);
        TextView userPhoneText = v.findViewById(R.id.userPhoneText);
        TextView userNameText = v.findViewById(R.id.userNameText);
        TextView priceText = v.findViewById(R.id.priceEdit);
        Button statusBtn = v.findViewById(R.id.statusBtn);

        OrderVo orderVo = (OrderVo) getArguments().getSerializable(NaberConstant.SELLER_STAT_LOGS_DETAIL);

        if (orderVo != null){
            foodCountText.setText(" (" +orderVo.order_detail.orders.size() +")");
            String content = "";
            for (OrderDetail.OrderData data :orderVo.order_detail.orders) {
                content += data.item.category_name + ": " +
                        Strings.padEnd(data.item.food_name, 20, '\u0020') +
                        Strings.padEnd(("x" + data.count), 15, '\u0020') +
                        "$ " + data.item.price +
                        "\n";

                content += "規格: " +
                        Strings.padEnd((data.item.scopes.get(0).name), 40 , '\u0020') +
                        "$ " + data.item.scopes.get(0).price+
                        "\n";

                content += "附加:";
                for (ItemVo item : data.item.opts) {
                    content += "\n" + Strings.padEnd(("    - " + item.name), 40, '\u0020') +
                            Strings.padEnd("  ", 10 , '\u0020') +
                            "$ " + item.price ;
                }
                content += data.item.opts.size() == 0 ? "無\n" : "\n";

                content += "需求: ";
                for (DemandsItemVo demands : data.item.demands) {
                    content += "" + demands.name + " : ";
                    for (ItemVo item : demands.datas) {
                        content += item.name + "";
                    }
                    content += ",  ";
                }
                content += "\n------------------------------------------------------\n";
            }

            foodContentText.setText(content);
            userMessageText.setText(orderVo.user_message);
            fetchDateText.setText(Tools.FORMAT.format(NaberConstant.DATE_FORMAT_PATTERN, "dd日 HH時 mm分", orderVo.fetch_date));
            userPhoneText.setText(orderVo.order_detail.user_phone);
            userNameText.setText(orderVo.order_detail.user_name);
            int use_bonus = IntegerTools.parseInt(orderVo.use_bonus,0);
            if(use_bonus > 0){
                int price = IntegerTools.parseInt(orderVo.order_price,0);
                priceText.setText(""+ (price - (use_bonus/10*3)));
            } else {
                priceText.setText(orderVo.order_price);
            }

            OrderStatus status = OrderStatus.of(orderVo.status);
            statusBtn.setText(status.getText());
            statusBtn.setBackgroundColor(getResources().getColor(status.getColor()));
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        SellerMainActivity.changeTabAndToolbarStatus();
        if (SellerMainActivity.toolbar != null) {
            SellerMainActivity.navigationIconDisplay(true, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SellerOrdersLogsFragment.TO_ORDERS_LOGS_DETAIL_INDEX = -1;
                    SellerMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.SELLER_ORDERS_LOGS, null);
                    SellerMainActivity.navigationIconDisplay(false, null);
                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        SellerMainActivity.navigationIconDisplay(false, null);

    }
}
