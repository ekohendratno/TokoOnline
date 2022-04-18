package com.company.tokoonline.models;

public class BarangItem {
    public String barang_judul;
    public String barang_katerangan;
    public String barang_kategori;
    public String barang_variasi;
    public String barang_ukuran;
    public int barang_berat;
    public int barang_stok;
    public int barang_harga;
    public int barang_diskon;
    public int barang_terjual;
    public String barang_gambar;
    public String barang_tanggal;
    public String barang_tanggal_diubah;
    public String barang_status;
    public int id;

    public BarangItem(
            int id,
            String barang_judul,
            String barang_katerangan,
            String barang_kategori,
            String barang_variasi,
            String barang_ukuran,
            int barang_berat,
            int barang_stok,
            int barang_harga,
            int barang_diskon,
            int barang_terjual,
            String barang_gambar,
            String barang_tanggal,
            String barang_tanggal_diubah,
            String barang_status) {
        this.id = id;
        this.barang_judul = barang_judul;
        this.barang_katerangan = barang_katerangan;
        this.barang_kategori = barang_kategori;
        this.barang_variasi = barang_variasi;
        this.barang_ukuran = barang_ukuran;
        this.barang_berat = barang_berat;
        this.barang_stok = barang_stok;
        this.barang_harga = barang_harga;
        this.barang_diskon = barang_diskon;
        this.barang_terjual = barang_terjual;
        this.barang_gambar = barang_gambar;
        this.barang_tanggal = barang_tanggal;
        this.barang_tanggal_diubah = barang_tanggal_diubah;
        this.barang_status = barang_status;
    }
}