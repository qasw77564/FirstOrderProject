package com.melonltd.naber.view.seller.adapter;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.common.base.Strings;
import com.melonltd.naber.R;
import com.melonltd.naber.model.bean.Model;
import com.melonltd.naber.util.IntegerTools;
import com.melonltd.naber.view.customize.SwitchButton;
import com.melonltd.naber.vo.FoodVo;


public class SellerFoodAdapter extends RecyclerView.Adapter<SellerFoodAdapter.ViewHolder> {
//    private static final String TAG = SellerFoodAdapter.class.getSimpleName();
    private SwitchButton.OnCheckedChangeListener switchListener;
    private View.OnClickListener deleteListener, editListener;
    private boolean IS_SORT_EDIT = false;

    public SellerFoodAdapter(SwitchButton.OnCheckedChangeListener switchListener, View.OnClickListener deleteListener, View.OnClickListener editListener) {
        this.switchListener = switchListener;
        this.deleteListener = deleteListener;
        this.editListener = editListener;
    }

    public SellerFoodAdapter setSortEdit(boolean isSortEdit) {
        this.IS_SORT_EDIT = isSortEdit;
        return this;
    }

    @NonNull
    @Override
    public SellerFoodAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_food_item, parent, false);
        SellerFoodAdapter.ViewHolder vh = new SellerFoodAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull SellerFoodAdapter.ViewHolder holder,final int position) {
        FoodVo vo = Model.SELLER_FOOD_LIST.get(position);

        holder.setTag(position);
        if (!Strings.isNullOrEmpty(Model.SELLER_FOOD_LIST.get(position).photo)){
            holder.itemIconImageView.setImageURI(Uri.parse(Model.SELLER_FOOD_LIST.get(position).photo));
        }else {
            ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithResourceId(R.drawable.naber_icon_logo_reverse).build();
            holder.itemIconImageView.setImageURI(imageRequest.getSourceUri());
        }

        holder.nameText.setText(Model.SELLER_FOOD_LIST.get(position).food_name);
        holder.priceText.setText(Model.SELLER_FOOD_LIST.get(position).default_price);
        holder.menuSwitch.setChecked(Model.SELLER_FOOD_LIST.get(position).status.getStatus());

        holder.menuSwitch.setOnCheckedChangeListener(this.switchListener);
        holder.deleteBtn.setOnClickListener(this.deleteListener);
        holder.editBtn.setOnClickListener(this.editListener);

        holder.topEdit.setText(vo.top);
        holder.topEdit.setEnabled(this.IS_SORT_EDIT);
        holder.topEdit.addTextChangedListener(new SellerFoodAdapter.SortEditListener(vo));
    }

    @Override
    public int getItemCount() {
        return Model.SELLER_FOOD_LIST.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView itemIconImageView;
        private TextView nameText, priceText;
        private Button deleteBtn, editBtn;
        private SwitchButton menuSwitch;
        private EditText topEdit;
        private View v;

        ViewHolder(View v) {
            super(v);
            this.v = v;
            this.topEdit = v.findViewById(R.id.top_edit);
            this.topEdit.setVisibility(View.VISIBLE);
            this.itemIconImageView = v.findViewById(R.id.ordersItemIconImageView);
            this.nameText = v.findViewById(R.id.ordersItemNameText);
            this.priceText = v.findViewById(R.id.itemPriceText);
            this.deleteBtn = v.findViewById(R.id.deleteBtn);
            this.deleteBtn.setVisibility(View.VISIBLE);
            this.editBtn = v.findViewById(R.id.editBtn);
            this.editBtn.setVisibility(View.VISIBLE);
            this.menuSwitch = v.findViewById(R.id.menuSwitch);
            this.menuSwitch.setVisibility(View.VISIBLE);
            View lineView = v.findViewById(R.id.lineView);
            lineView.setVisibility(View.VISIBLE);
        }

        public void setTag(int position){
            this.nameText.setTag(position);
            this.menuSwitch.setTag(position);
            this.deleteBtn.setTag(position);
            this.editBtn.setTag(position);
            this.itemIconImageView.setTag(position);
        }
    }
    public static int parseInt(String intStr, int dflt) {
        if (intStr == null)
            return dflt;

        try {
            return Integer.parseInt(intStr);
        } catch (NumberFormatException e) {
            return dflt;
        }
    }
    class SortEditListener implements TextWatcher{
        FoodVo vo;

        public SortEditListener(FoodVo vo) {
            this.vo = vo;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (Strings.isNullOrEmpty(s.toString())) {
                vo.top = "0";
            } else {
                vo.top = IntegerTools.parseInt(s.toString(), 0) + "";
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}

