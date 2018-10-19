package com.melonltd.naber.view.seller.adapter;

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

import com.google.common.base.Strings;
import com.melonltd.naber.R;
import com.melonltd.naber.model.bean.Model;
import com.melonltd.naber.util.IntegerTools;
import com.melonltd.naber.view.common.HideKeyboardListener;
import com.melonltd.naber.view.customize.SwitchButton;
import com.melonltd.naber.vo.CategoryRelVo;

public class SellerCategoryAdapter extends RecyclerView.Adapter<SellerCategoryAdapter.ViewHolder> {
//    private static final String TAG = SellerCategoryAdapter.class.getSimpleName();
    private SwitchButton.OnCheckedChangeListener aSwitchListener;
    private View.OnClickListener deleteListener, editListener;
    private View.OnClickListener hideKeyboardListener = new HideKeyboardListener();
    private boolean IS_SORT_EDIT = false;

    public SellerCategoryAdapter( SwitchButton.OnCheckedChangeListener aSwitchListener, View.OnClickListener editListener, View.OnClickListener deleteListener) {

        this.aSwitchListener = aSwitchListener;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    public SellerCategoryAdapter setSortEdit(boolean isSortEdit) {
        this.IS_SORT_EDIT = isSortEdit;
        return this;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_category_edit_items, parent, false);
        SellerCategoryAdapter.ViewHolder vh = new SellerCategoryAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull  ViewHolder holder, int position) {
        CategoryRelVo vo = Model.SELLER_CATEGORY_LIST.get(position);

        holder.v.setOnClickListener(this.hideKeyboardListener);
        holder.topEdit.setEnabled(this.IS_SORT_EDIT);

        holder.categoryText.setText(vo.category_name);
        holder.setTag(position);

        holder.aSwitch.setChecked(vo.status.getStatus());
        holder.aSwitch.setOnCheckedChangeListener(this.aSwitchListener);
        holder.editBtn.setOnClickListener(this.editListener);
        holder.deleteBtn.setOnClickListener(this.deleteListener);
        holder.topEdit.setText(vo.top);

        holder.topEdit.addTextChangedListener(new SortEditListener(vo));

    }

    @Override
    public int getItemCount() {
        return Model.SELLER_CATEGORY_LIST.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView categoryText, topText;
        private EditText topEdit;
        private Button editBtn, deleteBtn;
        private SwitchButton aSwitch;
        private View v;

        ViewHolder(View v) {
            super(v);
            this.v = v;
            this.topText = v.findViewById(R.id.topText);
            this.topText.setVisibility(View.GONE);
            this.categoryText = v.findViewById(R.id.categoryText);
            this.topEdit = v.findViewById(R.id.top_edit);
            this.editBtn = v.findViewById(R.id.editBtn);
            this.deleteBtn = v.findViewById(R.id.deleteBtn);
            this.aSwitch = v.findViewById(R.id.aSwitch);
        }

        public void setTag(int position) {
            this.aSwitch.setTag(position);
            this.editBtn.setTag(position);
            this.deleteBtn.setTag(position);
        }
    }

    class SortEditListener implements TextWatcher {
        CategoryRelVo vo;

        SortEditListener( CategoryRelVo vo ){
            this.vo = vo;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence s, int i, int i1, int i2) {
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
