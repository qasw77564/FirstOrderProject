package com.melonltd.naber.view.user.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.melonltd.naber.R;
import com.melonltd.naber.view.user.page.UserBonusExchangeFragment;
import com.melonltd.naber.vo.ActivitiesVo;

import java.util.List;

public class UserBonusExchangeAdapter extends RecyclerView.Adapter<UserBonusExchangeAdapter.ViewHolder> {
//    private static final String TAG = UserBonusExchangeAdapter.class.getSimpleName();
//    private List<UserBonusExchangeFragment.BonusExchange> list;
    private List<ActivitiesVo> list;
    private View.OnClickListener listener;

    public UserBonusExchangeAdapter(List<ActivitiesVo> list){
        this.list = list;
    }

    public void setOnItemClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_bonus_exchange_item, parent, false);
        UserBonusExchangeAdapter.ViewHolder vh = new UserBonusExchangeAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {

        h.itemView.setTag(position);
        h.itemView.setOnClickListener(this.listener);


        ActivitiesVo data = list.get(position);
        h.title.setText(data.need_bonus);
        h.content.setText(data.title);
        h.type.setText(data.content_text);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, content, type;
        public ViewHolder(View v) {
            super(v);
            this.title = v.findViewById(R.id.titleText);
            this.content = v.findViewById(R.id.contentText);
            this.type = v.findViewById(R.id.typeText);
        }
    }
}
