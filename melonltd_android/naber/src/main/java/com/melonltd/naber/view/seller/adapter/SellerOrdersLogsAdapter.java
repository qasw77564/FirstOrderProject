package com.melonltd.naber.view.seller.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.melonltd.naber.R;
import com.melonltd.naber.model.bean.Model;
import com.melonltd.naber.model.type.OrderStatus;
import com.melonltd.naber.util.IntegerTools;

public class SellerOrdersLogsAdapter extends RecyclerView.Adapter<SellerOrdersLogsAdapter.ViewHolder> {
    private Context context;
    private View.OnClickListener itemListener;

    public SellerOrdersLogsAdapter(View.OnClickListener itemOnClickListener) {
        this.itemListener = itemOnClickListener;
    }

    @NonNull
    @Override
    public SellerOrdersLogsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_orders_logs_items, parent, false);
        SellerOrdersLogsAdapter.ViewHolder vh = new SellerOrdersLogsAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull SellerOrdersLogsAdapter.ViewHolder holder, int position) {
        int use_bonus = IntegerTools.parseInt(Model.SELLER_STAT_LOGS.get(position).use_bonus,0);
        if(use_bonus > 0){
            int price = IntegerTools.parseInt(Model.SELLER_STAT_LOGS.get(position).order_price,0);
            holder.amountText.setText("$ " + (price - (use_bonus/10*3)));
        } else {
            holder.amountText.setText("$ " + Model.SELLER_STAT_LOGS.get(position).order_price);
        }

        String content = "電話  ";
        content += Strings.padEnd(Model.SELLER_STAT_LOGS.get(position).order_detail.user_phone, 25, '\u0020');
        content += "姓名  ";
        content += Strings.padEnd(Model.SELLER_STAT_LOGS.get(position).order_detail.user_name, 10, '\u0020');
        holder.logsItemText.setText(content);

        OrderStatus status = OrderStatus.of(Model.SELLER_STAT_LOGS.get(position).status);
        holder.statusBtn.setText(status.getText());
        holder.statusBtn.setBackgroundColor(this.context.getResources().getColor(status.getColor()));

        holder.v.setTag(position);
        holder.v.setOnClickListener(this.itemListener);
    }

    @Override
    public int getItemCount() {
        return Model.SELLER_STAT_LOGS.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private Button statusBtn;
        private TextView amountText, logsItemText;
        private View v;

        ViewHolder(View v) {
            super(v);
            this.v = v;
            statusBtn = v.findViewById(R.id.statusBtn);
            amountText = v.findViewById(R.id.amountText);
            logsItemText = v.findViewById(R.id.logsItemText);
        }
    }
}

