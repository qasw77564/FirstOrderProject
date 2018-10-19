package com.melonltd.naber.view.user.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.melonltd.naber.R;
import com.melonltd.naber.vo.CategoryRelVo;

import java.util.List;


public class UserCategoryAdapter extends RecyclerView.Adapter<UserCategoryAdapter.ViewHolder> {
//    private static final String TAG = UserCategoryAdapter.class.getSimpleName();
    private List<CategoryRelVo> listData;
    private View.OnClickListener itemClickListener;


    public UserCategoryAdapter(List<CategoryRelVo> listData) {
        this.listData = listData;
    }

    public void setItemClickListener(View.OnClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public UserCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_category_item, parent, false);
        UserCategoryAdapter.ViewHolder vh = new UserCategoryAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull UserCategoryAdapter.ViewHolder holder, int position) {

        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this.itemClickListener);
//        holder.categoryText.setTag(position);
        holder.categoryText.setText(listData.get(position).category_name);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView categoryText;

        public ViewHolder(View v) {
            super(v);
            categoryText = v.findViewById(R.id.categoryText);
        }
    }
}



