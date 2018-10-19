package com.melonltd.naber.view.seller.page;


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
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.melonltd.naber.R;
import com.melonltd.naber.model.api.ApiManager;
import com.melonltd.naber.model.api.ThreadCallback;
import com.melonltd.naber.model.service.SPService;
import com.melonltd.naber.util.VerifyUtil;
import com.melonltd.naber.view.factory.PageType;
import com.melonltd.naber.view.seller.SellerMainActivity;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class SellerResetPasswordFragment extends Fragment implements View.OnClickListener {
    public static SellerResetPasswordFragment FRAGMENT = null;

    private EditText oldPasswordEdit, newPasswordEdit, confirmPasswordEdit;

    public SellerResetPasswordFragment() {
        // Required empty public constructor
    }

    public Fragment getInstance(Bundle bundle) {
        if (FRAGMENT == null) {
            FRAGMENT = new SellerResetPasswordFragment();
        }
        if (bundle != null) {
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
        View v = inflater.inflate(R.layout.fragment_seller_reset_password, container, false);

        oldPasswordEdit = v.findViewById(R.id.oldPasswordEdit);
        newPasswordEdit = v.findViewById(R.id.newPasswordEdit);
        confirmPasswordEdit = v.findViewById(R.id.confirmPasswordEdit);
        Button submit = v.findViewById(R.id.submitBtn);

        v.setOnClickListener(this);
        submit.setOnClickListener(this);
        HideKeyboard hideKeyboard = new HideKeyboard();
        oldPasswordEdit.setOnFocusChangeListener(hideKeyboard);
        newPasswordEdit.setOnFocusChangeListener(hideKeyboard);
        confirmPasswordEdit.setOnFocusChangeListener(hideKeyboard);
        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        SellerMainActivity.changeTabAndToolbarStatus();

        if (SellerMainActivity.toolbar != null) {
            SellerMainActivity.navigationIconDisplay(true, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SellerDetailFragment.TO_RESET_PASSWORD_INDEX = -1;
                    SellerMainActivity.removeAndReplaceWhere(FRAGMENT, PageType.SELLER_SET_UP, null);
                    SellerMainActivity.navigationIconDisplay(false, null);

                }
            });
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        SellerMainActivity.navigationIconDisplay(false, null);
    }

    @Override
    public void onClick(View v) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

        if (v.getId() == R.id.submitBtn) {
            if (verifyInput()) {
                Map<String, String> map = Maps.newHashMap();
                map.put("phone", SPService.getUserPhone());
                map.put("old_password", oldPasswordEdit.getText().toString());
                map.put("password", newPasswordEdit.getText().toString());
                ApiManager.reseatPassword(map, new ThreadCallback(getContext()) {
                    @Override
                    public void onSuccess(String responseBody) {
                        SPService.setLoginLimit(0L);
                        getActivity().finish();
                        SellerMainActivity.clearAllFragment();
                    }

                    @Override
                    public void onFail(Exception error, String msg) {
                    }
                });

            }
        }
    }


    private boolean verifyInput() {
        boolean result = true;
        String message = "";
        if (Strings.isNullOrEmpty(oldPasswordEdit.getText().toString())) {
            message = "請填入舊密碼";
            result = false;
        }
        if (Strings.isNullOrEmpty(newPasswordEdit.getText().toString())) {
            message = "請填入新密碼";
            result = false;
        }
        if (Strings.isNullOrEmpty(confirmPasswordEdit.getText().toString())) {
            message = "請填入確認密碼";
            result = false;
        }

        // 驗證密碼不為空 並需要英文大小寫數字 6 ~ 20
        if (!VerifyUtil.password(newPasswordEdit.getText().toString())) {
            message = "驗證密碼不為空 並需要英文大小寫數字 6 ~ 20";
            result = false;
        }

        if (!confirmPasswordEdit.getText().toString().equals(newPasswordEdit.getText().toString())) {
            message = "驗證新密碼與確認密碼一致";
            result = false;
        }

        if (confirmPasswordEdit.getText().toString().equals(oldPasswordEdit.getText().toString())) {
            message = "舊密碼不可與新密碼相同，\n請重新輸入!";
            newPasswordEdit.setText("");
            confirmPasswordEdit.setText("");
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
