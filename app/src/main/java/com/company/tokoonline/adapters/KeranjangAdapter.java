package com.company.tokoonline.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.company.tokoonline.CartActivity;
import com.company.tokoonline.DetailActivity;
import com.company.tokoonline.R;
import com.company.tokoonline.Splash;
import com.company.tokoonline.config.AppController;
import com.company.tokoonline.config.Config;
import com.company.tokoonline.models.KeranjangItem;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;

public class KeranjangAdapter extends RecyclerView.Adapter<KeranjangAdapter.ViewHolder> {

    private Context activity;
    private List<KeranjangItem> keranjangItemList;

    public interface OnItemCheckListener {
        void onItemCheck(KeranjangItem item, int beli);
        void onItemUncheck(KeranjangItem item, int beli);
        void onItemJumlah(KeranjangItem item, int beli);
    }

    @NonNull
    private OnItemCheckListener onItemClick;

    public KeranjangAdapter(Context activity, List<KeranjangItem> keranjangItemList, @NonNull OnItemCheckListener onItemCheckListener) {
        this.activity = activity;
        this.keranjangItemList = keranjangItemList;
        this.onItemClick = onItemCheckListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_cart, viewGroup, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(KeranjangAdapter.ViewHolder viewHolder, int position) {
        KeranjangItem keranjangItem = keranjangItemList.get(position);



        viewHolder.id.setOnClickListener(v -> {
            int jumlah = Integer.parseInt( viewHolder.edit_jumlah_beli.getText().toString() );

            if (viewHolder.id.isChecked()) {
                onItemClick.onItemCheck(keranjangItem, jumlah);
            } else {
                onItemClick.onItemUncheck(keranjangItem, jumlah);
            }


        });

        viewHolder.harga.setText( Config.formatRupiah( keranjangItem.barang_harga) );

        Picasso.with(activity).load( keranjangItem.barang_gambar ).into( viewHolder.imageView );
        viewHolder.txtview.setText( keranjangItem.barang_judul );


        viewHolder.itemView.setOnClickListener(v -> {

            Intent myIntent = new Intent(v.getContext(), DetailActivity.class);
            myIntent.putExtra("key", keranjangItem.barang_id);
            v.getContext().startActivity(myIntent);

        });


        viewHolder.edit_jumlah_beli.setEnabled(false);



        int s = keranjangItem.barang_stok;

        if( s >= 1 ){
            viewHolder.edit_jumlah_beli.setText( String.valueOf(keranjangItem.keranjang_jumlah) );

            viewHolder.action_jumlah_beli_min.setEnabled(true);
            viewHolder.action_jumlah_beli_plus.setEnabled(true);
        }else{
            viewHolder.edit_jumlah_beli.setText( "0" );

            viewHolder.action_jumlah_beli_min.setEnabled(false);
            viewHolder.action_jumlah_beli_plus.setEnabled(false);
        }


        viewHolder.action_jumlah_beli_min.setOnClickListener(v->{
            int beli_minus = Integer.parseInt( viewHolder.edit_jumlah_beli.getText().toString() );
            int beli_minus_setelah = beli_minus-1;

            if( beli_minus_setelah > 0){
                viewHolder.edit_jumlah_beli.setText(String.valueOf( beli_minus_setelah ));


                onItemClick.onItemJumlah(keranjangItem, beli_minus_setelah);


                new setDataKeranjang(v.getContext(), keranjangItem.keranjang_id, beli_minus_setelah).execute();
            }


        });
        viewHolder.action_jumlah_beli_plus.setOnClickListener(v->{
            int beli_plus = Integer.parseInt( viewHolder.edit_jumlah_beli.getText().toString() );
            int beli_plus_setelah = beli_plus+1;


            if( beli_plus_setelah > 0 && beli_plus_setelah <= s){

                viewHolder.edit_jumlah_beli.setText(String.valueOf( beli_plus_setelah ));


                onItemClick.onItemJumlah(keranjangItem, beli_plus_setelah);

                new setDataKeranjang(v.getContext(), keranjangItem.keranjang_id, beli_plus_setelah).execute();


            }

        });
    }

    @Override
    public int getItemCount() {
        return keranjangItemList.size();
    }

    /**
     * View holder to display each RecylerView item
     */
    protected class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout linearLayout;
        private ImageView imageView;
        private TextView txtview;
        private TextView harga;
        private AppCompatImageView action_jumlah_beli_plus;
        private AppCompatImageView action_jumlah_beli_min;
        private AppCompatEditText edit_jumlah_beli;
        private CheckBox id;


        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageview);
            txtview = view.findViewById(R.id.txtview);
            linearLayout = view.findViewById(R.id.layout);

            id = view.findViewById(R.id.id);
            id.setClickable(true);

            harga = view.findViewById(R.id.harga);
            action_jumlah_beli_plus = view.findViewById(R.id.action_jumlah_beli_plus);
            action_jumlah_beli_min = view.findViewById(R.id.action_jumlah_beli_min);
            edit_jumlah_beli = view.findViewById(R.id.edit_jumlah_beli);
        }
    }




    private class setDataKeranjang extends AsyncTask<String, Void, Boolean> {

        private Context context;
        private SharedPreferences sharedpreferences;

        private int key;
        private int jumlah;

        public setDataKeranjang(Context context, int key, int jumlah) {
            this.context = context;
            this.key = key;
            this.jumlah = jumlah;
        }


        @Override
        protected Boolean doInBackground(String... params) {
            try {

                RequestQueue requestQueue = AppController.getInstance().getRequestQueue();
                sharedpreferences = context.getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);

                String uid = sharedpreferences.getString("uid", "");
                StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.restapi + "/api/keranjang?uid="+uid+"&key="+key+"&jumlah="+jumlah, response -> {
                    Log.e("VOLLEY", response);
                    try {

                        final JSONObject req = new JSONObject(response);

                        if( req.getBoolean("success") ) {
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
}
