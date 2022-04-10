package com.company.tokoonline.config;

import java.text.NumberFormat;
import java.util.Locale;

public class Config {
    public static String restapi = "http://192.168.10.248/tokoonline-api";



    public static String formatRupiah(int number){
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format( number );
    }
}
