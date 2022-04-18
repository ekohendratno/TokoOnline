package com.company.tokoonline.admin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.company.tokoonline.LoginActivity;
import com.company.tokoonline.R;
import com.company.tokoonline.Splash;
import com.company.tokoonline.adapters.KeranjangAdapter;
import com.company.tokoonline.adapters.PesananAdapter;
import com.company.tokoonline.adapters.RekomendasiAdapter;
import com.company.tokoonline.admin.adapters.BarangAdapter;
import com.company.tokoonline.config.AppController;
import com.company.tokoonline.config.Config;
import com.company.tokoonline.models.BarangItem;
import com.company.tokoonline.models.TransaksiItem;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class AdminBarang extends AppCompatActivity {

    private Context context;
    private SharedPreferences sharedpreferences;
    private SwipeRefreshLayout swipeRefresh;

    private RecyclerView recyclerView;
    private BarangAdapter barangAdapter;
    private BottomSheetDialog bottomSheetDialog;
    private ImageView gambar_preview;

    private AutoCompleteTextView status;
    private AutoCompleteTextView kategori;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_barang);


        context = AdminBarang.this;
        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);

        String uid = sharedpreferences.getString("uid", "");

        dialog = new ProgressDialog(context);
        dialog.setMessage("Loading. Please wait...");
        dialog.setCancelable(false);


        if( TextUtils.isEmpty(uid) ){

            startActivity(new Intent(context, LoginActivity.class));
            finish();

        }

        recyclerView = findViewById(R.id.recycle_view0);
        recyclerView.setLayoutManager( new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) );


        bottomSheetDialog = new BottomSheetDialog( context );
        bottomSheetDialog.setContentView(R.layout.admin_bottomsheet_barang);

        bottomSheetDialog.findViewById(R.id.actionClose).setOnClickListener(v1->{
            bottomSheetDialog.dismiss();
        });

        LinearLayout layout1 = bottomSheetDialog.findViewById(R.id.layout1);
        LinearLayout layout2 = bottomSheetDialog.findViewById(R.id.layout2);
        LinearLayout layout3 = bottomSheetDialog.findViewById(R.id.layout3);

        TabLayout tabLayout = bottomSheetDialog.findViewById(R.id.tabs);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                Log.e("p", String.valueOf(tab.getPosition()));

                layout1.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.GONE);
                layout3.setVisibility(View.GONE);

                if (tab.getPosition() == 1) {
                    layout1.setVisibility(View.GONE);
                    layout2.setVisibility(View.VISIBLE);
                    layout3.setVisibility(View.GONE);
                }else if (tab.getPosition() == 2) {
                    layout1.setVisibility(View.GONE);
                    layout2.setVisibility(View.GONE);
                    layout3.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        ArrayList<String> listJenisKelamin = new ArrayList<>();
        listJenisKelamin.add("Publish");
        listJenisKelamin.add("Pending");

        status = bottomSheetDialog.findViewById(R.id.status);
        kategori = bottomSheetDialog.findViewById(R.id.kategori);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(AdminBarang.this, android.R.layout.simple_dropdown_item_1line, listJenisKelamin);
        status.setAdapter(adapter);


        findViewById(R.id.actionBack).setOnClickListener(v -> {

            onBackPressed();

        });


        findViewById(R.id.actionAdd).setOnClickListener(v->{

            bottomSheetDialog.show();

            gambar_preview = bottomSheetDialog.findViewById(R.id.gambar_preview);

            bottomSheetDialog.findViewById(R.id.actionGambar).setOnClickListener(v1 -> {
                showPictureDialog(v1.getContext());
            });


            bottomSheetDialog.findViewById(R.id.actionSimpan).setOnClickListener(v1->{

                new simpanDataPost(0).execute();

            });

        });



        new getDataKategori().execute();
        new getDataBarang().execute();

        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(() -> {
            new getDataBarang().execute();

            swipeRefresh.setRefreshing(false);
        });
    }

    public void showPictureDialog(Context c){
        androidx.appcompat.app.AlertDialog.Builder pictureDialog = new androidx.appcompat.app.AlertDialog.Builder(c);
        pictureDialog.setTitle("Pilih Gambar");
        String[] pictureDialogItems = {
                "Dari Gallery",
                "Dari Camera" };
        pictureDialog.setItems(pictureDialogItems,
                (dialog, which) -> {
                    switch (which) {
                        case 0:
                            choosePhotoFromGallary();
                            break;
                        case 1:
                            takePhotoFromCamera();
                            break;
                    }
                });


        pictureDialog.show();


        return;

    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, 11101);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, 11102);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bitmap = null;
        if (requestCode == 11101 && data != null) {
            Uri picUri = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(picUri);
                Drawable drawing = Drawable.createFromStream(inputStream, picUri.toString() );
                bitmap = ((BitmapDrawable)drawing).getBitmap();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        } else if (requestCode == 11102 && data != null) {

            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");

        }

        if(bitmap != null && ( requestCode == 11101 || requestCode == 11102 ) ) {
            BitmapDrawable result = new BitmapDrawable(bitmap);
            gambar_preview.setImageDrawable(result);
            gambar_preview.setScaleType(ImageView.ScaleType.FIT_CENTER);

            Log.e("result1", String.valueOf(requestCode));
        }
    }


    private class getDataKategori extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {

                RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

                String uid = sharedpreferences.getString("uid", "");

                StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.restapi + "/api/admin_kategori?uid="+uid, response -> {
                    Log.e("VOLLEY", response);
                    try {

                        final JSONObject req = new JSONObject(response);

                        if( req.getBoolean("success") ) {

                            ArrayList<String> kategoriList = new ArrayList<>();

                            JSONArray barangRekomendasiItemArray = req.getJSONArray("response");
                            for(int a=0; a<barangRekomendasiItemArray.length() ;a++){
                                JSONObject rekomendasi = barangRekomendasiItemArray.getJSONObject(a);
                                kategoriList.add(rekomendasi.getString("kategori_judul"));
                            }


                            ArrayAdapter<String> adapter = new ArrayAdapter<>(AdminBarang.this, android.R.layout.simple_dropdown_item_1line, kategoriList);
                            kategori.setAdapter(adapter);


                        }else{
                        }
                    } catch (Exception e) {
                        Log.e("VOLLEY","Authentication error: " + e.getMessage());

                    }
                }, error -> {
                    Log.e("VOLLEY", error.toString());

                });



                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                requestQueue.add(stringRequest);



            }catch (Exception e){
                e.printStackTrace();
            }

            return true;
        }


        @Override
        protected void onPostExecute(Boolean result) {
        }

        @Override
        protected void onPreExecute() {
        }
    }

    private class getDataBarang extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {

                RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

                String uid = sharedpreferences.getString("uid", "");

                StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.restapi + "/api/admin_barang?uid="+uid, response -> {
                    Log.e("VOLLEY", response);
                    dialog.dismiss();
                    try {

                        final JSONObject req = new JSONObject(response);

                        if( req.getBoolean("success") ) {

                            final List<BarangItem> barangRekomendasiItemList = new ArrayList();
                            JSONArray barangRekomendasiItemArray = req.getJSONArray("response");
                            for(int a=0; a<barangRekomendasiItemArray.length() ;a++){
                                JSONObject rekomendasi = barangRekomendasiItemArray.getJSONObject(a);
                                barangRekomendasiItemList.add(new BarangItem(
                                        rekomendasi.getInt("barang_id"),
                                        rekomendasi.getString("barang_judul"),
                                        rekomendasi.getString("barang_katerangan"),
                                        rekomendasi.getString("barang_kategori"),
                                        rekomendasi.getString("barang_variasi"),
                                        rekomendasi.getString("barang_ukuran"),
                                        rekomendasi.getInt("barang_berat"),
                                        rekomendasi.getInt("barang_stok"),
                                        rekomendasi.getInt("barang_harga"),
                                        rekomendasi.getInt("barang_diskon"),
                                        rekomendasi.getInt("barang_terjual"),
                                        rekomendasi.getString("barang_gambar"),
                                        rekomendasi.getString("barang_tanggal"),
                                        rekomendasi.getString("barang_tanggal_diubah"),
                                        rekomendasi.getString("barang_status")
                                ));
                            }

                            barangAdapter = new BarangAdapter(context, bottomSheetDialog, barangRekomendasiItemList, new BarangAdapter.OnItemCheckListener() {
                                @Override
                                public void onUpdateItemClick(BarangItem item) {

                                    new simpanDataPost(item.id).execute();
                                }

                                @Override
                                public void onFotoItemClick(BarangItem item) {
                                    gambar_preview = bottomSheetDialog.findViewById(R.id.gambar_preview);

                                    showPictureDialog(context);
                                }

                                @Override
                                public void onRemoveItemClick(BarangItem item) {

                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                                    alertDialogBuilder.setTitle("Konfirmasi");
                                    alertDialogBuilder
                                            .setMessage("Yakin ingin menghapus produk?")
                                            .setPositiveButton("Ya", (dialog, id) -> {


                                                new removeData(item.id).execute();


                                            })
                                            .setNegativeButton("Tidak", (dialog, id) -> dialog.cancel());

                                    // membuat alert dialog dari builder
                                    AlertDialog alertDialog = alertDialogBuilder.create();

                                    // menampilkan alert dialog
                                    alertDialog.show();

                                }
                            });
                            recyclerView.setAdapter(barangAdapter);

                        }else{
                        }
                    } catch (Exception e) {
                        Log.e("VOLLEY","Authentication error: " + e.getMessage());

                    }
                }, error -> {
                    Log.e("VOLLEY", error.toString());

                });



                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                requestQueue.add(stringRequest);



            }catch (Exception e){
                e.printStackTrace();
            }

            return true;
        }


        @Override
        protected void onPostExecute(Boolean result) {
        }

        @Override
        protected void onPreExecute() {
            dialog.show();
        }
    }

    private class simpanDataPost extends AsyncTask<String, Void, Boolean> {

        int key;
        public simpanDataPost(int key) {
            this.key = key;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                okhttp3.MultipartBody.Builder multipartBody = new okhttp3.MultipartBody.Builder().setType(okhttp3.MultipartBody.FORM);

                Date currentTime = Calendar.getInstance().getTime();
                String uid = sharedpreferences.getString("uid", "");

                TextInputEditText barang_nama = bottomSheetDialog.findViewById(R.id.barang_nama);
                TextInputEditText keterangan = bottomSheetDialog.findViewById(R.id.keterangan);

                ImageView gambar_preview = bottomSheetDialog.findViewById(R.id.gambar_preview);
                AutoCompleteTextView status = bottomSheetDialog.findViewById(R.id.status);
                AutoCompleteTextView kategori = bottomSheetDialog.findViewById(R.id.kategori);
                TextInputEditText variasi = bottomSheetDialog.findViewById(R.id.variasi);
                TextInputEditText ukuran = bottomSheetDialog.findViewById(R.id.ukuran);
                TextInputEditText berat = bottomSheetDialog.findViewById(R.id.berat);
                TextInputEditText stok = bottomSheetDialog.findViewById(R.id.stok);
                TextInputEditText harga = bottomSheetDialog.findViewById(R.id.harga);
                TextInputEditText diskon = bottomSheetDialog.findViewById(R.id.diskon);

                if(gambar_preview.getDrawable() != null){

                    BitmapDrawable drawable = (BitmapDrawable) gambar_preview.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();

                    File f1 = new File( getCacheDir(), "barang_"+currentTime.toString()+".jpg" );
                    f1.createNewFile();

                    ByteArrayOutputStream bos1 = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos1);
                    byte[] bitmapdata1 = bos1.toByteArray();

                    FileOutputStream fos1 = new FileOutputStream(f1);
                    fos1.write(bitmapdata1);
                    fos1.flush();
                    fos1.close();

                    final okhttp3.MediaType MEDIA_TYPE = okhttp3.MediaType.parse("image/jpeg");

                    multipartBody.addFormDataPart("gambar", f1.getName(), okhttp3.RequestBody.create(MEDIA_TYPE, f1));

                }

                multipartBody.addFormDataPart("judul", barang_nama.getText().toString());
                multipartBody.addFormDataPart("keterangan", keterangan.getText().toString());
                multipartBody.addFormDataPart("kategori", kategori.getText().toString());
                multipartBody.addFormDataPart("variasi", variasi.getText().toString());
                multipartBody.addFormDataPart("ukuran", ukuran.getText().toString());
                multipartBody.addFormDataPart("berat", berat.getText().toString());
                multipartBody.addFormDataPart("stok", stok.getText().toString());
                multipartBody.addFormDataPart("harga", harga.getText().toString());
                multipartBody.addFormDataPart("diskon", diskon.getText().toString());
                multipartBody.addFormDataPart("status", status.getText().toString());

                okhttp3.RequestBody requestBody = multipartBody.build();

                String url = Config.restapi +"/api/admin_barang_simpan?uid="+uid+"&key="+key;
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();

                okhttp3.OkHttpClient.Builder builder = new okhttp3.OkHttpClient.Builder();
                builder.connectTimeout(5, TimeUnit.MINUTES) // connect timeout
                        .writeTimeout(5, TimeUnit.MINUTES) // write timeout
                        .readTimeout(5, TimeUnit.MINUTES); // read timeout

                okhttp3.OkHttpClient okHttpClient = builder.build();
                okhttp3.Response response = okHttpClient.newCall(request).execute();
                String res = response.body().string();

                Log.e("res",res);

                JSONObject j1 = new JSONObject( res );
                if(j1.getBoolean("success")){
                    dialog.dismiss();

                    return true;

                }
            } catch (Exception e) {
                e.printStackTrace();
                dialog.dismiss();
            }

            return false;
        }


        @Override
        protected void onPostExecute(Boolean result) {

            new getDataBarang().execute();

        }

        @Override
        protected void onPreExecute() {

            dialog.show();

        }
    }

    private class removeData extends AsyncTask<String, Void, Boolean> {

        int key;
        public removeData(int key) {
            this.key = key;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {

                RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

                String uid = sharedpreferences.getString("uid", "");

                StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.restapi + "/api/admin_barang_hapus?uid="+uid+"&key="+key, response -> {
                    Log.e("VOLLEY", response);
                    try {

                        final JSONObject req = new JSONObject(response);

                        if( req.getBoolean("success") ) {

                            new getDataBarang().execute();

                        }else{

                        }

                    } catch (Exception e) {
                        Log.e("VOLLEY","Authentication error: " + e.getMessage());

                    }
                }, error -> {
                    Log.e("VOLLEY", error.toString());

                });



                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                requestQueue.add(stringRequest);



            }catch (Exception e){
                e.printStackTrace();
            }

            return true;
        }


        @Override
        protected void onPostExecute(Boolean result) {

        }

        @Override
        protected void onPreExecute() {

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}