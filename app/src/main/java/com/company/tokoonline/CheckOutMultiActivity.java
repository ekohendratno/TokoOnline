package com.company.tokoonline;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.company.tokoonline.adapters.CheckoutAdapter;
import com.company.tokoonline.config.AppController;
import com.company.tokoonline.config.Config;
import com.company.tokoonline.models.KeranjangItem;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CheckOutMultiActivity extends AppCompatActivity {

    private ArrayList<KeranjangItem> keranjang;


    private Context context;
    private SharedPreferences sharedpreferences;

    private RecyclerView recyclerView;
    private CheckoutAdapter checkoutAdapter;


    TextView tv_ongkir;
    TextView tv_harga_subtotal_produk;
    TextView tv_harga_subtotal_pengiriman;
    TextView tv_harga_total_pembayaran;
    TextView tv_harga_total_pembayaran2;
    EditText catatan;

    private String keys = "";

    private SweetAlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_multi);

        context = CheckOutMultiActivity.this;

        Bundle bundles = getIntent().getExtras();
        keranjang = bundles.getParcelableArrayList("keranjang");
        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);

        dialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitleText( "Loading. Please wait..." );

        recyclerView = findViewById(R.id.recycle_view0);
        recyclerView.setLayoutManager( new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) );


        tv_ongkir = findViewById(R.id.ongkir);
        tv_harga_subtotal_produk = findViewById(R.id.harga_subtotal_produk);
        tv_harga_subtotal_pengiriman = findViewById(R.id.harga_subtotal_pengiriman);
        tv_harga_total_pembayaran = findViewById(R.id.harga_total_pembayaran);
        tv_harga_total_pembayaran2 = findViewById(R.id.harga_total_pembayaran2);
        catatan = findViewById(R.id.catatan);

        findViewById(R.id.actionBack).setOnClickListener(v -> {

            onBackPressed();

        });

        findViewById(R.id.actionCheckout).setOnClickListener(v -> {

            sendDataCheckout();

        });

        getDataOngkir();


        checkoutAdapter = new CheckoutAdapter(context, keranjang);
        recyclerView.setAdapter(checkoutAdapter);

    }



    private void getDataOngkir(){
        dialog.show();

        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

        String uid = sharedpreferences.getString("uid", "");

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.restapi + "/api/ongkir?uid="+uid, response -> {
            Log.e("VOLLEY", response);
            dialog.dismiss();
            try {

                final JSONObject req = new JSONObject(response);

                if( req.getBoolean("success") ) {

                    int ongkir = 0;
                    int total = 0;
                    int total_ongkir = 0;

                    ongkir = req.getInt("response");

                    for(int a=0; a<keranjang.size() ;a++){
                        KeranjangItem keranjangItem = keranjang.get(a);

                        Log.e("a", keranjangItem.toString());

                        int harga = keranjangItem.barang_harga;
                        int jumlah = keranjangItem.keranjang_jumlah;

                        total = total + ( harga*jumlah );

                        keys += keranjangItem.keranjang_id;

                        if( a != keranjang.size()-1){
                            keys += ",";
                        }
                    }

                    total_ongkir = ( total+ongkir );

                    tv_ongkir.setText( Config.formatRupiah( ongkir ) );
                    tv_harga_subtotal_produk.setText( Config.formatRupiah( total ) );
                    tv_harga_subtotal_pengiriman.setText( Config.formatRupiah( total_ongkir ) );
                    tv_harga_total_pembayaran.setText( Config.formatRupiah( total_ongkir ) );
                    tv_harga_total_pembayaran2.setText( Config.formatRupiah( total_ongkir ) );



                }else{
                }


            } catch (Exception e) {
                Log.e("VOLLEY","Authentication error: " + e.getMessage());

            }
        }, error -> {
            dialog.dismiss();

            new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Terjadi gangguan!")
                    .setContentText("Mengalami gangguan ketika mengambil data!")
                    .show();
            Log.e("VOLLEY", error.toString());

        });



        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);

    }

    private void sendDataCheckout() {
        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

        String uid = sharedpreferences.getString("uid", "");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.restapi + "/api/checkout_multi?uid="+uid, response -> {
            Log.e("VOLLEY", response);
            dialog.dismiss();
            try {

                final JSONObject req = new JSONObject(response);

                if( req.getBoolean("success") ) {

                    Intent intent = new Intent(context, PaymentMultiActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putString("unik", req.getString("unik") );
                    bundle.putParcelableArrayList("keranjang", (ArrayList<? extends Parcelable>) keranjang);
                    intent.putExtras(bundle);

                    startActivity(intent);

                    finish();

                }

            } catch (Exception e) {
                Log.e("VOLLEY","Authentication error: " + e.getMessage());

            }
        }, error -> {
            dialog.dismiss();

            new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Terjadi gangguan!")
                    .setContentText("Mengalami gangguan ketika mengambil data!")
                    .show();
            Log.e("VOLLEY", error.toString());

        }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("keys", String.valueOf( keys ));
                params.put("catatan", String.valueOf( catatan.getText().toString() ));

                return params;
            }
        };




        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}