package com.melonltd.naber.model.api;

import android.content.Context;
import android.net.ConnectivityManager;

import com.bigkoo.alertview.AlertView;
import com.melonltd.naber.model.service.Base64Service;
import com.melonltd.naber.util.Tools;
import com.melonltd.naber.vo.RespData;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public abstract class ApiCallback implements Callback {

//    private static final String TAG = ThreadCallback.class.getSimpleName();

    abstract public void onSuccess(String responseBody);

    abstract public void onFail(final Exception error, String msg);

    private Context context;

    public ApiCallback(Context context) {
        this.context = context;
    }

    @Override
    public void onFailure(Call call, final IOException e) {
        if (checkNetWork(context)) {
            getAlertView(context);
            return;
        }
        if (e.getMessage().contains("Canceled") || e.getMessage().contains("Socket closed")) {
        } else {
            onFail(e, e.getMessage());
        }
    }

    @Override
    public void onResponse(final Call call, final Response response) throws IOException {
        if (!response.isSuccessful() || response.body() == null) {
            onFailure(call, new IOException("Failed"));
            return;
        }
        try {
            final String resp = Base64Service.decryptBASE64(response.body().string());
            RespData data = Tools.JSONPARSE.fromJson(resp, RespData.class);
            if (data.status.toUpperCase().equals("TRUE")){
                onSuccess(Tools.JSONPARSE.toJson(data.data));
            }else {
                onFail(new IOException("Failed"), data.err_msg);
            }

        } catch (Exception e) {
            onFailure(call, new IOException("Failed"));
        }
    }

    public static boolean checkNetWork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return !Tools.NETWORK.hasNetWork(cm);
    }

    private static void getAlertView(Context context) {
        new AlertView.Builder()
                .setContext(context)
                .setStyle(AlertView.Style.Alert)
                .setTitle("網路連線錯誤")
                .setMessage("請檢查 Wi-Fi 或 4G是否已連接。")
                .setDestructive("確定")
                .build()
                .setCancelable(true)
                .show();
    }
}
