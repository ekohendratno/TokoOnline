package com.company.tokoonline.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.company.tokoonline.DetailActivity;
import com.company.tokoonline.R;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    int[] images = {R.drawable.baju1, R.drawable.baju2, R.drawable.baju1, R.drawable.baju1, R.drawable.baju1, R.drawable.baju1, R.drawable.baju1};
    String[] food_items = {
            "Atasan Batik Kombinasi WP 132 Navi MM Baju Blouse Kerja Pria Modern",
            "Atasan Batik Kombinasi WP 132 Navi MM Baju Blouse Kerja Pria Modern",
            "Atasan Batik Kombinasi WP 132 Navi MM Baju Blouse Kerja Pria Modern",
            "Atasan Batik Kombinasi WP 132 Navi MM Baju Blouse Kerja Pria Modern",
            "Atasan Batik Kombinasi WP 132 Navi MM Baju Blouse Kerja Pria Modern",
            "Atasan Batik Kombinasi WP 132 Navi MM Baju Blouse Kerja Pria Modern",
            "Atasan Batik Kombinasi WP 132 Navi MM Baju Blouse Kerja Pria Modern"
    };

    private Activity activity;


    public CartAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.item_cart, viewGroup, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(CartAdapter.ViewHolder viewHolder, int position) {
        viewHolder.imageView.setImageResource(images[position]);
        viewHolder.txtview.setText(food_items[position]);

        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(v.getContext(), DetailActivity.class);
                myIntent.putExtra("key", "1"); //Optional parameters
                v.getContext().startActivity(myIntent);

                //Toast.makeText(activity, "Position clicked: " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.length;
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
