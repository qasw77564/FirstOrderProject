package com.melonltd.naber.util;

import android.net.Uri;

public interface UpLoadCallBack {
    void getUri(Uri uri);

    void failure(String errMsg);
}
