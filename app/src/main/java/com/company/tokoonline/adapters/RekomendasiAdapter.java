package com.company.tokoonline.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.company.tokoonline.DetailActivity;
import com.company.tokoonline.R;
import com.company.tokoonline.config.Config;
import com.company.tokoonline.models.BarangItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RekomendasiAdapter extends RecyclerView.Adapter<RekomendasiAdapter.ViewHolder> {


    private Context activity;
    List<BarangItem> barangRekomendasiItemList;

    public RekomendasiAdapter(Context activity, List<BarangItem> barangRekomendasiItemList) {
        this.activity = activity;
        this.barangRekomendasiItemList = barangRekomendasiItemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_rekomendasi, viewGroup, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RekomendasiAdapter.ViewHolder viewHolder, int position) {
        BarangItem kategoriItem = barangRekomendasiItemList.get(position);

        viewHolder.diskon.setText( kategoriItem.barang_diskon +"%" );
        viewHolder.diskon_lyt.setVisibility(View.GONE);
        if( kategoriItem.barang_diskon > 0){
            viewHolder.diskon_lyt.setVisibility(View.VISIBLE);
        }

        viewHolder.harga.setText( Config.formatRupiah( kategoriItem.barang_harga ) );
        viewHolder.terjual.setText( kategoriItem.barang_terjual + " terjual" );

        Picasso.with(activity).load( kategoriItem.barang_gambar ).into( viewHolder.imageView );
        viewHolder.txtview.setText( kategoriItem.barang_judul );


        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(v.getContext(), DetailActivity.class);
                myIntent.putExtra("key", kategoriItem.id); //Optional parameters
                v.getContext().startActivity(myIntent);

                //Toast.makeText(activity, "Position clicked: " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return barangRekomendasiItemList.size();
    }

    /**
     * View holder to display each RecylerView item
     */
    protected class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout linearLayout;
        private ImageView imageView;
        private TextView txtview;
        private LinearLayout diskon_lyt;
        private TextView diskon;
        private TextView harga;
        private TextView terjual;


        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageview);
            txtview = view.findViewById(R.id.txtview);
            linearLayout = view.findViewById(R.id.layout);
            diskon_lyt = view.findViewById(R.id.diskon_lyt);
            diskon = view.findViewById(R.id.tv_diskon);
            harga = view.findViewById(R.id.harga);
            terjual = view.findViewById(R.id.terjual);
        }
    }
}
