package com.company.tokoonline;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.company.tokoonline.config.AppController;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;

public class Splash extends AppCompatActivity {

    public static final String MyPREFERENCES = "MyPrefs" ;

    Context context;
    RequestQueue requestQueue;
    SharedPreferences sharedpreferences;

    private final Handler waitHandler = new Handler();
    private final Runnable waitCallback = () -> {

        Intent intent = new Intent(Splash.this, MainActivity.class);
        startActivity(intent);
        finish();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.activity_start);


        context = Splash.this;
        requestQueue = AppController.getInstance().getRequestQueue();
        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);

        TextView tv_version = findViewById(R.id.tv_version);



        String versionName = new BuildConfig().VERSION_NAME;
        int versionCode = new BuildConfig().VERSION_CODE;


        tv_version.setText(versionName+" build "+versionCode);



        requestStoragePermission();

    }


    private void requestStoragePermission() {

        String[] permissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        Permissions.check(this/*context*/, permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                // do your task.
                Log.e("izin", "Semua izin telah disetujui!");

                waitHandler.postDelayed(waitCallback, 2000);


            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                // permission denied, block the feature.
                showSettingsDialog();
            }
        });
    }


    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Membutuhkan Izin");
        builder.setMessage("Beberapa fitur diperlukan untuk aplikasi ini. Kamu Setujui di pengaturan.");
        builder.setPositiveButton("KE PENGATURAN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("BATAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }
}
