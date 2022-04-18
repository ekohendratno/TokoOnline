package com.company.tokoonline.admin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.company.tokoonline.LoginActivity;
import com.company.tokoonline.R;
import com.company.tokoonline.Splash;
import com.company.tokoonline.admin.adapters.BankAdapter;
import com.company.tokoonline.config.AppController;
import com.company.tokoonline.config.Config;
import com.company.tokoonline.models.BankItem;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class AdminBank extends AppCompatActivity {

    private Context context;
    private SharedPreferences sharedpreferences;
    private SwipeRefreshLayout swipeRefresh;

    private RecyclerView recyclerView;
    private BankAdapter bankAdapter;
    private BottomSheetDialog bottomSheetDialog;

    ProgressDialog dialog;
    private TextInputEditText bank_nama;
    private TextInputEditText bank_atas_nama;
    private TextInputEditText bank_norek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_bank);


        context = AdminBank.this;
        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);

        String uid = sharedpreferences.getString("uid", "");

        dialog = new ProgressDialog(context);
        dialog.setMessage("Loading. Please wait...");
        dialog.setCancelable(false);


        if( TextUtils.isEmpty(uid) ){

            startActivity(new Intent(context, LoginActivity.class));
            finish();

        }

        recyclerView = findViewById(R.id.recycle_view0);
        recyclerView.setLayoutManager( new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) );


        bottomSheetDialog = new BottomSheetDialog( context );
        bottomSheetDialog.setContentView(R.layout.admin_bottomsheet_bank);

        bottomSheetDialog.findViewById(R.id.actionClose).setOnClickListener(v1->{
            bottomSheetDialog.dismiss();
        });

        bank_nama = bottomSheetDialog.findViewById(R.id.bank_nama);
        bank_atas_nama = bottomSheetDialog.findViewById(R.id.bank_atas_nama);
        bank_norek = bottomSheetDialog.findViewById(R.id.bank_norek);

        findViewById(R.id.actionBack).setOnClickListener(v -> {

            onBackPressed();

        });


        findViewById(R.id.actionAdd).setOnClickListener(v->{

            bottomSheetDialog.show();

            bank_nama.setText("");
            bank_atas_nama.setText("");
            bank_norek.setText("");

            bottomSheetDialog.findViewById(R.id.actionSimpan).setOnClickListener(v1->{

                new simpanDataPost(0).execute();

            });

        });



        new getDataBank().execute();

        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(() -> {
            new getDataBank().execute();

            swipeRefresh.setRefreshing(false);
        });
    }


    private class getDataBank extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {

                RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

                String uid = sharedpreferences.getString("uid", "");

                StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.restapi + "/api/admin_bank?uid="+uid, response -> {
                    Log.e("VOLLEY", response);
                    dialog.dismiss();
                    try {

                        final JSONObject req = new JSONObject(response);

                        if( req.getBoolean("success") ) {

                            ArrayList<BankItem> bankList = new ArrayList<>();

                            JSONArray bankRekomendasiItemArray = req.getJSONArray("response");
                            for(int a=0; a<bankRekomendasiItemArray.length() ;a++){
                                JSONObject rekomendasi = bankRekomendasiItemArray.getJSONObject(a);
                                bankList.add( new BankItem(
                                        rekomendasi.getInt("bank_id"),
                                        rekomendasi.getString("bank_nama"),
                                        rekomendasi.getString("bank_atas_nama"),
                                        rekomendasi.getString("bank_norek")
                                ) );
                            }


                            bankAdapter = new BankAdapter(context, bottomSheetDialog, bankList, new BankAdapter.OnItemCheckListener() {
                                @Override
                                public void onUpdateItemClick(BankItem item) {

                                    new simpanDataPost(item.bank_id).execute();
                                }

                                @Override
                                public void onRemoveItemClick(BankItem item) {

                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                                    alertDialogBuilder.setTitle("Konfirmasi");
                                    alertDialogBuilder
                                            .setMessage("Yakin ingin menghapus produk?")
                                            .setPositiveButton("Ya", (dialog, id) -> {


                                                new removeData(item.bank_id).execute();


                                            })
                                            .setNegativeButton("Tidak", (dialog, id) -> dialog.cancel());

                                    // membuat alert dialog dari builder
                                    AlertDialog alertDialog = alertDialogBuilder.create();

                                    // menampilkan alert dialog
                                    alertDialog.show();

                                }
                            });
                            recyclerView.setAdapter(bankAdapter);

                        }else{
                        }
                    } catch (Exception e) {
                        Log.e("VOLLEY","Authentication error: " + e.getMessage());

                    }
                }, error -> {
                    Log.e("VOLLEY", error.toString());

                });



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
            dialog.show();
        }
    }

    private class simpanDataPost extends AsyncTask<String, Void, Boolean> {

        int key;
        public simpanDataPost(int key) {
            this.key = key;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                MultipartBody.Builder multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM);

                Date currentTime = Calendar.getInstance().getTime();
                String uid = sharedpreferences.getString("uid", "");

                multipartBody.addFormDataPart("bank_nama", bank_nama.getText().toString());
                multipartBody.addFormDataPart("bank_atas_nama", bank_atas_nama.getText().toString());
                multipartBody.addFormDataPart("bank_norek", bank_norek.getText().toString());

                RequestBody requestBody = multipartBody.build();

                String url = Config.restapi +"/api/admin_bank_simpan?uid="+uid+"&key="+key;
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();

                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                builder.connectTimeout(5, TimeUnit.MINUTES) // connect timeout
                        .writeTimeout(5, TimeUnit.MINUTES) // write timeout
                        .readTimeout(5, TimeUnit.MINUTES); // read timeout

                OkHttpClient okHttpClient = builder.build();
                okhttp3.Response response = okHttpClient.newCall(request).execute();
                String res = response.body().string();

                Log.e("res",res);

                JSONObject j1 = new JSONObject( res );
                if(j1.getBoolean("success")){
                    dialog.dismiss();

                    return true;

                }
            } catch (Exception e) {
                e.printStackTrace();
                dialog.dismiss();
            }

            return false;
        }


        @Override
        protected void onPostExecute(Boolean result) {

            new getDataBank().execute();

        }

        @Override
        protected void onPreExecute() {

            dialog.show();

        }
    }

    private class removeData extends AsyncTask<String, Void, Boolean> {

        int key;
        public removeData(int key) {
            this.key = key;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {

                RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

                String uid = sharedpreferences.getString("uid", "");

                StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.restapi + "/api/admin_bank_hapus?uid="+uid+"&key="+key, response -> {
                    Log.e("VOLLEY", response);
                    try {

                        final JSONObject req = new JSONObject(response);

                        if( req.getBoolean("success") ) {

                            new getDataBank().execute();

                        }else{

                        }

                    } catch (Exception e) {
                        Log.e("VOLLEY","Authentication error: " + e.getMessage());

                    }
                }, error -> {
                    Log.e("VOLLEY", error.toString());

                });



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