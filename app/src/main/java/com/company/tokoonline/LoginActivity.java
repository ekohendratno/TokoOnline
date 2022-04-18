package com.company.tokoonline;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.company.tokoonline.admin.AdminBarang;
import com.company.tokoonline.admin.AdminDashboard;
import com.company.tokoonline.config.AppController;
import com.company.tokoonline.config.Config;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LoginActivity extends AppCompatActivity{
    String TAG = "MainActivity";

    Context context;


    SharedPreferences sharedpreferences;
    RequestQueue requestQueue;

    private final Handler waitHandler = new Handler();
    private final Runnable waitCallback = new Runnable() {
        @Override
        public void run() {
            progressBar.setVisibility(View.GONE);

            String level = sharedpreferences.getString("level", "");

            if( level.equalsIgnoreCase("penjual") ){

                Intent intent = new Intent(LoginActivity.this, AdminDashboard.class);
                startActivity(intent);
                finish();

            }else{

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }

        }
    };


    ProgressBar progressBar;

    TextInputEditText editAkun, editPassword;
    LinearLayout formLoginLoading, formLogin;

    MaterialButton actSignIn;

    TabLayout tabLayout;
    RelativeLayout lytLogin;
    NestedScrollView lytDaftar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = LoginActivity.this;
        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
        requestQueue = AppController.getInstance().getRequestQueue();


        tabLayout = findViewById(R.id.tabs);

        lytLogin = findViewById(R.id.lytLogin);
        lytDaftar = findViewById(R.id.lytDaftar);



        /**
         * Loading
         */
        formLoginLoading = findViewById(R.id.formLoginLoading);
        formLogin = findViewById(R.id.formLogin);
        formLogin.setVisibility(View.VISIBLE);
        formLoginLoading.setVisibility(View.GONE);

        progressBar = findViewById(R.id.progressBar);


        editAkun =  findViewById(R.id.editAkun);
        editPassword =  findViewById(R.id.editPassword);



        actSignIn = findViewById(R.id.actSignIn);
        actSignIn.setOnClickListener(v->{
            v.setBackgroundColor(v.getResources().getColor(R.color.colorStatusBack));

            String username  = editAkun.getText().toString();
            String password  = editPassword.getText().toString();


            if(!TextUtils.isEmpty(username) || !TextUtils.isEmpty(username)){
                actSignIn.setEnabled(false);
                editAkun.setEnabled(false);
                editPassword.setEnabled(false);

                signIn(username, password);

            }else{
                Toast.makeText(context,"Maaf Akun atau Password ada yang kosong!",Toast.LENGTH_LONG).show();
            }

        });



        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                Log.e("p", String.valueOf(tab.getPosition()));

                lytLogin.setVisibility(View.VISIBLE);
                lytDaftar.setVisibility(View.GONE);

                if (tab.getPosition() == 1) {
                    lytLogin.setVisibility(View.GONE);
                    lytDaftar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



        findViewById(R.id.actionInfo).setOnClickListener(v->{
            Toast.makeText(LoginActivity.this,"Dikembangkan Tahun 2022",Toast.LENGTH_LONG).show();
        });

        findViewById(R.id.actionBack).setOnClickListener(v -> {
            onBackPressed();
        });


        AutoCompleteTextView user_jk1 = findViewById(R.id.user_jk);

        ArrayList<String> listJenisKelamin = new ArrayList<>();
        listJenisKelamin.add("Laki-laki");
        listJenisKelamin.add("Perempuan");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, listJenisKelamin);
        user_jk1.setAdapter(adapter);

        findViewById(R.id.actionSignup).setOnClickListener(v -> {
            TextInputEditText user_email = findViewById(R.id.user_email);
            TextInputEditText user_password = findViewById(R.id.user_password);
            TextInputEditText user_nama = findViewById(R.id.user_nama);
            AutoCompleteTextView user_jk = findViewById(R.id.user_jk);
            TextInputEditText user_notelp = findViewById(R.id.user_notelp);
            TextInputEditText user_alamat = findViewById(R.id.user_alamat);

            if( TextUtils.isEmpty(user_email.getText().toString()) ){
                Toast.makeText(LoginActivity.this,"Email kosong",Toast.LENGTH_LONG).show();
            }else if( TextUtils.isEmpty(user_password.getText().toString()) ){
                Toast.makeText(LoginActivity.this,"Password kosong",Toast.LENGTH_LONG).show();
            }else if( TextUtils.isEmpty(user_nama.getText().toString()) ){
                Toast.makeText(LoginActivity.this,"Nama kosong",Toast.LENGTH_LONG).show();
            }else if( TextUtils.isEmpty(user_notelp.getText().toString()) ){
                Toast.makeText(LoginActivity.this,"No. Telp kosong",Toast.LENGTH_LONG).show();
            }else if( TextUtils.isEmpty(user_alamat.getText().toString()) ){
                Toast.makeText(LoginActivity.this,"Alamat kosong",Toast.LENGTH_LONG).show();
            }else{
                new sendDataDaftar().execute();
            }

        });


    }



    private void signIn(String username, String password){
        formLogin.setVisibility(View.GONE);
        formLoginLoading.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.restapi + "/api/signin?u="+username+"&p="+password, response -> {
            Log.e("VOLLEY", response);
            try {

                final JSONObject req = new JSONObject(response);

                if( req.getBoolean("success") ) {

                    actSignIn.setEnabled(false);
                    editAkun.setEnabled(false);
                    editPassword.setEnabled(false);


                    JSONObject jsonObject = req.getJSONObject("response");

                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("password", password);
                    editor.putString("uid", username);
                    editor.putString("email", jsonObject.getString("email"));
                    editor.putString("nama", jsonObject.getString("nama"));
                    editor.putString("foto", jsonObject.getString("foto"));
                    editor.putString("jk", jsonObject.getString("jk"));
                    editor.putString("notelp", jsonObject.getString("notelp"));
                    editor.putString("alamat", jsonObject.getString("alamat"));
                    editor.putString("level", jsonObject.getString("level"));



                    editor.putString("pengiriman_penerima", jsonObject.getString("pengiriman_penerima"));
                    editor.putString("pengiriman_notelp", jsonObject.getString("pengiriman_notelp"));
                    editor.putString("pengiriman_jalan", jsonObject.getString("pengiriman_jalan"));
                    editor.putString("pengiriman_provinsi", jsonObject.getString("pengiriman_provinsi"));
                    editor.putString("pengiriman_kabupaten", jsonObject.getString("pengiriman_kabupaten"));
                    editor.putString("pengiriman_kecamatan", jsonObject.getString("pengiriman_kecamatan"));
                    editor.putString("pengiriman_desa", jsonObject.getString("pengiriman_desa"));
                    editor.putString("pengiriman_rt", jsonObject.getString("pengiriman_rt"));
                    editor.putString("pengiriman_rw", jsonObject.getString("pengiriman_rw"));
                    editor.putString("pengiriman_kodepos", jsonObject.getString("pengiriman_kodepos"));


                    editor.apply();

                    waitHandler.postDelayed(waitCallback, 3000);

                }else{

                    formLogin.setVisibility(View.VISIBLE);
                    formLoginLoading.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);


                    actSignIn.setEnabled(true);
                    editAkun.setEnabled(true);
                    editPassword.setEnabled(true);



                    /**
                    SharedPreferences.Editor editor = sharedpreferences.edit();

                    editor.remove("username");
                    editor.remove("password");

                    editor.remove("uid");
                    editor.remove("email");
                    editor.remove("nama");
                    editor.remove("foto");
                    editor.remove("jk");
                    editor.remove("notelp");
                    editor.remove("alamat");
                    editor.remove("level");


                    editor.apply();*/


                }
            } catch (Exception e) {

                formLogin.setVisibility(View.VISIBLE);
                formLoginLoading.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);


                actSignIn.setEnabled(true);
                editAkun.setEnabled(true);
                editPassword.setEnabled(true);
            }
        }, error -> {
            Log.e("VOLLEY", error.toString());
            formLogin.setVisibility(View.VISIBLE);
            formLoginLoading.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);

            actSignIn.setEnabled(true);
            editAkun.setEnabled(true);
            editPassword.setEnabled(true);
        });

        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );

        requestQueue.add(stringRequest);

    }


    private class sendDataDaftar extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {

                RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

                TextInputEditText user_email = findViewById(R.id.user_email);
                TextInputEditText user_password = findViewById(R.id.user_password);
                TextInputEditText user_nama = findViewById(R.id.user_nama);
                AutoCompleteTextView user_jk = findViewById(R.id.user_jk);
                TextInputEditText user_notelp = findViewById(R.id.user_notelp);
                TextInputEditText user_alamat = findViewById(R.id.user_alamat);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.restapi + "/api/signup", response -> {
                    Log.e("VOLLEY", response);
                    try {

                        final JSONObject req = new JSONObject(response);

                        if( req.getBoolean("success") ) {

                            user_email.setText("");
                            user_password.setText("");
                            user_nama.setText("");
                            user_jk.setText("",false);
                            user_notelp.setText("");
                            user_alamat.setText("");

                            Toast.makeText(LoginActivity.this,"Pendaftaran berhasil dikirim, silahkan login",Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Log.e("VOLLEY","Authentication error: " + e.getMessage());

                    }
                }, error -> {
                    Log.e("VOLLEY", error.toString());

                }) {
                    @Override
                    protected Map<String, String> getParams() {

                        Map<String, String> params = new HashMap<>();
                        params.put("user_email", String.valueOf(user_email.getText().toString()));
                        params.put("user_password", String.valueOf(user_password.getText().toString()));
                        params.put("user_nama", String.valueOf(user_nama.getText().toString()));
                        params.put("user_jk", String.valueOf(user_jk.getText().toString()));
                        params.put("user_notelp", String.valueOf(user_notelp.getText().toString()));
                        params.put("user_alamat", String.valueOf(user_alamat.getText().toString()));

                        return params;
                    }
                };




                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                requestQueue.add(stringRequest);



            }catch (Exception e){
                e.printStackTrace();
            }

            return true;
        }


        @Override
        protected void onPostExecute(Boolean result) {
        }

        @Override
        protected void onPreExecute() {
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();


    }
}
