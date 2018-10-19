package com.melonltd.naber.view.common.page;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnDismissListener;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.firebase.iid.FirebaseInstanceId;
import com.melonltd.naber.model.api.ApiManager;
import com.melonltd.naber.model.api.ThreadCallback;
import com.melonltd.naber.util.VerifyUtil;
import com.melonltd.naber.view.common.BaseActivity;
import com.melonltd.naber.view.factory.PageType;
import com.melonltd.naber.R;

import java.util.Map;

public class RegisteredSellerFragment extends Fragment implements View.OnClickListener {
//    private static final String TAG = RegisteredSellerFragment.class.getSimpleName();
    public static RegisteredSellerFragment FRAGMENT = null;
    private EditText nameEdit, addressEdit, contactPersonEdit, contactPhoneEdit;

    public RegisteredSellerFragment() {
    }

    public Fragment newInstance(Object... o) {
        return new RegisteredSellerFragment();
    }

    public Fragment getInstance(Bundle bundle) {
        if (FRAGMENT == null) {
            FRAGMENT = new RegisteredSellerFragment();
            FRAGMENT.setArguments(bundle);
        }
        return FRAGMENT;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_registered_seller, container, false);
        getViews(v);
        return v;
    }

    private void getViews(View v) {
        nameEdit = v.findViewById(R.id.storeNameEditText);
        addressEdit = v.findViewById(R.id.storeAddressEditText);
        contactPersonEdit = v.findViewById(R.id.sellerContactPersonEditText);
        contactPhoneEdit = v.findViewById(R.id.sellerContactPhoneEditText);

        Button submitBun = v.findViewById(R.id.sellerSubmitBun);
        Button backToLoginBtn = v.findViewById(R.id.sellerBackToLoginBtn);
        HideKeyboard hideKeyboard = new HideKeyboard();
        nameEdit.setOnFocusChangeListener(hideKeyboard);
        addressEdit.setOnFocusChangeListener(hideKeyboard);
        contactPersonEdit.setOnFocusChangeListener(hideKeyboard);
        contactPhoneEdit.setOnFocusChangeListener(hideKeyboard);
        submitBun.setOnClickListener(this);
        backToLoginBtn.setOnClickListener(this);
        v.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        BaseActivity.changeToolbarStatus();
    }

    @Override
    public void onClick(View v) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        switch (v.getId()) {
            case R.id.sellerSubmitBun:
                if (verifyInput()) {
                    Map<String, String> map = Maps.newHashMap();
                    map.put("seller_name", nameEdit.getText().toString());
                    map.put("phone", contactPhoneEdit.getText().toString());
                    map.put("address", addressEdit.getText().toString());
                    map.put("device_id", FirebaseInstanceId.getInstance().getToken());
                    map.put("name", contactPersonEdit.getText().toString());
                    ApiManager.sellerRegistered(map, new ThreadCallback(getContext()) {
                        @Override
                        public void onSuccess(String responseBody) {
                            new AlertView.Builder()
                                    .setTitle("")
                                    .setMessage("感謝你註冊成為商家你，\n您的信息已經提交成功，\n請待客服與您聯繫!!")
                                    .setContext(getContext())
                                    .setStyle(AlertView.Style.Alert)
                                    .setOthers(new String[]{"確定"})
                                    .build()
                                    .setOnDismissListener(new OnDismissListener() {
                                        @Override
                                        public void onDismiss(Object o) {
                                            BaseActivity.removeAndReplaceWhere(FRAGMENT, PageType.LOGIN, null);
                                        }
                                    })
                                    .setCancelable(true)
                                    .show();
                        }

                        @Override
                        public void onFail(Exception error, String msg) {
                            BaseActivity.removeAndReplaceWhere(FRAGMENT, PageType.LOGIN, null);
                        }
                    });
                }
                break;
            case R.id.sellerBackToLoginBtn:
                BaseActivity.removeAndReplaceWhere(FRAGMENT, PageType.LOGIN, null);
                break;
        }
    }

    private boolean verifyInput() {
        boolean result = true;
        String message = "";
        if (Strings.isNullOrEmpty(nameEdit.getText().toString())) {
            message = "請輸入商店名稱";
            result = false;
        }
        if (Strings.isNullOrEmpty(addressEdit.getText().toString())) {
            message = "請輸入地址";
            result = false;
        }
        if (Strings.isNullOrEmpty(contactPersonEdit.getText().toString())) {
            message = "請輸入聯絡人";
            result = false;
        }
        if (!VerifyUtil.phoneNumber(contactPhoneEdit.getText().toString())) {
            message = "請輸入手機號碼，以便客服與您聯繫!";
            result = false;
        }
        if (!result) {
            new AlertView.Builder()
                    .setTitle("")
                    .setMessage(message)
                    .setContext(getContext())
                    .setStyle(AlertView.Style.Alert)
                    .setCancelText("取消")
                    .build()
                    .setCancelable(true)
                    .show();
        }
        return result;
    }

    class HideKeyboard implements View.OnFocusChangeListener {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }
}
