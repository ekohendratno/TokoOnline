package com.company.tokoonline.models;

import java.util.List;

public class TransaksiItem {
    public List<BarangItem> barangItemList;

    public int transaksi_id;
    public String transaksi_nota;
    public String transaksi_jumlah;
    public String transaksi_harga;
    public int transaksi_total_bayar;
    public int transaksi_ongkir;
    public String transaksi_status;
    public String transaksi_tanggal;
    public String transaksi_barang;
    public String transaksi_catatan;
    public String transaksi_user;

    public TransaksiItem(
            List<BarangItem> barangItemList,

            int transaksi_id,
            String transaksi_nota,
            String transaksi_jumlah,
            String transaksi_harga,
            int transaksi_total_bayar,
            int transaksi_ongkir,
            String transaksi_status,
            String transaksi_tanggal,
            String transaksi_barang,
            String transaksi_catatan,
            String transaksi_user) {

        this.barangItemList = barangItemList;

        this.transaksi_id = transaksi_id;
        this.transaksi_nota = transaksi_nota;
        this.transaksi_jumlah = transaksi_jumlah;
        this.transaksi_harga = transaksi_harga;
        this.transaksi_total_bayar = transaksi_total_bayar;
        this.transaksi_ongkir = transaksi_ongkir;
        this.transaksi_status = transaksi_status;
        this.transaksi_tanggal = transaksi_tanggal;
        this.transaksi_barang = transaksi_barang;
        this.transaksi_catatan = transaksi_catatan;
        this.transaksi_user = transaksi_user;
    }
}
