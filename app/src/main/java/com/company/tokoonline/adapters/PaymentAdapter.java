package com.company.tokoonline.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.company.tokoonline.R;
import com.company.tokoonline.config.Config;
import com.company.tokoonline.models.BankItem;
import com.company.tokoonline.models.KeranjangItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.ViewHolder> {

    private Context activity;
    private List<BankItem> checkoutItemList;


    public PaymentAdapter(Context activity, List<BankItem> checkoutItemList) {
        this.activity = activity;
        this.checkoutItemList = checkoutItemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_bank, viewGroup, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(PaymentAdapter.ViewHolder viewHolder, int position) {
        BankItem banktItem = checkoutItemList.get(position);

        viewHolder.bank_nama.setText(banktItem.bank_nama);
        viewHolder.bank_norek.setText(banktItem.bank_norek);

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
        private TextView bank_nama;
        private TextView bank_norek;


        public ViewHolder(View view) {
            super(view);

            bank_nama = view.findViewById(R.id.bank_nama);
            bank_norek = view.findViewById(R.id.bank_norek);
        }
    }
}
