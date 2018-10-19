package com.melonltd.naber.view.customize;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.RadioButton;

import com.google.common.base.Strings;
import com.melonltd.naber.R;

import java.io.Serializable;

public class NaberRadioButton implements Serializable {
    private static final long serialVersionUID = -395093075821576420L;
    private RadioButton radio;
    private String title = "\u0020", price = "\u0020", symbol = "\u0020";
    private String text = "";

    public NaberRadioButton() {

    }

    private NaberRadioButton setParameter(RadioButton radio, String title, String price, String symbol) {
        this.radio = radio;
        setTitleAndPriceAndSymbol(title, price, symbol);
        return this;
    }

    public static Builder Builder(Context context) {
        return new Builder(context);
    }

    private void setTitleAndPriceAndSymbol(String title, String price, String symbol) {
        this.title = Strings.padEnd(title, 20, '\u0020');
        this.price = Strings.padEnd(price, 10, '\u0020');
        this.symbol = Strings.padEnd(symbol, 3, '\u0020');
        this.text = this.title + this.symbol + this.price;
        this.radio.setText(this.text);
    }

    private RadioButton getView() {
        return this.radio;
    }

    public static class Builder {
        private RadioButton radio;
        private NaberRadioButton naberRadio;
        private String title = "\u0020", price = "\u0020", symbol = "\u0020";

        Builder(Context context) {
            this.naberRadio = new NaberRadioButton();
            this.radio = new RadioButton(context, null, R.attr.radioButtonStyle);
            this.radio.setGravity(Gravity.START);
            this.radio.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            this.radio.setText("");
            this.radio.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            this.radio.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            this.radio.setPaddingRelative(50, 12, 0, 0);
        }

        public Builder setChecked(boolean checked) {
            this.radio.setChecked(checked);
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setPrice(String price) {
            this.price = price;
            return this;
        }

        public Builder setId(int id) {
            this.radio.setId(id);
            return this;
        }

        public Builder setTag(int id, Object o) {
            this.radio.setTag(id, o);
            return this;
        }

        public Builder setPadding(int left, int top, int right, int bottom) {
            this.radio.setPaddingRelative(left, top, right, bottom);
            return this;
        }

        public Builder setPadding(int padding) {
            setPadding(padding, padding, padding, padding);
            return this;
        }

        public Builder setSymbol(String symbol) {
            this.symbol = symbol;
            return this;
        }

        public Builder setTag(Object o) {
            this.radio.setTag(o);
            return this;
        }

        public RadioButton build() {
            return this.naberRadio.setParameter(this.radio, this.title, this.price, this.symbol).getView();
        }
    }


}
