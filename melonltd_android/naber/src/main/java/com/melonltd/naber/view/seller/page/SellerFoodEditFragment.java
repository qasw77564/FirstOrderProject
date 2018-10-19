package com.melonltd.naber.view.seller.page;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.common.base.Strings;
import com.melonltd.naber.R;
import com.melonltd.naber.model.api.ApiManager;
import com.melonltd.naber.model.api.ThreadCallback;
import com.melonltd.naber.model.constant.NaberConstant;
import com.melonltd.naber.util.LoadingBarTools;
import com.melonltd.naber.util.PhotoTools;
import com.melonltd.naber.util.UpLoadCallBack;
import com.melonltd.naber.view.common.BaseCore;
import com.melonltd.naber.view.factory.PageType;
import com.melonltd.naber.view.seller.SellerMainActivity;
import com.melonltd.naber.vo.FoodVo;
import com.melonltd.naber.vo.DemandsItemVo;
import com.melonltd.naber.vo.ItemVo;
import com.melonltd.naber.vo.ReqData;

import java.io.ByteArrayOutputStream;

public class SellerFoodEditFragment extends Fragment implements View.OnFocusChangeListener {
    //    private static final String TAG = SellerFoodEditFragment.class.getSimpleName();
    public static SellerFoodEditFragment FRAGMENT = null;
    private TextView foodNameText;
    private SimpleDraweeView menuIconImage;
    private LinearLayout demandLayout;
    private LinearLayout scopeLayout, optLayout;
    private FoodVo foodVo;

    private static final int PICK_FROM_GALLERY = 9908;
    private static final int PICK_FROM_CAMERA = 9901;

    public SellerFoodEditFragment() {
    }

    public Fragment getInstance(Bundle bundle) {
        if (FRAGMENT == null) {
            FRAGMENT = new SellerFoodEditFragment();
        }
        if (bundle != null) {
            FRAGMENT.setArguments(bundle);
        }
        return FRAGMENT;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_seller_food_edit, container, false);
        getViews(v);
        return v;
    }

    private void getViews(View v) {
        menuIconImage = v.findViewById(R.id.menuIconImage);
        foodNameText = v.findViewById(R.id.foodNameText);
        demandLayout = v.findViewById(R.id.demandLayout);
        scopeLayout = v.findViewById(R.id.scopeLayout);
        optLayout = v.findViewById(R.id.optsLayout);

        Button demandBtn = v.findViewById(R.id.demandBtn);
        Button saveBtn = v.findViewById(R.id.saveBtn);
        Button cancelBtn = v.findViewById(R.id.cancelBtn);

        ImageButton addScopeBtn = v.findViewById(R.id.scopeBtn);
        ImageButton addOptBtn = v.findViewById(R.id.optBtn);
        addScopeBtn.setVisibility(View.VISIBLE);
        addOptBtn.setVisibility(View.VISIBLE);

        // set listener
        SaveAndCancelListener saveAndCancelListener = new SaveAndCancelListener();
        saveBtn.setOnClickListener(saveAndCancelListener);
        cancelBtn.setOnClickListener(saveAndCancelListener);

        AddFoodContentListener contentListener = new AddFoodContentListener();
        addScopeBtn.setOnClickListener(contentListener);
        addOptBtn.setOnClickListener(contentListener);
        demandBtn.setOnClickListener(contentListener);
        menuIconImage.setOnClickListener(new UpLoadMenuIcon());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Bitmap bitmap = null;
            switch (requestCode) {
                case PICK_FROM_CAMERA:
                    bitmap = (Bitmap) data.getExtras().get("data");
                    break;
                case PICK_FROM_GALLERY:
                    Uri pickedImage = data.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), pickedImage);
                    } catch (Exception e) {
                    }
                    break;
            }

            if (bitmap != null && bitmap.getByteCount() != 0) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                bitmap = PhotoTools.sampleBitmap(bitmap, 120);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                byte[] bytes = out.toByteArray();
                final AlertDialog dialog = LoadingBarTools.newLoading(getContext());
                PhotoTools.upLoadImage(bytes, NaberConstant.STORAGE_PATH_FOOD, foodVo.food_uuid + ".jpg", new UpLoadCallBack() {
                    @Override
                    public void getUri(final Uri uri) {
                        ReqData req = new ReqData();
                        req.date = uri.toString();
                        req.uuid = foodVo.food_uuid;
                        req.type = "FOOD";
                        ApiManager.uploadPhoto(req, new ThreadCallback(getContext()) {
                            @Override
                            public void onSuccess(String responseBody) {
                                foodVo.photo = uri.toString();
                                menuIconImage.setImageURI(uri);
                                dialog.dismiss();
                            }

                            @Override
                            public void onFail(Exception error, String msg) {
                                dialog.dismiss();
                            }
                        });
                    }

                    @Override
                    public void failure(String errMsg) {
                        ImageRequest request = ImageRequestBuilder.newBuilderWithResourceId(R.drawable.naber_icon_logo).build();
                        menuIconImage.setImageURI(request.getSourceUri());
                    }
                });
            } else {
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        SellerMainActivity.changeTabAndToolbarStatus();

        foodVo = (FoodVo) getArguments().getSerializable(NaberConstant.SELLER_FOOD_INFO);
        setValue();

        if (SellerMainActivity.toolbar != null) {
            SellerMainActivity.navigationIconDisplay(true, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    new AlertView.Builder()
                            .setTitle("")
                            .setMessage("請確認您所編輯的產品是否已經儲存，\n否則離開後所編輯之數據將會清空！")
                            .setContext(getContext())
                            .setStyle(AlertView.Style.Alert)
                            .setOthers(new String[]{"確定離開", "取消"})
                            .setOnItemClickListener(new OnItemClickListener() {
                                @Override
                                public void onItemClick(Object o, int position) {
                                    if (position == 0) {
                                        SellerFoodListFragment.TO_MENU_EDIT_PAGE_INDEX = -1;
                                        SellerMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.SELLER_FOOD_LIST, null);
                                        SellerMainActivity.navigationIconDisplay(false, null);
                                    }
                                }
                            })
                            .build()
                            .setCancelable(true)
                            .show();
                }
            });
        }
    }

    private void setValue() {
        if (!Strings.isNullOrEmpty(foodVo.photo)) {
            menuIconImage.setImageURI(Uri.parse(foodVo.photo));
        } else {
            ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithResourceId(R.drawable.naber_icon_logo).build();
            menuIconImage.setImageURI(imageRequest.getSourceUri());
        }
        foodNameText.setText(foodVo.food_name);

        scopeLayout.removeAllViews();
        for (int i = 0; i < foodVo.food_data.scopes.size(); i++) {
            scopeLayout.addView(addScopeEditView(i));
        }
        optLayout.removeAllViews();
        for (int i = 0; i < foodVo.food_data.opts.size(); i++) {
            optLayout.addView(addOptEditView(i));
        }
        demandLayout.removeAllViews();
        for (int i = 0; i < foodVo.food_data.demands.size(); i++) {
            demandLayout.addView(newDemandView(i));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        SellerMainActivity.navigationIconDisplay(false, null);
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if (!b) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void intentToPick() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_FROM_GALLERY);
    }

    public void intentToCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    class UpLoadMenuIcon implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            new AlertView.Builder()
                    .setContext(getContext())
                    .setStyle(AlertView.Style.ActionSheet)
                    .setCancelText("返回")
                    .setOthers(new String[]{"相機", "相簿"})
                    .setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int position) {
                            if (position == 1) {
                                // 確認寫入權限
                                if (BaseCore.checkWritePermission(getContext())) {
                                    ActivityCompat.requestPermissions(getActivity(), BaseCore.IO_STREAM, BaseCore.IO_STREAM_CODE);
                                } else {
                                    intentToPick();
                                }
                            } else if (position == 0) {
                                // 相機權限
                                if (BaseCore.checkCameraPermission(getContext())) {
                                    ActivityCompat.requestPermissions(getActivity(), BaseCore.CAMERA_PERMISSION, BaseCore.CAMERA_CODE);
                                } else {
                                    intentToCamera();
                                }
                            }
                        }
                    })
                    .build()
                    .setCancelable(true)
                    .show();
        }
    }

    class SaveAndCancelListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.saveBtn:
                    String msg = verifyData();
                    if (!Strings.isNullOrEmpty(msg)) {
                        new AlertView.Builder()
                                .setTitle("")
                                .setMessage(msg)
                                .setContext(getContext())
                                .setStyle(AlertView.Style.Alert)
                                .setOthers(new String[]{"我知道了"})
                                .build()
                                .setCancelable(true)
                                .show();
                    } else {
                        ApiManager.sellerFoodUpdate(foodVo, new ThreadCallback(getContext()) {
                            @Override
                            public void onSuccess(String responseBody) {
                                SellerFoodListFragment.TO_MENU_EDIT_PAGE_INDEX = -1;
                                SellerMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.SELLER_FOOD_LIST, null);
                                SellerMainActivity.navigationIconDisplay(false, null);
                            }

                            @Override
                            public void onFail(Exception error, String msg) {

                            }
                        });
                    }

                    break;
                case R.id.cancelBtn:
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    new AlertView.Builder()
                            .setTitle("")
                            .setMessage("請確認您所編輯的產品是否已經儲存，\n否則離開後所編輯之數據將會清空！")
                            .setContext(getContext())
                            .setStyle(AlertView.Style.Alert)
                            .setOthers(new String[]{"確定離開", "取消"})
                            .setOnItemClickListener(new OnItemClickListener() {
                                @Override
                                public void onItemClick(Object o, int position) {
                                    if (position == 0) {
                                        SellerFoodListFragment.TO_MENU_EDIT_PAGE_INDEX = -1;
                                        SellerMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.SELLER_FOOD_LIST, null);
                                        SellerMainActivity.navigationIconDisplay(false, null);
                                    }
                                }
                            })
                            .build()
                            .setCancelable(true)
                            .show();
                    break;
            }
        }
    }

    class AddFoodContentListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.scopeBtn:
                    foodVo.food_data.scopes.add(new ItemVo());
                    scopeLayout.addView(addScopeEditView(foodVo.food_data.scopes.size() - 1));
                    break;
                case R.id.optBtn:
                    foodVo.food_data.opts.add(new ItemVo());
                    optLayout.addView(addOptEditView(foodVo.food_data.opts.size() - 1));
                    break;
                case R.id.demandBtn:
                    final DemandsItemVo demandsItemVo = new DemandsItemVo();
                    foodVo.food_data.demands.add(demandsItemVo);
//                    final View demandView = newDemandView(foodVo.food_data.demands.size() - 1);
                    demandLayout.addView(newDemandView(foodVo.food_data.demands.size() - 1));
                    break;
            }
        }
    }

    private View addScopeEditView(final int index) {

        final ItemVo itemVo = foodVo.food_data.scopes.get(index);
        final View v = LayoutInflater.from(getContext()).inflate(R.layout.seller_edit_menu_detail, null);
        TextView priceText = v.findViewById(R.id.priceText);
        priceText.setVisibility(View.VISIBLE);
        final EditText priceEdit = v.findViewById(R.id.priceEdit);
        priceEdit.setVisibility(View.VISIBLE);
        final EditText nameEdit = v.findViewById(R.id.nameEdit);

        nameEdit.setText(itemVo.name);
        priceEdit.setText(itemVo.price);

        nameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                itemVo.name = editable.toString();
                foodVo.food_data.scopes.get(index).name = editable.toString();
            }
        });

        priceEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                itemVo.price = editable.toString();
                foodVo.food_data.scopes.get(index).price = editable.toString();
            }
        });

        nameEdit.setOnFocusChangeListener(this);
        priceEdit.setOnFocusChangeListener(this);

        ImageButton deleteBtn = v.findViewById(R.id.menuEditDeleteBtn);
        deleteBtn.setTag(index);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                if (foodVo.food_data.scopes.size() > 1) {
                    foodVo.food_data.scopes.remove(itemVo);
                    scopeLayout.removeView(v);
                } else {
                    Toast.makeText(getContext(), getContext().getString(R.string.app_name) + "品項規格至少需要一筆資了以上", Toast.LENGTH_LONG).show();
                }
            }
        });
        return v;
    }

    private View addOptEditView(final int index) {
        final ItemVo itemVo = foodVo.food_data.opts.get(index);
        final View v = LayoutInflater.from(getContext()).inflate(R.layout.seller_edit_menu_detail, null);
        TextView priceText = v.findViewById(R.id.priceText);
        final EditText priceEdit = v.findViewById(R.id.priceEdit);
        final EditText nameEdit = v.findViewById(R.id.nameEdit);
        priceEdit.setVisibility(View.VISIBLE);
        priceText.setVisibility(View.VISIBLE);

        nameEdit.setText(itemVo.name);
        priceEdit.setText(itemVo.price);

        nameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                itemVo.name = editable.toString();
                foodVo.food_data.opts.get(index).name = editable.toString();
            }
        });

        priceEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                itemVo.price = editable.toString();
                foodVo.food_data.opts.get(index).price = editable.toString();
            }
        });

        nameEdit.setOnFocusChangeListener(this);
        priceEdit.setOnFocusChangeListener(this);

        ImageButton deleteBtn = v.findViewById(R.id.menuEditDeleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                foodVo.food_data.opts.remove(itemVo);
                optLayout.removeView(v);
            }
        });
        return v;
    }

    private View newDemandView(final int index) {
        final DemandsItemVo demandsItemVo = foodVo.food_data.demands.get(index);
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.menu_detail_demand, null);
        ImageButton addBtn = view.findViewById(R.id.demandAddBtn);
        addBtn.setVisibility(View.VISIBLE);

        final EditText demandNameEdit = view.findViewById(R.id.demandNameEdit);
        demandNameEdit.setVisibility(View.VISIBLE);
        demandNameEdit.setText(demandsItemVo.name);
        demandNameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                demandsItemVo.name = editable.toString();
                foodVo.food_data.demands.get(index).name = editable.toString();
            }
        });

        demandNameEdit.setOnFocusChangeListener(this);
        Button removeBtn = view.findViewById(R.id.demandRemoveBtn);
        removeBtn.setVisibility(View.VISIBLE);
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                foodVo.food_data.demands.remove(demandsItemVo);
                demandLayout.removeView(view);
            }
        });

        final LinearLayout demandLayout = view.findViewById(R.id.demandLayout);
        for (int i = 0; i < foodVo.food_data.demands.get(index).datas.size(); i++) {
            demandLayout.addView(addDemandEditView(demandLayout, index, i));
        }

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                foodVo.food_data.demands.get(index).datas.add(new ItemVo());
                demandLayout.addView(addDemandEditView(demandLayout, index, foodVo.food_data.demands.get(index).datas.size() - 1));
            }
        });

        return view;
    }

    private View addDemandEditView(final LinearLayout demandLayout, final int index, final int subIndex) {
        final ItemVo itemVo = foodVo.food_data.demands.get(index).datas.get(subIndex);
        final View v = LayoutInflater.from(getContext()).inflate(R.layout.seller_edit_menu_detail, null);
        final EditText nameEdit = v.findViewById(R.id.nameEdit);
        nameEdit.setText(itemVo.name);

        nameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                itemVo.name = editable.toString();
                foodVo.food_data.demands.get(index).datas.get(subIndex).name = editable.toString();
            }
        });
        nameEdit.setOnFocusChangeListener(this);
        ImageButton deleteBtn = v.findViewById(R.id.menuEditDeleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                foodVo.food_data.demands.get(index).datas.remove(itemVo);
                demandLayout.removeView(v);
            }
        });
        return v;
    }

    private String verifyData() {
        for (int i = 0; i < foodVo.food_data.scopes.size(); i++) {
            if (Strings.isNullOrEmpty(foodVo.food_data.scopes.get(i).name)) {
                return "規格名稱不可空白！";
            }
            if (Strings.isNullOrEmpty(foodVo.food_data.scopes.get(i).price)) {
                return "規格價格不可為空白！";
            } else {
                if (Integer.parseInt(foodVo.food_data.scopes.get(i).price) <= 0) {
                    return "規格價格不可為0！";
                }
            }
        }

        for (int i = 0; i < foodVo.food_data.opts.size(); i++) {
            if (Strings.isNullOrEmpty(foodVo.food_data.opts.get(i).name)) {
                return "追加項目名稱不可空白！";
            }
            if (Strings.isNullOrEmpty(foodVo.food_data.opts.get(i).price)) {
                return "追加項目價格不可為空白！";
            }
        }

        for (int i = 0; i < foodVo.food_data.demands.size(); i++) {
            if (Strings.isNullOrEmpty(foodVo.food_data.demands.get(i).name)) {
                return "需求主名稱不可空白！";
            }
            for (int j = 0; j < foodVo.food_data.demands.get(i).datas.size(); j++) {
                if (Strings.isNullOrEmpty(foodVo.food_data.demands.get(i).datas.get(j).name)) {
                    return "需求細項名稱不可空白！";
                }
            }
        }
        return "";
    }

}
