package com.company.tokoonline;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.company.tokoonline.adapters.RekomendasiAdapter;
import com.company.tokoonline.config.AppController;
import com.company.tokoonline.config.Config;
import com.company.tokoonline.models.BarangItem;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PencarianActivity extends AppCompatActivity {


    private String key;

    private RecyclerView recyleviewRekomendasi;
    private RekomendasiAdapter rekomendasiAdapter;

    private Context context;
    private SharedPreferences sharedpreferences;
    private SwipeRefreshLayout swipeRefresh;

    private TextInputEditText tf_query;

    long delay = 1500; // 1 seconds after user stops typing
    long last_text_edit = 0;
    Handler handler = new Handler();




    private Runnable input_finish_checker = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + delay - 500) ) {
                // TODO: do what you need here
                // ............
                // ............
                Log.d("TAG", "stopped typing");

                new getDataPencarian().execute();


            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pencarian);


        context = PencarianActivity.this;
        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);

        tf_query = findViewById(R.id.tf_query);
        recyleviewRekomendasi = findViewById(R.id.recycle_view0);

        findViewById(R.id.actionBack).setOnClickListener(v -> {

            onBackPressed();

        });


        tf_query.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged (CharSequence s,int start, int count,
                                           int after){
            }
            @Override
            public void onTextChanged ( final CharSequence s, int start, int before,
                                        int count){
                //You need to remove this to run only once
                handler.removeCallbacks(input_finish_checker);

            }
            @Override
            public void afterTextChanged ( final Editable s){
                //avoid triggering event when text is empty
                if (s.length() > 0) {

                    last_text_edit = System.currentTimeMillis();
                    handler.postDelayed(input_finish_checker, delay);


                } else {

                }
            }
        });


    }





    private class getDataPencarian extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {

                RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

                String uid = sharedpreferences.getString("uid", "");
                key = tf_query.getText().toString();

                StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.restapi + "/api/produk_bycari?uid="+uid+"&key="+key, response -> {
                    Log.e("VOLLEY", response);
                    try {

                        final JSONObject req = new JSONObject(response);

                        if( req.getBoolean("success") ) {




                            /**
                             * SET DATA REKOMENDASI
                             */
                            final List<BarangItem> barangRekomendasiItemList = new ArrayList();
                            JSONArray barangRekomendasiItemArray = req.getJSONArray("response");
                            for(int a=0; a<barangRekomendasiItemArray.length() ;a++){
                                JSONObject rekomendasi = barangRekomendasiItemArray.getJSONObject(a);
                                barangRekomendasiItemList.add(new BarangItem(
                                        rekomendasi.getInt("barang_id"),
                                        rekomendasi.getString("barang_judul"),
                                        rekomendasi.getString("barang_katerangan"),
                                        rekomendasi.getString("barang_kategori"),
                                        rekomendasi.getString("barang_variasi"),
                                        rekomendasi.getString("barang_ukuran"),
                                        rekomendasi.getInt("barang_berat"),
                                        rekomendasi.getInt("barang_stok"),
                                        rekomendasi.getInt("barang_harga"),
                                        rekomendasi.getInt("barang_diskon"),
                                        rekomendasi.getInt("barang_terjual"),
                                        rekomendasi.getString("barang_gambar"),
                                        rekomendasi.getString("barang_tanggal"),
                                        rekomendasi.getString("barang_tanggal_diubah")
                                ));
                            }
                            recyleviewRekomendasi.setLayoutManager( new GridLayoutManager(context, 2) );
                            rekomendasiAdapter = new RekomendasiAdapter(context, barangRekomendasiItemList);
                            recyleviewRekomendasi.setAdapter(rekomendasiAdapter);





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