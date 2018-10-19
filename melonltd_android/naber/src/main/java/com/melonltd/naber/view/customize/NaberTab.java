package com.melonltd.naber.view.customize;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v7.content.res.AppCompatResources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.melonltd.naber.R;

import java.io.Serializable;

public class NaberTab implements Serializable {
    private static final long serialVersionUID = -595086961009546689L;
    View view;
    ImageView icon;
    TextView titel;
    Context context;

    public NaberTab(Context context) {
        this.context = context;
        this.view = LayoutInflater.from(context).inflate(R.layout.naber_user_tab_item, null);
        this.icon = view.findViewById(R.id.tabIcon);
        this.titel = view.findViewById(R.id.tabTitle);
    }

    private NaberTab setParameter(Context context, @StringRes int titleResId, @DrawableRes int iconResId) {
        icon.setImageDrawable(AppCompatResources.getDrawable(context, iconResId));
        titel.setText(titleResId);
        return this;
    }

    public View getView() {
        return this.view;
    }

    public Builder Builder() {
        return new Builder(context);
    }


    public static class Builder {
        int title, icon;
        NaberTab tab;
        Context context;

        Builder(Context context) {
            this.context = context;
            this.tab = new NaberTab(context);
        }

        public Builder setTitle(@StringRes int resId) {
            this.title = resId;
            return this;
        }

        public Builder setIcon(@DrawableRes int resId) {
            this.icon = resId;
            return this;
        }

        public View build() {
            return this.tab.setParameter(this.context, this.title, this.icon).getView();
        }

    }
}
