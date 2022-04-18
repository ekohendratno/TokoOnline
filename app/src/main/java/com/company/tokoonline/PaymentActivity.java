package com.company.tokoonline;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.company.tokoonline.adapters.KeranjangAdapter;
import com.company.tokoonline.adapters.PaymentAdapter;
import com.company.tokoonline.config.AppController;
import com.company.tokoonline.config.Config;
import com.company.tokoonline.models.BankItem;
import com.company.tokoonline.models.KeranjangItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {

    private int key;

    private Context context;
    private SharedPreferences sharedpreferences;


    TextView tv_nota;
    TextView tv_harga_total_pembayaran;


    private static RecyclerView recyclerView;
    private static PaymentAdapter paymentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);




        context = PaymentActivity.this;

        Intent intent = getIntent();
        key = intent.getIntExtra("key",0);
        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);


        tv_nota = findViewById(R.id.nota);
        tv_harga_total_pembayaran = findViewById(R.id.harga_total_pembayaran);

        recyclerView = findViewById(R.id.recycle_view0);
        recyclerView.setLayoutManager( new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) );

        findViewById(R.id.actionBack).setOnClickListener(v -> {

            Intent myIntent = new Intent(v.getContext(), MainActivity.class);
            startActivity(myIntent);
            finish();

        });


        findViewById(R.id.actionOk).setOnClickListener(v -> {

            Intent myIntent = new Intent(v.getContext(), PesananActivity.class);
            startActivity(myIntent);
            finish();

        });




        findViewById(R.id.actionKonfirmasi).setOnClickListener(v -> {

            Intent myIntent = new Intent(v.getContext(), KonfirmasiActivity.class);
            myIntent.putExtra("key", key);
            startActivity(myIntent);
            finish();

        });


        new getDataBank().execute();
        new getDataPaymentDetail().execute();


    }


    private class getDataBank extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {

                RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

                String uid = sharedpreferences.getString("uid", "");

                StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.restapi + "/api/bank?uid="+uid, response -> {
                    Log.e("VOLLEY", response);
                    try {

                        final JSONObject req = new JSONObject(response);

                        if( req.getBoolean("success") ) {

                            final List<BankItem> bankItemList = new ArrayList();
                            JSONArray bankItemArray= req.getJSONArray("response");
                            for(int a=0; a<bankItemArray.length() ;a++){
                                JSONObject keranjang = bankItemArray.getJSONObject(a);
                                bankItemList.add(new BankItem(
                                        keranjang.getInt("bank_id"),
                                        keranjang.getString("bank_nama"),
                                        keranjang.getString("bank_atas_nama"),
                                        keranjang.getString("bank_norek")
                                ));
                            }


                            paymentAdapter = new PaymentAdapter(context, bankItemList);
                            recyclerView.setAdapter(paymentAdapter);

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


    private class getDataPaymentDetail extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {

                RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

                String uid = sharedpreferences.getString("uid", "");

                StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.restapi + "/api/pesanan_detail?uid="+uid+"&key="+key, response -> {
                    Log.e("VOLLEY", response);
                    try {

                        final JSONObject req = new JSONObject(response);

                        if( req.getBoolean("success") ) {
                            JSONObject jsonObject = req.getJSONObject("response");

                            tv_nota.setText("#"+jsonObject.getString("transaksi_nota") );
                            tv_harga_total_pembayaran.setText(  Config.formatRupiah( jsonObject.getInt("transaksi_harga_total") ) );

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