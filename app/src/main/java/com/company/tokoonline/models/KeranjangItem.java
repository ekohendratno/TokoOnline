package com.company.tokoonline.models;

import android.os.Parcel;
import android.os.Parcelable;

public class KeranjangItem implements Parcelable {
    public int barang_id;
    public String barang_judul;
    public String barang_gambar;
    public int barang_harga;
    public int barang_stok;

    public int keranjang_id;
    public int keranjang_jumlah;

    public void setJumlah(int keranjang_jumlah) {
        this.keranjang_jumlah = keranjang_jumlah;
    }

    @Override
    public String toString() {
        return "KeranjangItem{" +
                "barang_id=" + barang_id +
                ", barang_judul='" + barang_judul + '\'' +
                ", barang_gambar='" + barang_gambar + '\'' +
                ", barang_harga=" + barang_harga +
                ", barang_stok=" + barang_stok +
                ", keranjang_id=" + keranjang_id +
                ", keranjang_jumlah=" + keranjang_jumlah +
                '}';
    }

    public KeranjangItem(
            int barang_id,
            String barang_judul,
            String barang_gambar,
            int barang_harga,
            int barang_stok,
            int keranjang_id,
            int keranjang_jumlah) {

        this.barang_id = barang_id;
        this.barang_judul = barang_judul;
        this.barang_gambar = barang_gambar;
        this.barang_harga = barang_harga;
        this.barang_stok = barang_stok;

        this.keranjang_id = keranjang_id;
        this.keranjang_jumlah = keranjang_jumlah;
    }

    protected KeranjangItem(Parcel in) {
        barang_id = in.readInt();
        barang_judul = in.readString();
        barang_gambar = in.readString();
        barang_harga = in.readInt();
        barang_stok = in.readInt();
        keranjang_id = in.readInt();
        keranjang_jumlah = in.readInt();
    }

    public static final Creator<KeranjangItem> CREATOR = new Creator<KeranjangItem>() {
        @Override
        public KeranjangItem createFromParcel(Parcel in) {
            return new KeranjangItem(in);
        }

        @Override
        public KeranjangItem[] newArray(int size) {
            return new KeranjangItem[size];
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
        dest.writeInt(barang_stok);
        dest.writeInt(keranjang_id);
        dest.writeInt(keranjang_jumlah);
    }
}
