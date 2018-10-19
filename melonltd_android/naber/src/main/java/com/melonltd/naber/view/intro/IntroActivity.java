package com.melonltd.naber.view.intro;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro2;
import com.melonltd.naber.R;
import com.melonltd.naber.model.service.SPService;

public class IntroActivity extends AppIntro2 {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setFadeAnimation();
//        showSkipButton(false);
//        showStatusBar(false);
//        showSkipButton(false);
//        showDoneButton(true);

        addSlide(Slide.newInstance(R.layout.intro_1));
        addSlide(Slide.newInstance(R.layout.intro_2));
        addSlide(Slide.newInstance(R.layout.intro_3));
        addSlide(Slide.newInstance(R.layout.intro_4));

        setFadeAnimation();
        showSkipButton(false);
        showStatusBar(false);
        showSkipButton(false);
        showDoneButton(true);

    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        SPService.setIsFirstLogin(false);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }

}
