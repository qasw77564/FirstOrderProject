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
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.melonltd.naber.R;
import com.melonltd.naber.model.api.ApiManager;
import com.melonltd.naber.model.api.ThreadCallback;
import com.melonltd.naber.util.VerifyUtil;
import com.melonltd.naber.view.common.BaseActivity;
import com.melonltd.naber.view.factory.PageType;

import java.util.Iterator;
import java.util.Map;

public class RecoverPasswordFragment extends Fragment implements View.OnClickListener {
//    private static final String TAG = RecoverPasswordFragment.class.getSimpleName();
    public static RecoverPasswordFragment FRAGMENT = null;
//    private EditText mailEdit, phoneEdit;
    private EditText phoneEdit;

    public RecoverPasswordFragment() {
    }

    public Fragment getInstance(Bundle bundle) {
        if (FRAGMENT == null) {
            FRAGMENT = new RecoverPasswordFragment();
            FRAGMENT.setArguments(bundle);
        }
        return FRAGMENT;
    }

    public Fragment newInstance(Object... o) {
        return new RecoverPasswordFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recover_password, container, false);
        getView(v);
        return v;
    }

    private void getView(View v) {
        Button submitBtn = v.findViewById(R.id.submitRecoverPasswordBtn);
//        mailEdit = v.findViewById(R.id.emailEdit);
        phoneEdit = v.findViewById(R.id.phoneEdit);

        HideKeyboard hideKeyboard = new HideKeyboard();
//        mailEdit.setOnFocusChangeListener(hideKeyboard);
        phoneEdit.setOnFocusChangeListener(hideKeyboard);
        submitBtn.setOnClickListener(this);
        v.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        BaseActivity.changeToolbarStatus();
//        mailEdit.setText("");
        phoneEdit.setText("");
        if (BaseActivity.toolbar != null) {
            BaseActivity.navigationIconDisplay(true, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BaseActivity.removeAndReplaceWhere(FRAGMENT, PageType.LOGIN, null);
                    BaseActivity.navigationIconDisplay(false, null);
                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        BaseActivity.navigationIconDisplay(false, null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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

    @Override
    public void onClick(View v) {

        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        if (v.getId() == R.id.submitRecoverPasswordBtn) {
            if (verifyInput()) {
                Map<String, String> req  = Maps.newHashMap();
                req.put("phone", phoneEdit.getText().toString());
//                req.put("email", mailEdit.getText().toString());
                ApiManager.forgetPassword(req, new ThreadCallback(getContext()) {
                    @Override
                    public void onSuccess(String responseBody) {
//                        Log.d(TAG, responseBody);
                        new AlertView.Builder()
                                .setContext(getContext())
                                .setStyle(AlertView.Style.Alert)
                                .setTitle("")
                                .setMessage("密碼已經傳送至於您的手機，\n請於簡訊功能查看！")
                                .setCancelText("關閉")
                                .build()
                                .setOnDismissListener(new OnDismissListener() {
                                    @Override
                                    public void onDismiss(Object o) {
                                        BaseActivity.removeAndReplaceWhere(FRAGMENT, PageType.LOGIN, null);
                                        BaseActivity.navigationIconDisplay(false, null);
                                    }
                                })
                                .setCancelable(true)
                                .show();
                    }

                    @Override
                    public void onFail(Exception error, String msg) {
                        String message = "";
                        Iterator<String> iterator = Splitter.on("$split").split(msg).iterator();
                        while (iterator.hasNext()){
                            message += iterator.next() + "\n";
                        }
                        new AlertView.Builder()
                                .setContext(getContext())
                                .setStyle(AlertView.Style.Alert)
                                .setTitle("")
                                .setMessage(message)
                                .setCancelText("關閉")
                                .build()
                                .setCancelable(true)
                                .show();
                    }
                });
            }
        }
    }

    private boolean verifyInput() {
        boolean result = true;
        String message = "";

        // 驗證手機號碼錯誤格式
        if (!VerifyUtil.phoneNumber(phoneEdit.getText().toString())) {
            message = "請輸入正確手機號碼，避免無法正常接收SMS";
            result = false;
        }

//        // 驗證Email錯誤格式
//        if (!VerifyUtil.email(mailEdit.getText().toString())) {
//            message = "Email錯誤格式，請重新輸入";
//            result = false;
//        }

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
