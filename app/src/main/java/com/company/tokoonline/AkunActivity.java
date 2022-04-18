package com.company.tokoonline;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class AkunActivity extends AppCompatActivity {

    private Context context;
    private SharedPreferences sharedpreferences;


    String uid;
    String nama;
    String jk;
    String email;
    String notelp;
    String alamat;
    String foto;

    String pengiriman_penerima;
    String pengiriman_notelp;
    String pengiriman_jalan;
    String pengiriman_provinsi;
    String pengiriman_kabupaten;
    String pengiriman_kecamatan;
    String pengiriman_desa;
    String pengiriman_rt;
    String pengiriman_rw;
    String pengiriman_kodepos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_akun);




        context = AkunActivity.this;

        getDataPrefrence();

        TextView tv_uid = findViewById(R.id.uid);
        TextView tv_namalengkap = findViewById(R.id.namalengkap);
        TextView tv_email = findViewById(R.id.email);
        TextView tv_jk = findViewById(R.id.jk);
        TextView tv_notelp = findViewById(R.id.notelp);
        TextView tv_alamat = findViewById(R.id.alamat);


        TextView tv_pengiriman_penerima = findViewById(R.id.pengiriman_penerima);
        TextView tv_pengiriman_notelp = findViewById(R.id.pengiriman_notelp);
        TextView tv_pengiriman_jalan = findViewById(R.id.pengiriman_jalan);
        TextView tv_pengiriman_provinsi = findViewById(R.id.pengiriman_provinsi);
        TextView tv_pengiriman_kabupaten = findViewById(R.id.pengiriman_kabupaten);
        TextView tv_pengiriman_kecamatan = findViewById(R.id.pengiriman_kecamatan);
        TextView tv_pengiriman_desa = findViewById(R.id.pengiriman_desa);
        TextView tv_pengiriman_rt = findViewById(R.id.pengiriman_rt);
        TextView tv_pengiriman_rw = findViewById(R.id.pengiriman_rw);
        TextView tv_pengiriman_kodepos = findViewById(R.id.pengiriman_kodepos);



        if( jk.equalsIgnoreCase("L") ){
            jk = "Laki-laki";
        }else if( jk.equalsIgnoreCase("P") ){
            jk = "Perempuan";
        }


        tv_uid.setText( uid );
        tv_namalengkap.setText( nama );
        tv_email.setText( email );
        tv_jk.setText( jk );
        tv_notelp.setText( notelp );
        tv_alamat.setText( alamat );


        tv_pengiriman_penerima.setText( pengiriman_penerima );
        tv_pengiriman_notelp.setText( pengiriman_notelp );
        tv_pengiriman_jalan.setText( pengiriman_jalan );
        tv_pengiriman_provinsi.setText( pengiriman_provinsi );
        tv_pengiriman_kabupaten.setText( pengiriman_kabupaten );
        tv_pengiriman_kecamatan.setText( pengiriman_kecamatan );
        tv_pengiriman_desa.setText( pengiriman_desa );
        tv_pengiriman_rt.setText( pengiriman_rt );
        tv_pengiriman_rw.setText( pengiriman_rw );
        tv_pengiriman_kodepos.setText( pengiriman_kodepos );



        if( TextUtils.isEmpty(uid) ){

            startActivity(new Intent(context, LoginActivity.class));
            finish();

        }

        findViewById(R.id.actionBack).setOnClickListener(v ->{
            onBackPressed();
        });


        findViewById(R.id.actionLogout).setOnClickListener(v ->{

            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Perhartian");
            alertDialog.setMessage("Yakin ingin logout dari aplikasi?");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ya",
                    (dialog, which) -> {
                        dialog.dismiss();


                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.remove("username");
                        editor.remove("password");

                        editor.remove("uid");
                        editor.remove("email");
                        editor.remove("nama");
                        editor.remove("foto");
                        editor.remove("jk");
                        editor.remove("notelp");
                        editor.remove("alamat");
                        editor.remove("level");


                        editor.apply();


                        startActivity(new Intent(context, MainActivity.class));
                        finish();


                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Batal",
                    (dialog, which) -> dialog.dismiss());
            alertDialog.show();


        });




        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottomsheet_akun);

        findViewById(R.id.actionEdit).setOnClickListener(v ->{
            getDataPrefrence();

            bottomSheetDialog.findViewById(R.id.actionClose).setOnClickListener(v1->{
                bottomSheetDialog.dismiss();
            });

            TextInputEditText ti_namalengkap = bottomSheetDialog.findViewById(R.id.namalengkap);
            TextInputEditText ti_email = bottomSheetDialog.findViewById(R.id.email);
            //AutoCompleteTextView ti_jk = bottomSheetDialog.findViewById(R.id.jk);
            TextInputEditText ti_notelp = bottomSheetDialog.findViewById(R.id.notelp);
            TextInputEditText ti_alamat = bottomSheetDialog.findViewById(R.id.alamat);

            TextInputEditText ti_pengiriman_penerima = bottomSheetDialog.findViewById(R.id.pengiriman_penerima);
            TextInputEditText ti_pengiriman_notelp = bottomSheetDialog.findViewById(R.id.pengiriman_notelp);
            TextInputEditText ti_pengiriman_jalan = bottomSheetDialog.findViewById(R.id.pengiriman_jalan);
            TextInputEditText ti_pengiriman_provinsi = bottomSheetDialog.findViewById(R.id.pengiriman_provinsi);
            TextInputEditText ti_pengiriman_kabupaten = bottomSheetDialog.findViewById(R.id.pengiriman_kabupaten);
            TextInputEditText ti_pengiriman_kecamatan = bottomSheetDialog.findViewById(R.id.pengiriman_kecamatan);
            TextInputEditText ti_pengiriman_desa = bottomSheetDialog.findViewById(R.id.pengiriman_desa);
            TextInputEditText ti_pengiriman_rt = bottomSheetDialog.findViewById(R.id.pengiriman_rt);
            TextInputEditText ti_pengiriman_rw = bottomSheetDialog.findViewById(R.id.pengiriman_rw);
            TextInputEditText ti_pengiriman_kodepos = bottomSheetDialog.findViewById(R.id.pengiriman_kodepos);


            ArrayList<String> items = new ArrayList<>();
            items.add("Laki-laki");
            items.add("Perempuan");
            ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
            //ti_jk.setAdapter(itemsAdapter);

            ti_namalengkap.setText( nama );
            ti_email.setText( email );
            //ti_jk.setText( jk ,false);
            ti_notelp.setText( notelp );
            ti_alamat.setText( alamat );

            ti_pengiriman_penerima.setText( pengiriman_penerima );
            ti_pengiriman_notelp.setText( pengiriman_notelp );
            ti_pengiriman_jalan.setText( pengiriman_jalan );
            ti_pengiriman_provinsi.setText( pengiriman_provinsi );
            ti_pengiriman_kabupaten.setText( pengiriman_kabupaten );
            ti_pengiriman_kecamatan.setText( pengiriman_kecamatan );
            ti_pengiriman_desa.setText( pengiriman_desa );
            ti_pengiriman_rt.setText( pengiriman_rt );
            ti_pengiriman_rw.setText( pengiriman_rw );
            ti_pengiriman_kodepos.setText( pengiriman_kodepos );

            bottomSheetDialog.show();

        });



        String level = sharedpreferences.getString("level","");

        LinearLayout pengiriman1 = findViewById(R.id.pengiriman);
        LinearLayout pengiriman2 = bottomSheetDialog.findViewById(R.id.pengiriman);
        if( level.equalsIgnoreCase("penjual") ){
            pengiriman1.setVisibility(View.GONE);
            pengiriman2.setVisibility(View.GONE);
        }
    }

    public void getDataPrefrence(){


        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);
        uid = sharedpreferences.getString("uid", "");
        nama = sharedpreferences.getString("nama", "");
        jk = sharedpreferences.getString("jk", "");
        email = sharedpreferences.getString("email", "");
        notelp = sharedpreferences.getString("notelp", "");
        alamat = sharedpreferences.getString("alamat", "");
        foto = sharedpreferences.getString("foto", "");

        pengiriman_penerima = sharedpreferences.getString("pengiriman_penerima", "");
        pengiriman_notelp = sharedpreferences.getString("pengiriman_notelp", "");
        pengiriman_jalan = sharedpreferences.getString("pengiriman_jalan", "");
        pengiriman_provinsi = sharedpreferences.getString("pengiriman_provinsi", "");
        pengiriman_kabupaten = sharedpreferences.getString("pengiriman_kabupaten", "");
        pengiriman_kecamatan = sharedpreferences.getString("pengiriman_kecamatan", "");
        pengiriman_desa = sharedpreferences.getString("pengiriman_desa", "");
        pengiriman_rt = sharedpreferences.getString("pengiriman_rt", "");
        pengiriman_rw = sharedpreferences.getString("pengiriman_rw", "");
        pengiriman_kodepos = sharedpreferences.getString("pengiriman_kodepos", "");


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}