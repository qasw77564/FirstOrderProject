package com.melonltd.naber.view.common;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class HideKeyboardListener implements View.OnClickListener {
    @Override
    public void onClick(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
