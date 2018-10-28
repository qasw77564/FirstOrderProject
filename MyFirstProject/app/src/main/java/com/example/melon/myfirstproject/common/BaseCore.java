package com.example.melon.myfirstproject.common;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.Toast;

import com.example.melon.myfirstproject.model.Model;
import com.example.melon.myfirstproject.util.Tools;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.Lists;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class BaseCore extends AppCompatActivity implements LocationListener {
    public static Context context;
    private static ConnectivityManager cm;
    public static LocationManager LOCATION_MG;

//    public static String FRAGMENT_TAG = PageType.LOGIN.name();

    public static final int CAMERA_CODE = 8765;
    public static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};

    public static final int LOCATION_CODE = 9876;
    public static final String[] LOCATION_PERMISSION = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    public static final int IO_STREAM_CODE = 1987;
    public static final String[] IO_STREAM = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    public static FirebaseUser currentUser;

    public static boolean IS_USER = true;
    public static boolean IS_HAS_ACC = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        // TODO init Manager
        cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

//        // TODO 權限相關
//        // 相機權限
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, CAMERA_PERMISSION, CAMERA_CODE);
//        }
//
        // TODO GPS 權限
        if (checkLocationPermission(this)) {
            ActivityCompat.requestPermissions(this, LOCATION_PERMISSION, LOCATION_CODE);
        } else {
            setLocationListener();
        }

//        // TODO 寫入權限
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, IO_STREAM, IO_STREAM_CODE);
//        }


        // TODO  init SharedPreferences
//        SPService.getInstance(getSharedPreferences(getString(R.string.shared_preferences_key), Context.MODE_PRIVATE));
    }


    // TODO 相機權限
    public static boolean checkCameraPermission(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED;
    }

    // TODO GPS 權限
    public static boolean checkLocationPermission(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
    }

    // TODO 寫入權限
    public static boolean checkWritePermission(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
    }

//    public static void getCurrentUser(Activity activity) {
//        final FirebaseAuth auth = FirebaseAuth.getInstance();
//        auth.signInWithEmailAndPassword(NaberConstant.FIREBASE_ACCOUNT, NaberConstant.FIREBASE_PSW)
//                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            currentUser = auth.getCurrentUser();
//                        }
//                    }
//                });
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Tools.GoogleVersion.checkVersion(context, this);
//        // TODO check mobile has net work
//        if (!Tools.NETWORK.hasNetWork(cm)) {
//            // TODO show Dialog
//            Log.d(TAG, "no NETWORK? ???");
//        } else {
//            // TODO check google service version

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_CODE:
                if (checkGrantResults(grantResults)) {
                    setLocationListener();
                }
                break;
            case CAMERA_CODE:
                break;
            case IO_STREAM_CODE:
                break;
        }
    }

    public static boolean checkGrantResults(int[] grantResults) {
        if (grantResults == null) {
            return false;
        }
        boolean status = false;
        for (int i = 0; i < grantResults.length; i++) {
            status = grantResults[i] == PackageManager.PERMISSION_GRANTED;
        }
        if (!status) {
//            Toast.makeText(context, context.getString(R.string.app_name) + "權限受限制會影響部分功能，請於管理應用程式設定權限", Toast.LENGTH_LONG).show();
        }
        return status;
    }


//    // TODO 偏好儲存設定 寫入
//    public SharedPreferences getPreferencesEditor() {
//        return getSharedPreferences(getString(R.string.shared_preferences_key), Context.MODE_PRIVATE);
//    }
//
//    // TODO 偏好儲存設定 讀取
//    public SharedPreferences getRead() {
//        return getPreferences(Context.MODE_PRIVATE);
//    }

    @SuppressLint("MissingPermission")
    private void setLocationListener() {
        LOCATION_MG = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LOCATION_MG.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        LOCATION_MG.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        LOCATION_MG = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LOCATION_MG.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        LOCATION_MG.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        LOCATION_MG.isProviderEnabled(LocationManager.GPS_PROVIDER);
        LOCATION_MG.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Model.LOCATION = LOCATION_MG.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onLocationChanged(Location loc) {
        if (loc != null) {
            Model.LOCATION = loc;
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static void loadJsonData(Context context) {
        String jsonData = Tools.JSONPARSE.getJson(context, "identity.json");
//        ArrayList<IdentityJsonBean> identityBean = Tools.JSONPARSE.parseData(JsonData);
//        List<IdentityJsonBean> identityBeans= Tools.JSONPARSE.fromJsonList(jsonData, IdentityJsonBean[].class);
        List<String> opt1 = Lists.newArrayList();
        List<List<String>> opt2 = Lists.newArrayList();
//        for (IdentityJsonBean b : identityBeans) {
//            opt1.add(b.getName());
//            List<String> datas = Lists.newArrayList();
//            for (String d : b.getDatas()) {
//                datas.add(d);
//            }
//            opt2.add(datas);
//        }
        Model.OPT_ITEM_1.clear();
        Model.OPT_ITEM_1.addAll(opt1);
        Model.OPT_ITEM_2.clear();
        Model.OPT_ITEM_2.addAll(opt2);
    }
}
