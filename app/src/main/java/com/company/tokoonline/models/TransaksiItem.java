package com.company.tokoonline.models;

public class TransaksiItem {
    public int barang_id;
    public String barang_judul;
    public String barang_gambar;

    public int transaksi_id;
    public String transaksi_nota;
    public int transaksi_jumlah;
    public int transaksi_harga;
    public int transaksi_harga_total;
    public int transaksi_ongkir;
    public String transaksi_status;
    public String transaksi_tanggal;
    public String transaksi_barang;
    public String transaksi_catatan;
    public String transaksi_user;

    public TransaksiItem(
            int barang_id,
            String barang_judul,
            String barang_gambar,

            int transaksi_id,
            String transaksi_nota,
            int transaksi_jumlah,
            int transaksi_harga,
            int transaksi_harga_total,
            int transaksi_ongkir,
            String transaksi_status,
            String transaksi_tanggal,
            String transaksi_barang,
            String transaksi_catatan,
            String transaksi_user) {

        this.barang_id = barang_id;
        this.barang_judul = barang_judul;
        this.barang_gambar = barang_gambar;

        this.transaksi_id = transaksi_id;
        this.transaksi_nota = transaksi_nota;
        this.transaksi_jumlah = transaksi_jumlah;
        this.transaksi_harga = transaksi_harga;
        this.transaksi_harga_total = transaksi_harga_total;
        this.transaksi_ongkir = transaksi_ongkir;
        this.transaksi_status = transaksi_status;
        this.transaksi_tanggal = transaksi_tanggal;
        this.transaksi_barang = transaksi_barang;
        this.transaksi_catatan = transaksi_catatan;
        this.transaksi_user = transaksi_user;
    }
}
