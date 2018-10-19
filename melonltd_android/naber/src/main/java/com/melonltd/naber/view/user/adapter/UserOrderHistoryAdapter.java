package com.melonltd.naber.view.user.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.melonltd.naber.R;
import com.melonltd.naber.model.constant.NaberConstant;
import com.melonltd.naber.model.type.OrderStatus;
import com.melonltd.naber.util.IntegerTools;
import com.melonltd.naber.util.Tools;
import com.melonltd.naber.vo.OrderDetail;
import com.melonltd.naber.vo.OrderVo;

import java.util.List;


public class UserOrderHistoryAdapter extends RecyclerView.Adapter<UserOrderHistoryAdapter.ViewHolder> {
    private static final String TAG = UserOrderHistoryAdapter.class.getSimpleName();
    private View.OnClickListener itemClickListener;
    private Context context;
    private List<OrderVo> orderHistoryList = Lists.newArrayList();

    public UserOrderHistoryAdapter(List<OrderVo> orderHistoryList){
        this.orderHistoryList = orderHistoryList;
    }

    public void setListener(View.OnClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public UserOrderHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_history_item, parent, false);
        UserOrderHistoryAdapter.ViewHolder vh = new UserOrderHistoryAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull UserOrderHistoryAdapter.ViewHolder holder, int position) {

        OrderVo orderVo = orderHistoryList.get(position);

        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this.itemClickListener);
        OrderDetail detail = Tools.JSONPARSE.fromJson(orderVo.order_data, OrderDetail.class);
        holder.getOrderTimeText.setText(Tools.FORMAT.format(NaberConstant.DATE_FORMAT_PATTERN, "dd日 HH時 mm分", orderVo.fetch_date));

        holder.restaurantNameText.setText(detail.restaurant_name);
        OrderStatus status = OrderStatus.of(orderVo.status);
        if (status != null){
            holder.orderStatusText.setTextColor(this.context.getResources().getColor(status.getColor()));
            if(status.equals(OrderStatus.UNFINISH)){
                holder.orderStatusText.setText("");
            } else {
                holder.orderStatusText.setText(status.getText());
            }
        }

        int use_bonus = IntegerTools.parseInt(orderVo.use_bonus, 0);
        if( use_bonus > 0){
            int price = IntegerTools.parseInt(orderVo.order_price,0);
            holder.totalAmountText.setText("$" + (price - (use_bonus / 10 * 3) ));
        } else {
            holder.totalAmountText.setText("$" + (orderVo.order_price));
        }
    }


    @Override
    public int getItemCount() {
        return orderHistoryList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView restaurantNameText, totalAmountText, orderStatusText, getOrderTimeText;

        public ViewHolder(View v) {
            super(v);
            restaurantNameText = v.findViewById(R.id.restaurantNameText);
            totalAmountText = v.findViewById(R.id.priceEdit);
            orderStatusText = v.findViewById(R.id.orderStatusText);
            getOrderTimeText = v.findViewById(R.id.getOrderTimeText);
        }
    }
}


