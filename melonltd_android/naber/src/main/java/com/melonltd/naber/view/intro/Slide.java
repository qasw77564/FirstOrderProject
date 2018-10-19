package com.melonltd.naber.view.intro;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Slide extends Fragment {

    private static final String ARG_LAYOUT_RES_ID = "layoutResId";
//    private static final String INTRO_RES_ID = "introResId";
    private int layoutResId;
//    private int introResId;

    public static Slide newInstance(int layoutResId) {
        Slide slide = new Slide();

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
//        args.putInt(INTRO_RES_ID, introResId);
        slide.setArguments(args);

        return slide;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ARG_LAYOUT_RES_ID)) {
            this.layoutResId = getArguments().getInt(ARG_LAYOUT_RES_ID);
//            this.introResId = getArguments().getInt(INTRO_RES_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        savedInstanceState.getInt(ARG_LAYOUT_RES_ID);
        View v = inflater.inflate(this.layoutResId, container, false);
//        ImageView introImage = v.findViewById(R.id.introImage);
//        introImage.setImageResource(this.introResId);
        return v;
//        return inflater.inflate(layoutResId, container, false);
    }
}