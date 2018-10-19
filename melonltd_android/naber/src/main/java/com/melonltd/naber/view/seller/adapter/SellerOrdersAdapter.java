package com.melonltd.naber.view.seller.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.melonltd.naber.R;
import com.melonltd.naber.model.bean.Model;
import com.melonltd.naber.model.constant.NaberConstant;
import com.melonltd.naber.model.type.Delivery;
import com.melonltd.naber.model.type.OrderStatus;
import com.melonltd.naber.util.IntegerTools;
import com.melonltd.naber.util.Tools;
import com.melonltd.naber.vo.DemandsItemVo;
import com.melonltd.naber.vo.ItemVo;
import com.melonltd.naber.vo.OrderDetail;

public class SellerOrdersAdapter extends RecyclerView.Adapter<SellerOrdersAdapter.ViewHolder> {
    private static final String TAG = SellerOrdersAdapter.class.getSimpleName();
    private View.OnClickListener cancelListener,failureListener, statusChangeClickListener;

    public SellerOrdersAdapter(View.OnClickListener cancelListener, View.OnClickListener  failureListener, View.OnClickListener statusChangeClickListener) {
        this.cancelListener = cancelListener;
        this.failureListener = failureListener;
        this.statusChangeClickListener = statusChangeClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_orders_items, parent, false);
        SellerOrdersAdapter.ViewHolder vh = new SellerOrdersAdapter.ViewHolder(v);
        vh.setOnClickListeners(cancelListener, failureListener, statusChangeClickListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.processingBtn.setVisibility(View.GONE);
        holder.canFetchBtn.setVisibility(View.GONE);
        holder.finishBtn.setVisibility(View.GONE);
        holder.failureBtn.setVisibility(View.GONE);
        holder.finishBtn.setVisibility(View.GONE);

        // set Tag
        holder.setTag(position);

        OrderStatus status = OrderStatus.of(Model.SELLER_TMP_ORDERS_LIST.get(position).status);
        if(Model.SELLER_TMP_ORDERS_LIST.get(position).order_detail.order_type.delivery.equals(Delivery.IN)){
            holder.mealText.setText("內用");
        } else if(Model.SELLER_TMP_ORDERS_LIST.get(position).order_detail.order_type.delivery.equals(Delivery.OUT)){
            holder.mealText.setText("外帶");
        }
        holder.fetchTimeText.setText(Tools.FORMAT.format(NaberConstant.DATE_FORMAT_PATTERN, "dd日 HH時 mm分", Model.SELLER_TMP_ORDERS_LIST.get(position).fetch_date));
        holder.remarkText.setText(Model.SELLER_TMP_ORDERS_LIST.get(position).user_message);

        holder.foodItemsCountText.setText("  (" + Model.SELLER_TMP_ORDERS_LIST.get(position).order_detail.orders.size() + ")");

        holder.userPhoneNumberText.setText(Model.SELLER_TMP_ORDERS_LIST.get(position).order_detail.user_phone);
        holder.userNameText.setText(Model.SELLER_TMP_ORDERS_LIST.get(position).order_detail.user_name);
        int use_bonus = IntegerTools.parseInt(Model.SELLER_TMP_ORDERS_LIST.get(position).use_bonus,0);
        if(use_bonus > 0 ){
            int price = IntegerTools.parseInt(Model.SELLER_TMP_ORDERS_LIST.get(position).order_price,0);
            holder.totalAmountText.setText("$ " + (price - (use_bonus/10*3)) + ", 使用紅利: " + use_bonus);
        } else {
            holder.totalAmountText.setText("$ " + Model.SELLER_TMP_ORDERS_LIST.get(position).order_price);
        }
        String content = "";
        for (OrderDetail.OrderData data : Model.SELLER_TMP_ORDERS_LIST.get(position).order_detail.orders) {
            content += data.item.category_name +": " +
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
        holder.foodItemsText.setText(content);

        switch (status) {
            case UNFINISH:
                holder.processingBtn.setVisibility(View.VISIBLE);
                holder.canFetchBtn.setVisibility(View.VISIBLE);
                break;
            case CAN_FETCH:
                holder.failureBtn.setVisibility(View.VISIBLE);
                holder.finishBtn.setVisibility(View.VISIBLE);
                break;
            case PROCESSING:
                holder.canFetchBtn.setVisibility(View.VISIBLE);
                holder.finishBtn.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return Model.SELLER_TMP_ORDERS_LIST.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView foodItemsCountText, foodItemsText, remarkText, fetchTimeText, userPhoneNumberText, userNameText, totalAmountText,mealText;
        private Button cancelBtn, failureBtn, processingBtn, canFetchBtn, finishBtn;

        ViewHolder(View v) {
            super(v);
            foodItemsCountText = v.findViewById(R.id.foodCountText);
            foodItemsText = v.findViewById(R.id.foodContentText);
            remarkText = v.findViewById(R.id.userMessageText);

            cancelBtn = v.findViewById(R.id.cancelBtn);
            processingBtn = v.findViewById(R.id.processingBtn);
            failureBtn = v.findViewById(R.id.failureBtn);
            canFetchBtn = v.findViewById(R.id.canFetchBtn);
            finishBtn = v.findViewById(R.id.finishBtn);

            mealText = v.findViewById(R.id.meal_Text);
            fetchTimeText = v.findViewById(R.id.fetchDateText);
            userPhoneNumberText = v.findViewById(R.id.userPhoneText);
            userNameText = v.findViewById(R.id.userNameText);
            totalAmountText = v.findViewById(R.id.priceEdit);
        }

        private void setOnClickListeners(View.OnClickListener cancelListener, View.OnClickListener  failureListener, View.OnClickListener statusChangeClickListener) {
            cancelBtn.setOnClickListener(cancelListener);
            processingBtn.setOnClickListener(statusChangeClickListener);
            failureBtn.setOnClickListener(failureListener);
            canFetchBtn.setOnClickListener(statusChangeClickListener);
            finishBtn.setOnClickListener(statusChangeClickListener);
        }

        private void setTag(int position) {
            this.cancelBtn.setTag(position);
            this.processingBtn.setTag(position);
            this.failureBtn.setTag(position);
            this.canFetchBtn.setTag(position);
            this.finishBtn.setTag(position);
        }
    }
}