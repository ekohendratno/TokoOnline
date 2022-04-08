package com.company.tokoonline.adapters;

import android.app.Activity;
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

public class TerkiniAdapter extends RecyclerView.Adapter<TerkiniAdapter.ViewHolder> {

    int[] images = {R.drawable.a, R.drawable.a, R.drawable.a, R.drawable.a, R.drawable.a, R.drawable.a, R.drawable.a};
    String[] food_items = {"prawan", "awadhi_lucknow_biryani", "eggwraps", "chips", "mayonnaise", "companin", "mixvegwrap"};
    private Activity activity;


    public TerkiniAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.item_terkini, viewGroup, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(TerkiniAdapter.ViewHolder viewHolder, int position) {
        viewHolder.imageView.setImageResource(images[position]);
        viewHolder.txtview.setText(food_items[position].toUpperCase());

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
