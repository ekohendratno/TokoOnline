package com.company.tokoonline.admin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.company.tokoonline.R;
import com.company.tokoonline.models.BankItem;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BankAdapter extends RecyclerView.Adapter<BankAdapter.ViewHolder> {


    private Context activity;
    private BottomSheetDialog bottomSheetDialog;


    List<BankItem> bankItemList;

    public interface OnItemCheckListener {
        void onUpdateItemClick(BankItem item);
        void onRemoveItemClick(BankItem item);
    }

    @NonNull
    private OnItemCheckListener onItemClick;

    public BankAdapter(Context activity, BottomSheetDialog bottomSheetDialog, List<BankItem> bankItemList, @NonNull OnItemCheckListener onItemCheckListener) {
        this.activity = activity;
        this.bottomSheetDialog = bottomSheetDialog;
        this.bankItemList = bankItemList;
        this.onItemClick = onItemCheckListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.admin_item_bank, viewGroup, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(BankAdapter.ViewHolder viewHolder, int position) {
        BankItem kategoriItem = bankItemList.get(position);

        viewHolder.txtview.setText( kategoriItem.bank_nama );
        viewHolder.status.setText( kategoriItem.bank_atas_nama );

        viewHolder.actionHapus.setOnClickListener(v->{

            onItemClick.onRemoveItemClick(kategoriItem);

        });

        viewHolder.itemView.setOnClickListener(v -> {

            bottomSheetDialog.show();

            TextInputEditText bank_nama = bottomSheetDialog.findViewById(R.id.bank_nama);
            TextInputEditText bank_atas_nama = bottomSheetDialog.findViewById(R.id.bank_atas_nama);
            TextInputEditText bank_norek = bottomSheetDialog.findViewById(R.id.bank_norek);

            bank_nama.setText( kategoriItem.bank_nama );
            bank_atas_nama.setText( kategoriItem.bank_atas_nama );
            bank_norek.setText( kategoriItem.bank_norek );


            bottomSheetDialog.findViewById(R.id.actionSimpan).setOnClickListener(v1->{
                onItemClick.onUpdateItemClick(kategoriItem);
            });

        });
    }

    @Override
    public int getItemCount() {
        return bankItemList.size();
    }

    /**
     * View holder to display each RecylerView item
     */
    protected class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtview;
        private TextView status;

        private ImageView actionHapus;


        public ViewHolder(View view) {
            super(view);
            txtview = view.findViewById(R.id.txtview);
            status = view.findViewById(R.id.status);

            actionHapus = view.findViewById(R.id.actionHapus);
        }
    }


}
