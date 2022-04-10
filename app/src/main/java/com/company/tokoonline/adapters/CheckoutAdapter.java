package com.company.tokoonline.adapters;

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
import com.company.tokoonline.config.Config;
import com.company.tokoonline.models.KeranjangItem;
import com.company.tokoonline.models.TransaksiItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.ViewHolder> {

    private Context activity;
    private List<KeranjangItem> checkoutItemList;


    public CheckoutAdapter(Context activity, List<KeranjangItem> checkoutItemList) {
        this.activity = activity;
        this.checkoutItemList = checkoutItemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_checkout_multi, viewGroup, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(CheckoutAdapter.ViewHolder viewHolder, int position) {
        KeranjangItem checkoutItem = checkoutItemList.get(position);

        int harga = checkoutItem.barang_harga;
        int jumlah = checkoutItem.keranjang_jumlah;

        harga = harga*jumlah;

        viewHolder.harga.setText( Config.formatRupiah( harga ) );
        viewHolder.beli.setText( "x"+jumlah );

        Picasso.with(activity).load( checkoutItem.barang_gambar ).into( viewHolder.imageView );
        viewHolder.txtview.setText( checkoutItem.barang_judul );

    }

    @Override
    public int getItemCount() {
        return checkoutItemList.size();
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
