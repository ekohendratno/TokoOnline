package com.company.tokoonline.admin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.company.tokoonline.R;
import com.company.tokoonline.config.Config;
import com.company.tokoonline.models.BarangItem;
import com.company.tokoonline.models.KategoriItem;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import java.util.List;

public class KategoriAdapter extends RecyclerView.Adapter<KategoriAdapter.ViewHolder> {


    private Context activity;
    private BottomSheetDialog bottomSheetDialog;


    List<KategoriItem> barangRekomendasiItemList;

    public interface OnItemCheckListener {
        void onFotoItemClick(KategoriItem item);
        void onUpdateItemClick(KategoriItem item);
        void onRemoveItemClick(KategoriItem item);
    }

    @NonNull
    private OnItemCheckListener onItemClick;

    public KategoriAdapter(Context activity, BottomSheetDialog bottomSheetDialog, List<KategoriItem> barangRekomendasiItemList, @NonNull OnItemCheckListener onItemCheckListener) {
        this.activity = activity;
        this.bottomSheetDialog = bottomSheetDialog;
        this.barangRekomendasiItemList = barangRekomendasiItemList;
        this.onItemClick = onItemCheckListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.admin_item_kategori, viewGroup, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(KategoriAdapter.ViewHolder viewHolder, int position) {
        KategoriItem kategoriItem = barangRekomendasiItemList.get(position);

        Picasso.with(activity).load( kategoriItem.kategori_gambar ).into( viewHolder.imageView );
        viewHolder.txtview.setText( kategoriItem.kategori_judul );
        viewHolder.status.setText( kategoriItem.kategori_status );

        viewHolder.actionHapus.setOnClickListener(v->{

            onItemClick.onRemoveItemClick(kategoriItem);

        });

        viewHolder.itemView.setOnClickListener(v -> {

            bottomSheetDialog.show();

            TextInputEditText judul = bottomSheetDialog.findViewById(R.id.judul);
            AutoCompleteTextView status = bottomSheetDialog.findViewById(R.id.status);
            ImageView gambar_preview = bottomSheetDialog.findViewById(R.id.gambar_preview);

            judul.setText( kategoriItem.kategori_judul );
            status.setText( kategoriItem.kategori_status, false );
            Picasso.with(activity).load( kategoriItem.kategori_gambar ).into( gambar_preview );


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

        private ImageView imageView;
        private TextView txtview;
        private TextView status;

        private ImageView actionHapus;


        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageview);
            txtview = view.findViewById(R.id.txtview);
            status = view.findViewById(R.id.status);

            actionHapus = view.findViewById(R.id.actionHapus);
        }
    }


}
