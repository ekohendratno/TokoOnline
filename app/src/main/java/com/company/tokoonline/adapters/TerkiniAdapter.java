package com.company.tokoonline.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.company.tokoonline.models.BarangItem;
import com.company.tokoonline.models.KategoriItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TerkiniAdapter extends RecyclerView.Adapter<TerkiniAdapter.ViewHolder> {

    private Context activity;
    List<BarangItem> barangItemList;


    public TerkiniAdapter(Context activity, List<BarangItem> barangItemList) {
        this.activity = activity;
        this.barangItemList = barangItemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_terkini, viewGroup, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(TerkiniAdapter.ViewHolder viewHolder, int position) {

        BarangItem kategoriItem = barangItemList.get(position);

        Picasso.with(activity).load( kategoriItem.barang_gambar ).into( viewHolder.imageView );
        viewHolder.txtview.setText( kategoriItem.barang_judul );

        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(v.getContext(), DetailActivity.class);
                myIntent.putExtra("key", kategoriItem.id);
                v.getContext().startActivity(myIntent);

                //Toast.makeText(activity, "Position clicked: " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return barangItemList.size();
    }

    /**
     * View holder to display each RecylerView item
     */
    protected class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout linearLayout;
        private ImageView imageView;
        private TextView txtview;


        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageview);
            txtview = view.findViewById(R.id.txtview);
            linearLayout = view.findViewById(R.id.layout);
        }
    }
}
