package com.company.tokoonline.models;

public class KategoriItem {
    public String kategori_judul,kategori_parent,kategori_gambar,kategori_status;
    public int kategori_id;
    public KategoriItem(int kategori_id, String kategori_judul, String kategori_parent, String kategori_gambar, String kategori_status) {
        this.kategori_id = kategori_id;
        this.kategori_judul = kategori_judul;
        this.kategori_parent = kategori_parent;
        this.kategori_gambar = kategori_gambar;
        this.kategori_status = kategori_status;
    }
}