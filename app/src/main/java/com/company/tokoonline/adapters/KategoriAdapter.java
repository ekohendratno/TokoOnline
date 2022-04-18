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
import com.company.tokoonline.KategoriActivity;
import com.company.tokoonline.R;
import com.company.tokoonline.models.KategoriItem;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

public class KategoriAdapter extends RecyclerView.Adapter<KategoriAdapter.ViewHolder> {

    private Context activity;
    List<KategoriItem> kategoriItemList;

    public KategoriAdapter(Context activity, List<KategoriItem> kategoriItemList) {
        this.activity = activity;
        this.kategoriItemList = kategoriItemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_kategori, viewGroup, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(KategoriAdapter.ViewHolder viewHolder, int position) {
        KategoriItem kategoriItem = kategoriItemList.get(position);

        Picasso.with(activity).load( kategoriItem.kategori_gambar ).into( viewHolder.imageView );
        viewHolder.txtview.setText( kategoriItem.kategori_judul );

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(v.getContext(), KategoriActivity.class);
                myIntent.putExtra("key", kategoriItem.kategori_judul); //Optional parameters
                v.getContext().startActivity(myIntent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return kategoriItemList.size();
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
