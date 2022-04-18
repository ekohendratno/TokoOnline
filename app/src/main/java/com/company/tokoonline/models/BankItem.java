package com.company.tokoonline.models;

public class BankItem {
    public int bank_id;
    public String bank_nama;
    public String bank_atas_nama;
    public String bank_norek;
    public BankItem(int bank_id, String bank_nama, String bank_atas_nama, String bank_norek) {
        this.bank_id = bank_id;
        this.bank_nama = bank_nama;
        this.bank_atas_nama = bank_atas_nama;
        this.bank_norek = bank_norek;
    }
}
