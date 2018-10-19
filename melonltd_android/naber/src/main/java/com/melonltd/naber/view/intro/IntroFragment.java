package com.melonltd.naber.view.intro;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.melonltd.naber.R;
public class IntroFragment extends Fragment {

    private static IntroFragment FRAGMENT = null;

    public static IntroFragment getInstance() {
        if (FRAGMENT == null){
            FRAGMENT = new IntroFragment();
        }
        return FRAGMENT;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        startActivity(new Intent(getActivity().getBaseContext(), IntroActivity.class));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_intro, container, false);
    }
}
