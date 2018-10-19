package com.melonltd.naber.view.user.page;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.melonltd.naber.R;
import com.melonltd.naber.model.constant.NaberConstant;
import com.melonltd.naber.model.service.SPService;
import com.melonltd.naber.view.factory.PageType;
import com.melonltd.naber.view.user.UserMainActivity;
import com.melonltd.naber.vo.OrderDetail;

import java.util.Iterator;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

public class UserShoppingCartFragment extends Fragment {
//    private static final String TAG = UserShoppingCartFragment.class.getSimpleName();
    public static UserShoppingCartFragment FRAGMENT = null;
    private ShoppingCartAdapter adapter;
    public static int TO_SUBMIT_ORDERS_PAGE_INDEX = -1;
    private List<OrderDetail> cacheShoppingCar = Lists.<OrderDetail>newArrayList();

    public UserShoppingCartFragment() {
    }

    public Fragment getInstance(Bundle bundle) {
        if (FRAGMENT == null) {
            FRAGMENT = new UserShoppingCartFragment();
            TO_SUBMIT_ORDERS_PAGE_INDEX = -1;
        }
        if (bundle != null) {
            FRAGMENT.setArguments(bundle);
        }
        return FRAGMENT;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(getContext());
        adapter = new ShoppingCartAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container.getTag(R.id.user_shopping_cart_page) == null) {
            View v = inflater.inflate(R.layout.fragment_user_shopping_cart, container, false);
            getViews(v);
            container.setTag(R.id.user_shopping_cart_page, v);
            return v;
        }
        return (View) container.getTag(R.id.user_shopping_cart_page);
    }

    private void getViews(View v) {
        final BGARefreshLayout bgaRefreshLayout = v.findViewById(R.id.shoppingCartBGARefreshLayout);
        RecyclerView recyclerView = v.findViewById(R.id.shoppingCartRecyclerView);

        BGANormalRefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(getContext(), true);
        refreshViewHolder.setPullDownRefreshText("Pull");
        refreshViewHolder.setRefreshingText("Pull to refresh");
        refreshViewHolder.setReleaseRefreshText("Pull to refresh");
        refreshViewHolder.setLoadingMoreText("Loading more !");

        bgaRefreshLayout.setRefreshViewHolder(refreshViewHolder);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        // setListener
        adapter.setListener(new CancelListener(), new SubmitListener());
        recyclerView.setAdapter(adapter);
        bgaRefreshLayout.setDelegate(new BGARefreshLayout.BGARefreshLayoutDelegate() {
            @Override
            public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
                bgaRefreshLayout.endRefreshing();
//                Model.USER_CACHE_SHOPPING_CART.clear();
//                Model.USER_CACHE_SHOPPING_CART.addAll(SPService.getUserCacheShoppingCarData());
                cacheShoppingCar.clear();
                cacheShoppingCar.addAll(SPService.getUserCacheShoppingCarData());
                adapter.notifyDataSetChanged();
            }

            @Override
            public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
                bgaRefreshLayout.endLoadingMore();
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // 清除 model 已 偏好儲存資料為主，並刷新 model
//        Model.USER_CACHE_SHOPPING_CART.clear();
//        Model.USER_CACHE_SHOPPING_CART.addAll(SPService.getUserCacheShoppingCarData());
        this.cacheShoppingCar.clear();
        this.cacheShoppingCar.addAll(SPService.getUserCacheShoppingCarData());
        UserMainActivity.changeTabAndToolbarStatus();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class CancelListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final int index = (int) v.getTag();
            new AlertView.Builder()
                    .setContext(getContext())
                    .setStyle(AlertView.Style.Alert)
                    .setTitle("")
                    .setMessage("確定是否刪除此訂單!!")
                    .setOthers(new String[]{"刪除", "返回"})
                    .setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int position) {
                            if (position == 0) {

//                                Model.USER_CACHE_SHOPPING_CART.remove(index);
//                                SPService.setUserCacheShoppingCarData(Model.USER_CACHE_SHOPPING_CART);

                                cacheShoppingCar.remove(index);
                                SPService.setUserCacheShoppingCarData(cacheShoppingCar);

                                adapter.notifyItemRemoved(index);
                            }
                        }
                    })
                    .build()
                    .setCancelable(true)
                    .show();
        }
    }

    class SubmitListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int index = (int) v.getTag();
            int amount = 0;
//            for (int i = 0; i < Model.USER_CACHE_SHOPPING_CART.get(index).orders.size(); i++) {
//                amount += Integer.parseInt(Model.USER_CACHE_SHOPPING_CART.get(index).orders.get(i).item.price);
//            }

            for (int i = 0; i < cacheShoppingCar.get(index).orders.size(); i++) {
                amount += Integer.parseInt(cacheShoppingCar.get(index).orders.get(i).item.price);
            }

            if (amount <= NaberConstant.ORDER_MAX_AMOUNT) {
                TO_SUBMIT_ORDERS_PAGE_INDEX = index;
                Bundle bundle = new Bundle();
                bundle.putInt(NaberConstant.ORDER_DETAIL_INDEX, index);
                // 帶 index 到 提交訂單頁面
                UserMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.USER_SUBMIT_ORDER, bundle);
            } else {
                new AlertView.Builder()
                        .setContext(getContext())
                        .setStyle(AlertView.Style.Alert)
                        .setTitle("")
                        .setMessage("單筆訂單金額不可超過 5000，\n請重新編輯您的訂單內容!!")
                        .setOthers(new String[]{"返回"})
                        .build()
                        .setCancelable(true)
                        .show();
            }
        }
    }

    class FoodAddAndMinusListener implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            Iterator<String> iterator = Splitter.on("$split").split(v.getTag().toString()).iterator();
            int rootIndex = Integer.parseInt(iterator.next());
            int subIndex = Integer.parseInt(iterator.next());

            OrderDetail orderDetail = cacheShoppingCar.get(rootIndex);

            int price = Integer.parseInt(orderDetail.orders.get(subIndex).item.price) / orderDetail.orders.get(subIndex).count;

            if (v.getId() == R.id.ordersItemAddBtn) {
                orderDetail.orders.get(subIndex).count++;
                if (orderDetail.orders.get(subIndex).count > 50) {
                    orderDetail.orders.get(subIndex).count = 50;
                }
            } else if (v.getId() == R.id.ordersItemMinusBtn) {
                orderDetail.orders.get(subIndex).count--;
                if (orderDetail.orders.get(subIndex).count <= 0) {
                    orderDetail.orders.get(subIndex).count = 1;
                }
            }
            orderDetail.orders.get(subIndex).item.price = (price * orderDetail.orders.get(subIndex).count) + "";
            SPService.setUserCacheShoppingCarData(cacheShoppingCar);






//            int price = Integer.parseInt(Model.USER_CACHE_SHOPPING_CART.get(rootIndex).orders.get(subIndex).item.price) / Model.USER_CACHE_SHOPPING_CART.get(rootIndex).orders.get(subIndex).count;
//
//            if (v.getId() == R.id.ordersItemAddBtn) {
//                Model.USER_CACHE_SHOPPING_CART.get(rootIndex).orders.get(subIndex).count++;
//                if (Model.USER_CACHE_SHOPPING_CART.get(rootIndex).orders.get(subIndex).count > 50) {
//                    Model.USER_CACHE_SHOPPING_CART.get(rootIndex).orders.get(subIndex).count = 50;
//                }
//            } else if (v.getId() == R.id.ordersItemMinusBtn) {
//                Model.USER_CACHE_SHOPPING_CART.get(rootIndex).orders.get(subIndex).count--;
//                if (Model.USER_CACHE_SHOPPING_CART.get(rootIndex).orders.get(subIndex).count <= 0) {
//                    Model.USER_CACHE_SHOPPING_CART.get(rootIndex).orders.get(subIndex).count = 1;
//                }
//            }
//            Model.USER_CACHE_SHOPPING_CART.get(rootIndex).orders.get(subIndex).item.price = (price * Model.USER_CACHE_SHOPPING_CART.get(rootIndex).orders.get(subIndex).count) + "";
//            SPService.setUserCacheShoppingCarData(Model.USER_CACHE_SHOPPING_CART);
            adapter.notifyItemChanged(rootIndex);
        }
    }

    class FoodDeleteListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Iterator<String> iterator = Splitter.on("$split").split(v.getTag().toString()).iterator();
            final int rootIndex = Integer.parseInt(iterator.next());
            int subIndex = Integer.parseInt(iterator.next());

            OrderDetail orderDetail = cacheShoppingCar.get(rootIndex);

            if (orderDetail.orders.size() > 1) {
                orderDetail.orders.remove(subIndex);
                SPService.setUserCacheShoppingCarData(cacheShoppingCar);
                adapter.notifyDataSetChanged();
            } else {
                new AlertView.Builder()
                        .setContext(getContext())
                        .setStyle(AlertView.Style.Alert)
                        .setTitle("")
                        .setMessage("剩下最後一筆菜單，\n如果刪除該筆訂單也會連同刪除，\n確定是否刪除!!")
                        .setOthers(new String[]{"刪除", "返回"})
                        .setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(Object o, int position) {
                                if (position == 0) {
                                    cacheShoppingCar.remove(rootIndex);
                                    SPService.setUserCacheShoppingCarData(cacheShoppingCar);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        })
                        .build()
                        .setCancelable(true)
                        .show();
            }

//            if (Model.USER_CACHE_SHOPPING_CART.get(rootIndex).orders.size() > 1) {
//                Model.USER_CACHE_SHOPPING_CART.get(rootIndex).orders.remove(subIndex);
//                SPService.setUserCacheShoppingCarData(Model.USER_CACHE_SHOPPING_CART);
//                adapter.notifyDataSetChanged();
//            } else {
//                new AlertView.Builder()
//                        .setContext(getContext())
//                        .setStyle(AlertView.Style.Alert)
//                        .setTitle("")
//                        .setMessage("剩下最後一筆菜單，\n如果刪除該筆訂單也會連同刪除，\n確定是否刪除!!")
//                        .setOthers(new String[]{"刪除", "返回"})
//                        .setOnItemClickListener(new OnItemClickListener() {
//                            @Override
//                            public void onItemClick(Object o, int position) {
//                                if (position == 0) {
//                                    Model.USER_CACHE_SHOPPING_CART.remove(rootIndex);
//                                    SPService.setUserCacheShoppingCarData(Model.USER_CACHE_SHOPPING_CART);
//                                    adapter.notifyDataSetChanged();
//                                }
//                            }
//                        })
//                        .build()
//                        .setCancelable(true)
//                        .show();
//            }
        }
    }

    class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartAdapter.ViewHolder> {
        private Context context;
        private View.OnClickListener cancelListener, submitListener;

        private void setListener(View.OnClickListener cancelListener, View.OnClickListener submitListener) {
            this.cancelListener = cancelListener;
            this.submitListener = submitListener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            this.context = parent.getContext();
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_shopping_detail_item, parent, false);
            ShoppingCartAdapter.ViewHolder vh = new ShoppingCartAdapter.ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            OrderDetail orderDetail = cacheShoppingCar.get(position);
            int amount = 0;
            for (int i = 0; i < orderDetail.orders.size(); i++) {
                amount += Integer.parseInt(orderDetail.orders.get(i).item.price);
            }

            holder.setSubViews(position);
            holder.totalAmountText.setText(amount + "");
            //  未更新APP會生錯誤
            if (Strings.isNullOrEmpty(orderDetail.can_discount)){
                orderDetail.can_discount = "Y";
            }

            if (orderDetail.can_discount.equals("N")){
                holder.bonusText.setText("該店家不提供紅利");
            }else {
                holder.bonusText.setText(((int) Math.floor(amount / 10d)) + "");
            }
            holder.nameText.setText(orderDetail.restaurant_name);
            holder.cancelBtn.setTag(position);
            holder.submitBtn.setTag(position);
            holder.cancelBtn.setOnClickListener(this.cancelListener);
            holder.submitBtn.setOnClickListener(this.submitListener);





//            int amount = 0;
//            for (int i = 0; i < Model.USER_CACHE_SHOPPING_CART.get(position).orders.size(); i++) {
//                amount += Integer.parseInt(Model.USER_CACHE_SHOPPING_CART.get(position).orders.get(i).item.price);
//            }
//
//            holder.setSubViews(position);
//            holder.totalAmountText.setText(amount + "");
//            //  未更新APP會生錯誤
//            if (Strings.isNullOrEmpty(Model.USER_CACHE_SHOPPING_CART.get(position).can_discount)){
//                Model.USER_CACHE_SHOPPING_CART.get(position).can_discount = "Y";
//            }
//
//            if (Model.USER_CACHE_SHOPPING_CART.get(position).can_discount.equals("N")){
//                holder.bonusText.setText("該店家不提供紅利");
//            }else {
//                holder.bonusText.setText(((int) Math.floor(amount / 10d)) + "");
//            }
//            holder.nameText.setText(Model.USER_CACHE_SHOPPING_CART.get(position).restaurant_name);
//            holder.cancelBtn.setTag(position);
//            holder.submitBtn.setTag(position);
//            holder.cancelBtn.setOnClickListener(this.cancelListener);
//            holder.submitBtn.setOnClickListener(this.submitListener);
        }

        @Override
        public int getItemCount() {
            return cacheShoppingCar.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView nameText, totalAmountText, bonusText;
            private LinearLayout layout;
            private Button cancelBtn, submitBtn;
            private View.OnClickListener foodAddAndMinusListener;
            private View.OnClickListener foodDeleteListener;

            ViewHolder(View v) {
                super(v);
                this.nameText = v.findViewById(R.id.restaurantNameText);
                this.totalAmountText = v.findViewById(R.id.ordersTotalAmountText);
                this.bonusText = v.findViewById(R.id.bonusText);
                this.layout = v.findViewById(R.id.ordersItemsLayout);
                this.cancelBtn = v.findViewById(R.id.deleteOrdersBtn);
                this.submitBtn = v.findViewById(R.id.submitOrdersBtn);
                this.foodAddAndMinusListener = new FoodAddAndMinusListener();
                this.foodDeleteListener = new FoodDeleteListener();
            }

            public void setSubViews(final int position) {
                this.layout.removeAllViews();

                OrderDetail orderDetail = cacheShoppingCar.get(position);

                for (int i = 0; i < orderDetail.orders.size(); i++) {
                    FoodItem foodItem = new FoodItem(context, position + "$split" + i);

                    String msg = "規格：";
                    msg += orderDetail.orders.get(i).item.scopes.get(0).name + "\n";
                    for (int j = 0; j < orderDetail.orders.get(i).item.demands.size(); j++) {
                        msg += orderDetail.orders.get(i).item.demands.get(j).name + ":";
                        msg += orderDetail.orders.get(i).item.demands.get(j).datas.get(0).name + ",";
                    }
                    msg += "\n";
                    if (orderDetail.orders.get(i).item.opts.size() > 0) {
                        msg += "追加：";
                        for (int k = 0; k < orderDetail.orders.get(i).item.opts.size(); k++) {
                            msg += orderDetail.orders.get(i).item.opts.get(k).name + ",";
                        }
                    }

                    foodItem.foodNameText.setText(orderDetail.orders.get(i).item.food_name);
                    foodItem.detailText.setText(msg);
                    foodItem.priceText.setText(orderDetail.orders.get(i).item.price);
                    foodItem.countText.setText(orderDetail.orders.get(i).count + "");
                    foodItem.addBtn.setOnClickListener(this.foodAddAndMinusListener);
                    foodItem.minusBtn.setOnClickListener(this.foodAddAndMinusListener);
                    foodItem.deleteBtn.setOnClickListener(this.foodDeleteListener);
                    if (!Strings.isNullOrEmpty(orderDetail.orders.get(i).item.food_photo)){
                        foodItem.foodPhotoImageView.setImageURI(Uri.parse(orderDetail.orders.get(i).item.food_photo));
                    }else {
                        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithResourceId(R.drawable.naber_icon_logo_reverse).build();
                        foodItem.foodPhotoImageView.setImageURI(imageRequest.getSourceUri());
                    }

                    this.layout.addView(foodItem.getView());
                }


//                for (int i = 0; i < Model.USER_CACHE_SHOPPING_CART.get(position).orders.size(); i++) {
//                    FoodItem foodItem = new FoodItem(context, position + "$split" + i);
//
//                    String msg = "規格：";
//                    msg += Model.USER_CACHE_SHOPPING_CART.get(position).orders.get(i).item.scopes.get(0).name + "\n";
//                    for (int j = 0; j < Model.USER_CACHE_SHOPPING_CART.get(position).orders.get(i).item.demands.size(); j++) {
//                        msg += Model.USER_CACHE_SHOPPING_CART.get(position).orders.get(i).item.demands.get(j).name + ":";
//                        msg += Model.USER_CACHE_SHOPPING_CART.get(position).orders.get(i).item.demands.get(j).datas.get(0).name + ",";
//                    }
//                    msg += "\n";
//                    if (Model.USER_CACHE_SHOPPING_CART.get(position).orders.get(i).item.opts.size() > 0) {
//                        msg += "追加：";
//                        for (int k = 0; k < Model.USER_CACHE_SHOPPING_CART.get(position).orders.get(i).item.opts.size(); k++) {
//                            msg += Model.USER_CACHE_SHOPPING_CART.get(position).orders.get(i).item.opts.get(k).name + ",";
//                        }
//                    }
//
//                    foodItem.foodNameText.setText(Model.USER_CACHE_SHOPPING_CART.get(position).orders.get(i).item.food_name);
//                    foodItem.detailText.setText(msg);
//                    foodItem.priceText.setText(Model.USER_CACHE_SHOPPING_CART.get(position).orders.get(i).item.price);
//                    foodItem.countText.setText(Model.USER_CACHE_SHOPPING_CART.get(position).orders.get(i).count + "");
//                    foodItem.addBtn.setOnClickListener(this.foodAddAndMinusListener);
//                    foodItem.minusBtn.setOnClickListener(this.foodAddAndMinusListener);
//                    foodItem.deleteBtn.setOnClickListener(this.foodDeleteListener);
//                    if (!Strings.isNullOrEmpty(Model.USER_CACHE_SHOPPING_CART.get(position).orders.get(i).item.food_photo)){
//                        foodItem.foodPhotoImageView.setImageURI(Uri.parse(Model.USER_CACHE_SHOPPING_CART.get(position).orders.get(i).item.food_photo));
//                    }else {
//                        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithResourceId(R.drawable.naber_icon_logo_reverse).build();
//                        foodItem.foodPhotoImageView.setImageURI(imageRequest.getSourceUri());
//                    }
//
//                    this.layout.addView(foodItem.getView());
//                }
            }

            class FoodItem {
                private SimpleDraweeView foodPhotoImageView;
                private TextView foodNameText, detailText;
                private ImageButton minusBtn, addBtn, deleteBtn;
                private TextView countText, priceText;
                private View subView;

                FoodItem(Context context, Object tag) {
                    this.subView = LayoutInflater.from(context).inflate(R.layout.user_shopping_order_item, null);
                    this.foodPhotoImageView = this.subView.findViewById(R.id.ordersItemIconImageView);
                    this.foodNameText = this.subView.findViewById(R.id.ordersItemNameText);
                    this.detailText = this.subView.findViewById(R.id.ordersItemScopeText);
                    this.minusBtn = this.subView.findViewById(R.id.ordersItemMinusBtn);
                    this.addBtn = this.subView.findViewById(R.id.ordersItemAddBtn);
                    this.deleteBtn = this.subView.findViewById(R.id.menuEditDeleteBtn);
                    this.countText = this.subView.findViewById(R.id.ordersItemQuantityText);
                    this.priceText = this.subView.findViewById(R.id.ordersItemPriceText);
                    this.minusBtn.setTag(tag);
                    this.addBtn.setTag(tag);
                    this.deleteBtn.setTag(tag);
                }

                private View getView() {
                    return this.subView;
                }
            }
        }
    }

}
