package com.company.tokoonline.config;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;

import java.text.NumberFormat;
import java.util.Locale;

public class Config {
    public static String restapi = "https://toko1.kopas.id";

    public static String formatRupiah(int number){
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        formatRupiah.setMaximumFractionDigits(0);
        return formatRupiah.format( number );
    }
}
