package com.company.tokoonline.models;

import android.os.Parcel;
import android.os.Parcelable;

public class CheckoutItem implements Parcelable {
    public int barang_id;
    public String barang_judul;
    public String barang_gambar;
    public int barang_harga;

    public int beli;

    public CheckoutItem(
            int barang_id,
            String barang_judul,
            String barang_gambar,
            int barang_harga,

            int beli) {

        this.barang_id = barang_id;
        this.barang_judul = barang_judul;
        this.barang_gambar = barang_gambar;
        this.barang_harga = barang_harga;

        this.beli = beli;
    }

    protected CheckoutItem(Parcel in) {
        barang_id = in.readInt();
        barang_judul = in.readString();
        barang_gambar = in.readString();
        barang_harga = in.readInt();
        beli = in.readInt();
    }

    public static final Creator<CheckoutItem> CREATOR = new Creator<CheckoutItem>() {
        @Override
        public CheckoutItem createFromParcel(Parcel in) {
            return new CheckoutItem(in);
        }

        @Override
        public CheckoutItem[] newArray(int size) {
            return new CheckoutItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(barang_id);
        dest.writeString(barang_judul);
        dest.writeString(barang_gambar);
        dest.writeInt(barang_harga);
        dest.writeInt(beli);
    }
}
