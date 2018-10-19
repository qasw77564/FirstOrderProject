package com.melonltd.naber.view.common.page;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.google.common.base.Strings;
import com.google.firebase.iid.FirebaseInstanceId;
import com.melonltd.naber.R;
import com.melonltd.naber.model.api.ApiManager;
import com.melonltd.naber.model.api.ThreadCallback;
import com.melonltd.naber.model.service.SPService;
import com.melonltd.naber.model.type.Identity;
import com.melonltd.naber.util.Tools;
import com.melonltd.naber.view.common.BaseActivity;
import com.melonltd.naber.view.factory.PageType;
import com.melonltd.naber.view.seller.SellerMainActivity;
import com.melonltd.naber.view.user.UserMainActivity;
import com.melonltd.naber.vo.AccountInfoVo;

import java.util.Date;

public class LoginFragment extends Fragment implements View.OnClickListener {
//    private static final String TAG = LoginFragment.class.getSimpleName();
    public static LoginFragment FRAGMENT = null;
    private EditText accountEdit, passwordEdit;
    private CheckBox rememberMe;

    public LoginFragment() {
    }

    public Fragment getInstance(Bundle bundle) {
        if (FRAGMENT == null) {
            FRAGMENT = new LoginFragment();
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
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        getView(v);
        return v;
    }

    private void getView(View v) {
        accountEdit = v.findViewById(R.id.accountEdit);
        passwordEdit = v.findViewById(R.id.passwordEdit);
        rememberMe = v.findViewById(R.id.rememberMeCheckBox);

        Button loginBtn = v.findViewById(R.id.loginBtn);
        Button toVerifySMSBtn = v.findViewById(R.id.toVerifySMSBtn);
        Button toRegisteredSellerBtn = v.findViewById(R.id.toRegisteredSellerBtn);
        TextView recoverPasswordText = v.findViewById(R.id.recoverPasswordText);
        ConstraintLayout largeLabel = v.findViewById(R.id.largeLabel);
        // setListener
        HideKeyboard hideKeyboard = new HideKeyboard();
        accountEdit.setOnFocusChangeListener(hideKeyboard);
        passwordEdit.setOnFocusChangeListener(hideKeyboard);
        rememberMe.setOnFocusChangeListener(hideKeyboard);
        loginBtn.setOnClickListener(this);

        toVerifySMSBtn.setOnClickListener(this);
        toRegisteredSellerBtn.setOnClickListener(this);
        recoverPasswordText.setOnClickListener(this);
        largeLabel.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        BaseActivity.changeToolbarStatus();
        accountEdit.setText(SPService.getAccout());
        rememberMe.setChecked(SPService.getRememberMe());
    }

    @Override
    public void onClick(View v) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//        Fragment fragment = null;
        switch (v.getId()) {
            case R.id.loginBtn:
                if (verifyInput()) {
                    AccountInfoVo req = new AccountInfoVo();
                    req.phone = accountEdit.getText().toString();
                    req.password = passwordEdit.getText().toString();
                    req.device_token = FirebaseInstanceId.getInstance().getToken();
                    req.device_category = "ANDROID";
                    ApiManager.login(req, new ThreadCallback(getContext()) {
                        @Override
                        public void onSuccess(String responseBody) {
                            AccountInfoVo resp = Tools.JSONPARSE.fromJson(responseBody, AccountInfoVo.class);
                            SPService.setOauth(resp.account_uuid);
                            SPService.setUserName(resp.name);
                            SPService.setAccout(resp.account);
                            SPService.setUserPhone(resp.phone);
                            SPService.setOauth(resp.account_uuid);
                            SPService.setIdentity(resp.identity);

                            SPService.setRememberMe(rememberMe.isChecked());
                            SPService.setLoginLimit(new Date().getTime());
                            SPService.setRememberAccount(resp.phone);

                            if (Identity.getUserValues().contains(Identity.of(resp.identity))) {
//                                BaseCore.loadRestaurantTemplate(getContext());
                                startActivity(new Intent(getActivity().getBaseContext(), UserMainActivity.class));
                            } else if (Identity.SELLERS.equals(Identity.of(resp.identity))) {
                                startActivity(new Intent(getActivity().getBaseContext(), SellerMainActivity.class));
                            } else {
                                new AlertView.Builder()
                                        .setContext(getContext())
                                        .setStyle(AlertView.Style.Alert)
                                        .setTitle("")
                                        .setMessage("你的帳戶無法使用該APP，\n請確認你的帳號!")
                                        .setCancelText("關閉")
                                        .build()
                                        .setCancelable(true)
                                        .show();
                            }
                        }

                        @Override
                        public void onFail(Exception error, String msg) {
                            new AlertView.Builder()
                                    .setTitle("")
                                    .setMessage(msg)
                                    .setContext(getContext())
                                    .setStyle(AlertView.Style.Alert)
                                    .setCancelText("取消")
                                    .build()
                                    .setCancelable(true)
                                    .show();
                        }
                    });
                }
                break;
            case R.id.toVerifySMSBtn:
                BaseActivity.removeAndReplaceWhere(FRAGMENT, PageType.VERIFY_SMS, null);
                break;
            case R.id.toRegisteredSellerBtn:
                BaseActivity.removeAndReplaceWhere(FRAGMENT, PageType.REGISTERED_SELLER, null);
                break;
            case R.id.recoverPasswordText:
                BaseActivity.removeAndReplaceWhere(FRAGMENT, PageType.RECOVER_PASSWORD, null);
                break;
        }
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

    private boolean verifyInput() {
        boolean result = true;
        String message = "";
        if (Strings.isNullOrEmpty(accountEdit.getText().toString())) {
            message = "請輸入帳號";
            result = false;
        }
        if (Strings.isNullOrEmpty(passwordEdit.getText().toString())) {
            message = "請輸入密碼";
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

}
