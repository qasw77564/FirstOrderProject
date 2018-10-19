package com.melonltd.naber.model.api;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;

import com.bigkoo.alertview.AlertView;
import com.melonltd.naber.model.service.Base64Service;
import com.melonltd.naber.util.LoadingBarTools;
import com.melonltd.naber.util.Tools;
import com.melonltd.naber.vo.RespData;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public abstract class ThreadCallback implements Callback {
    private static final String TAG = ThreadCallback.class.getSimpleName();
    private AlertDialog DIALOG = null;

    abstract public void onSuccess(String responseBody);

    abstract public void onFail(final Exception error, String msg);

    private Context context;

    public ThreadCallback(Context context) {
        this.context = context;
        if (context instanceof Activity) {
            this.DIALOG = LoadingBarTools.newLoading(context);
        }
    }


    @Override
    public void onFailure(Call call, final IOException e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 如果是 network 錯誤
                if (checkNetWork()) {
                    if (DIALOG != null){
                        DIALOG.cancel();
                    }
                    getAlertView();
                    return;
                }
                if (e.getMessage().contains("Canceled") || e.getMessage().contains("Socket closed")) {

                } else {
                    if (DIALOG != null){
                        DIALOG.cancel();
                    }
                    onFail(e, e.getMessage());
                }

            }
        });
    }

    @Override
    public void onResponse(final Call call, final Response response) throws IOException {
        if (!response.isSuccessful() || response.body() == null) {
            onFailure(call, new IOException("Failed"));
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String resp = Base64Service.decryptBASE64(response.body().string());
                    RespData data = Tools.JSONPARSE.fromJson(resp, RespData.class);
                    if (data.status.toUpperCase().equals("TRUE")){
                        if (DIALOG != null){
                            DIALOG.cancel();
                        }
                        onSuccess(Tools.JSONPARSE.toJson(data.data));
                    }else {
                        if (DIALOG != null) {
                            DIALOG.cancel();
                        }
                        onFail(new IOException("Failed"), data.err_msg);
                    }
                } catch (Exception e) {

                    onFailure(call, new IOException("Failed"));
                }

            }
        });
    }


    public boolean checkNetWork() {
        ConnectivityManager cm = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return !Tools.NETWORK.hasNetWork(cm);
    }


    private void runOnUiThread(Runnable task) {
        new Handler(Looper.getMainLooper()).post(task);
    }

    private void getAlertView() {
        if (context instanceof Activity) {
            new AlertView.Builder()
                    .setContext(this.context)
                    .setStyle(AlertView.Style.Alert)
                    .setTitle("網路連線錯誤")
                    .setMessage("請檢查 Wi-Fi 或 4G是否已連接。")
                    .setDestructive("確定")
                    .build()
                    .setCancelable(true)
                    .show();
        }
    }
}
