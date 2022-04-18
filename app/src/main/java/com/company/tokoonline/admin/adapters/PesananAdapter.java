package com.company.tokoonline.admin.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.company.tokoonline.PesananDetailActivity;
import com.company.tokoonline.R;
import com.company.tokoonline.admin.AdminPesananDetail;
import com.company.tokoonline.config.Config;
import com.company.tokoonline.models.TransaksiItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PesananAdapter extends RecyclerView.Adapter<PesananAdapter.ViewHolder> {

    private Context activity;
    private List<TransaksiItem> transaskiItemList;


    public PesananAdapter(Context activity, List<TransaksiItem> transaskiItemList) {
        this.activity = activity;
        this.transaskiItemList = transaskiItemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_pesanan, viewGroup, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        TransaksiItem transaksiItem = transaskiItemList.get(position);

        viewHolder.harga.setText( Config.formatRupiah( transaksiItem.transaksi_harga_total) );
        viewHolder.beli.setText( "x"+transaksiItem.transaksi_jumlah );

        Picasso.with(activity).load( transaksiItem.barang_gambar ).into( viewHolder.imageView );
        viewHolder.txtview.setText( transaksiItem.barang_judul );



        if( transaksiItem.transaksi_status.equalsIgnoreCase("Pending") ){
            viewHolder.actionCek.setText("Bayar Sekarang");
        }else if( transaksiItem.transaksi_status.equalsIgnoreCase("Diperiksa") ){
            viewHolder.actionCek.setText("Periksa Pembayaran");
        }else if( transaksiItem.transaksi_status.equalsIgnoreCase("Bayar") ){
            viewHolder.actionCek.setText("Sudah Bayar");
        }else if( transaksiItem.transaksi_status.equalsIgnoreCase("Dikirim") ){
            viewHolder.actionCek.setText("Dikirim");
        }else if( transaksiItem.transaksi_status.equalsIgnoreCase("Diterima") ){
            viewHolder.actionCek.setText("Diterima");
        }else if( transaksiItem.transaksi_status.equalsIgnoreCase("Dibatalkan") ){
            viewHolder.actionCek.setText("Dibatalkan");
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(v.getContext(), AdminPesananDetail.class);
                myIntent.putExtra("key", transaksiItem.transaksi_id); //Optional parameters
                v.getContext().startActivity(myIntent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return transaskiItemList.size();
    }

    /**
     * View holder to display each RecylerView item
     */
    protected class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout linearLayout;
        private ImageView imageView;
        private TextView txtview;
        private TextView harga;
        private TextView beli;
        private TextView actionCek;


        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageview);
            txtview = view.findViewById(R.id.txtview);
            linearLayout = view.findViewById(R.id.layout);

            harga = view.findViewById(R.id.harga);
            beli = view.findViewById(R.id.beli);
            actionCek = view.findViewById(R.id.actionCek);
        }
    }
}
