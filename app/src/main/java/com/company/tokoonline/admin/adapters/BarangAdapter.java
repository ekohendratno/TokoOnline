package com.company.tokoonline.admin.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.company.tokoonline.DetailActivity;
import com.company.tokoonline.R;
import com.company.tokoonline.Splash;
import com.company.tokoonline.adapters.KeranjangAdapter;
import com.company.tokoonline.admin.AdminBarang;
import com.company.tokoonline.config.AppController;
import com.company.tokoonline.config.Config;
import com.company.tokoonline.models.BarangItem;
import com.company.tokoonline.models.KeranjangItem;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;

public class BarangAdapter extends RecyclerView.Adapter<BarangAdapter.ViewHolder> {


    private Context activity;
    private BottomSheetDialog bottomSheetDialog;


    List<BarangItem> barangRekomendasiItemList;

    public interface OnItemCheckListener {
        void onFotoItemClick(BarangItem item);
        void onUpdateItemClick(BarangItem item);
        void onRemoveItemClick(BarangItem item);
    }

    @NonNull
    private OnItemCheckListener onItemClick;

    public BarangAdapter(Context activity, BottomSheetDialog bottomSheetDialog, List<BarangItem> barangRekomendasiItemList, @NonNull OnItemCheckListener onItemCheckListener) {
        this.activity = activity;
        this.bottomSheetDialog = bottomSheetDialog;
        this.barangRekomendasiItemList = barangRekomendasiItemList;
        this.onItemClick = onItemCheckListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.admin_item_barang, viewGroup, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(BarangAdapter.ViewHolder viewHolder, int position) {
        BarangItem kategoriItem = barangRekomendasiItemList.get(position);

        viewHolder.ket.setText( "Dis "+kategoriItem.barang_diskon +"% , Jual " + kategoriItem.barang_terjual + "x" );
        viewHolder.harga.setText( Config.formatRupiah( kategoriItem.barang_harga ) );

        Picasso.with(activity).load( kategoriItem.barang_gambar ).into( viewHolder.imageView );
        viewHolder.txtview.setText( kategoriItem.barang_judul );

        viewHolder.actionHapus.setOnClickListener(v->{

            onItemClick.onRemoveItemClick(kategoriItem);

        });

        viewHolder.itemView.setOnClickListener(v -> {

            bottomSheetDialog.show();

            TextInputEditText barang_nama = bottomSheetDialog.findViewById(R.id.barang_nama);
            TextInputEditText keterangan = bottomSheetDialog.findViewById(R.id.keterangan);
            ImageView gambar_preview = bottomSheetDialog.findViewById(R.id.gambar_preview);
            AutoCompleteTextView status = bottomSheetDialog.findViewById(R.id.status);
            AutoCompleteTextView kategori = bottomSheetDialog.findViewById(R.id.kategori);
            TextInputEditText variasi = bottomSheetDialog.findViewById(R.id.variasi);
            TextInputEditText ukuran = bottomSheetDialog.findViewById(R.id.ukuran);
            TextInputEditText berat = bottomSheetDialog.findViewById(R.id.berat);
            TextInputEditText stok = bottomSheetDialog.findViewById(R.id.stok);
            TextInputEditText harga = bottomSheetDialog.findViewById(R.id.harga);
            TextInputEditText diskon = bottomSheetDialog.findViewById(R.id.diskon);

            barang_nama.setText( kategoriItem.barang_judul );
            keterangan.setText( kategoriItem.barang_katerangan );
            status.setText( kategoriItem.barang_status,false );
            kategori.setText( kategoriItem.barang_kategori,false );
            variasi.setText( kategoriItem.barang_variasi );
            ukuran.setText( kategoriItem.barang_ukuran );
            berat.setText( String.valueOf(kategoriItem.barang_berat) );
            stok.setText( String.valueOf(kategoriItem.barang_stok) );
            harga.setText( String.valueOf(kategoriItem.barang_harga) );
            diskon.setText( String.valueOf(kategoriItem.barang_diskon) );

            Picasso.with(activity).load( kategoriItem.barang_gambar ).into( gambar_preview );


            bottomSheetDialog.findViewById(R.id.actionGambar).setOnClickListener(v1 -> {
                onItemClick.onFotoItemClick(kategoriItem);
            });


            bottomSheetDialog.findViewById(R.id.actionSimpan).setOnClickListener(v1->{
                onItemClick.onUpdateItemClick(kategoriItem);
            });

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
        private TextView ket;
        private TextView harga;

        private ImageView actionHapus;


        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageview);
            txtview = view.findViewById(R.id.txtview);
            linearLayout = view.findViewById(R.id.layout);
            ket = view.findViewById(R.id.ket);
            harga = view.findViewById(R.id.harga);

            actionHapus = view.findViewById(R.id.actionHapus);
        }
    }


}
