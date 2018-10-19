package com.melonltd.naber.view.user.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.common.base.Strings;
import com.melonltd.naber.R;
import com.melonltd.naber.util.Tools;
import com.melonltd.naber.vo.RestaurantInfoVo;

import java.util.List;

public class UserRestaurantAdapter extends RecyclerView.Adapter<UserRestaurantAdapter.ViewHolder> {
    //    private static final String TAG = UserRestaurantAdapter.class.getSimpleName();
    private View.OnClickListener itemOnClickListener;
    private List<RestaurantInfoVo> list;
    private Context context;

    public UserRestaurantAdapter(List<RestaurantInfoVo> list, View.OnClickListener itemOnClickListener) {
        this.list = list;
        this.itemOnClickListener = itemOnClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_restaurant_item, parent, false);
        this.context = parent.getContext();
        UserRestaurantAdapter.ViewHolder vh = new UserRestaurantAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final UserRestaurantAdapter.ViewHolder h, int position) {
        h.restaurantItem.setTag(position);
        h.restaurantItem.setOnClickListener(this.itemOnClickListener);

        if (!Strings.isNullOrEmpty(list.get(position).photo)) {
            h.restaurantIcon.setImageURI(Uri.parse(list.get(position).photo));
        } else {
            ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithResourceId(R.drawable.naber_icon_logo_reverse).build();
            h.restaurantIcon.setImageURI(imageRequest.getSourceUri());
        }

        h.restaurantNameText.setText(list.get(position).name);
        h.businessTimeText.setText("接單時間: " +
                list.get(position).store_start + "~" +
                list.get(position).store_end);

        h.addressText.setText(list.get(position).address);
        String result = "";
        if (list.get(position).distance > 0) {
            result = Tools.FORMAT.decimal("0.0", list.get(position).distance);
            result = result.equals("0.0") ? "0.1" : result + "公里";
        }
        h.distanceText.setText(result);


        h.storeStatusText.setTextColor(this.context.getResources().getColor(R.color.naber_basis_red));
        if (list.get(position).not_business.size() > 0) {
            h.storeStatusText.setText("今日已結束接單");
        } else if (!Strings.isNullOrEmpty(list.get(position).is_store_now_open) &&
                list.get(position).is_store_now_open.toUpperCase().equals("FALSE")) {
            h.storeStatusText.setText("該商家尚未營業");
        } else {
            h.storeStatusText.setTextColor(this.context.getResources().getColor(R.color.naber_basis_green));
            h.storeStatusText.setText("接單中");
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView restaurantIcon;
        ConstraintLayout restaurantItem;
        TextView restaurantNameText, businessTimeText, addressText, distanceText, storeStatusText;

        ViewHolder(View v) {
            super(v);
            this.restaurantItem = v.findViewById(R.id.restaurantItem);
            this.restaurantIcon = v.findViewById(R.id.restaurantImageView);
            this.restaurantNameText = v.findViewById(R.id.restaurantNameText);
            this.businessTimeText = v.findViewById(R.id.businessTimeText);
            this.addressText = v.findViewById(R.id.addressText);
            this.distanceText = v.findViewById(R.id.distanceText);
            this.storeStatusText = v.findViewById(R.id.storeStatusText);
        }
    }
}
