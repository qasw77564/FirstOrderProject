package com.melonltd.naber.view.user.adapter;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.common.base.Strings;
import com.melonltd.naber.vo.FoodVo;
import com.melonltd.naber.R;

import java.util.List;


public class UserFoodAdapter extends RecyclerView.Adapter<UserFoodAdapter.ViewHolder> {
    private static final String TAG = UserFoodAdapter.class.getSimpleName();
    private List<FoodVo> listData;
    private View.OnClickListener itemClickListener;

    public UserFoodAdapter(List<FoodVo> listData) {
        this.listData = listData;
    }

    public void setItemClickListener(View.OnClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public UserFoodAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_food_item, parent, false);
        UserFoodAdapter.ViewHolder vh = new UserFoodAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull UserFoodAdapter.ViewHolder h, int position) {

        h.itemView.setTag(position);
        h.itemView.setOnClickListener(this.itemClickListener);

        if (!Strings.isNullOrEmpty(listData.get(position).photo)){
            h.itemIconImageView.setImageURI(Uri.parse(listData.get(position).photo));
        }else {
            ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithResourceId(R.drawable.naber_icon_logo_reverse).build();
            h.itemIconImageView.setImageURI(imageRequest.getSourceUri());
        }
        h.itemNameText.setText(listData.get(position).food_name);
        h.itemPriceText.setText(listData.get(position).default_price);

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView itemIconImageView;
        private TextView itemNameText, itemPriceText;

        public ViewHolder(View v) {
            super(v);
            itemIconImageView = v.findViewById(R.id.ordersItemIconImageView);
            itemNameText = v.findViewById(R.id.ordersItemNameText);
            itemPriceText = v.findViewById(R.id.itemPriceText);
        }
    }
}

