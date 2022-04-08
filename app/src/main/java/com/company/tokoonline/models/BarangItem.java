package com.company.tokoonline.models;

public class BarangItem {
    public String barang_judul,barang_katerangan,barang_kategori,barang_variasi,barang_ukuran,barang_berat,barang_stok,barang_harga,barang_diskon,barang_gambar,barang_tanggal,barang_tanggal_diubah;
    public int id;
    public BarangItem(
            int id,
            String barang_judul,
            String barang_katerangan,
            String barang_kategori,
            String barang_variasi,
            String barang_ukuran,
            String barang_berat,
            String barang_stok,
            String barang_harga,
            String barang_diskon,
            String barang_gambar,
            String barang_tanggal,
            String barang_tanggal_diubah) {
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
        this.barang_gambar = barang_gambar;
        this.barang_tanggal = barang_tanggal;
        this.barang_tanggal_diubah = barang_tanggal_diubah;
    }
}