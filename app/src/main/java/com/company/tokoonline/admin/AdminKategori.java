package com.company.tokoonline.admin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
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
import com.company.tokoonline.admin.adapters.BarangAdapter;
import com.company.tokoonline.admin.adapters.KategoriAdapter;
import com.company.tokoonline.config.AppController;
import com.company.tokoonline.config.Config;
import com.company.tokoonline.models.BarangItem;
import com.company.tokoonline.models.KategoriItem;
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

public class AdminKategori extends AppCompatActivity {

    private Context context;
    private SharedPreferences sharedpreferences;
    private SwipeRefreshLayout swipeRefresh;

    private RecyclerView recyclerView;
    private KategoriAdapter kategoriAdapter;
    private BottomSheetDialog bottomSheetDialog;
    private ImageView gambar_preview;

    ProgressDialog dialog;
    private AutoCompleteTextView status;
    private TextInputEditText judul;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_kategori);


        context = AdminKategori.this;
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
        bottomSheetDialog.setContentView(R.layout.admin_bottomsheet_kategori);

        bottomSheetDialog.findViewById(R.id.actionClose).setOnClickListener(v1->{
            bottomSheetDialog.dismiss();
        });

        status = bottomSheetDialog.findViewById(R.id.status);
        judul = bottomSheetDialog.findViewById(R.id.judul);
        gambar_preview = bottomSheetDialog.findViewById(R.id.gambar_preview);



        ArrayList<String> listJenisKelamin = new ArrayList<>();
        listJenisKelamin.add("Publish");
        listJenisKelamin.add("Pending");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(AdminKategori.this, android.R.layout.simple_dropdown_item_1line, listJenisKelamin);
        status.setAdapter(adapter);

        findViewById(R.id.actionBack).setOnClickListener(v -> {

            onBackPressed();

        });


        findViewById(R.id.actionAdd).setOnClickListener(v->{

            bottomSheetDialog.show();

            status.setText("",false);
            judul.setText("");
            gambar_preview.setImageDrawable(null);

            bottomSheetDialog.findViewById(R.id.actionGambar).setOnClickListener(v1 -> {
                showPictureDialog(v1.getContext());
            });


            bottomSheetDialog.findViewById(R.id.actionSimpan).setOnClickListener(v1->{

                new simpanDataPost(0).execute();

            });

        });



        new getDataKategori().execute();

        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(() -> {
            new getDataKategori().execute();

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
                    dialog.dismiss();
                    try {

                        final JSONObject req = new JSONObject(response);

                        if( req.getBoolean("success") ) {

                            ArrayList<KategoriItem> kategoriList = new ArrayList<>();

                            JSONArray kategoriRekomendasiItemArray = req.getJSONArray("response");
                            for(int a=0; a<kategoriRekomendasiItemArray.length() ;a++){
                                JSONObject rekomendasi = kategoriRekomendasiItemArray.getJSONObject(a);
                                kategoriList.add( new KategoriItem(
                                        rekomendasi.getInt("kategori_id"),
                                        rekomendasi.getString("kategori_judul"),
                                        rekomendasi.getString("kategori_parent"),
                                        rekomendasi.getString("kategori_gambar"),
                                        rekomendasi.getString("kategori_status")
                                ) );
                            }


                            kategoriAdapter = new KategoriAdapter(context, bottomSheetDialog, kategoriList, new KategoriAdapter.OnItemCheckListener() {
                                @Override
                                public void onUpdateItemClick(KategoriItem item) {

                                    new simpanDataPost(item.kategori_id).execute();
                                }

                                @Override
                                public void onFotoItemClick(KategoriItem item) {
                                    gambar_preview = bottomSheetDialog.findViewById(R.id.gambar_preview);

                                    showPictureDialog(context);
                                }

                                @Override
                                public void onRemoveItemClick(KategoriItem item) {

                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                                    alertDialogBuilder.setTitle("Konfirmasi");
                                    alertDialogBuilder
                                            .setMessage("Yakin ingin menghapus produk?")
                                            .setPositiveButton("Ya", (dialog, id) -> {


                                                new removeData(item.kategori_id).execute();


                                            })
                                            .setNegativeButton("Tidak", (dialog, id) -> dialog.cancel());

                                    // membuat alert dialog dari builder
                                    AlertDialog alertDialog = alertDialogBuilder.create();

                                    // menampilkan alert dialog
                                    alertDialog.show();

                                }
                            });
                            recyclerView.setAdapter(kategoriAdapter);

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
                MultipartBody.Builder multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM);

                Date currentTime = Calendar.getInstance().getTime();
                String uid = sharedpreferences.getString("uid", "");

                if(gambar_preview.getDrawable() != null){

                    BitmapDrawable drawable = (BitmapDrawable) gambar_preview.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();

                    File f1 = new File( getCacheDir(), "kategori_"+currentTime.toString()+".jpg" );
                    f1.createNewFile();

                    ByteArrayOutputStream bos1 = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos1);
                    byte[] bitmapdata1 = bos1.toByteArray();

                    FileOutputStream fos1 = new FileOutputStream(f1);
                    fos1.write(bitmapdata1);
                    fos1.flush();
                    fos1.close();

                    final okhttp3.MediaType MEDIA_TYPE = okhttp3.MediaType.parse("image/jpeg");

                    multipartBody.addFormDataPart("gambar", f1.getName(), RequestBody.create(MEDIA_TYPE, f1));

                }

                multipartBody.addFormDataPart("judul", judul.getText().toString());
                multipartBody.addFormDataPart("status", status.getText().toString());

                RequestBody requestBody = multipartBody.build();

                String url = Config.restapi +"/api/admin_kategori_simpan?uid="+uid+"&key="+key;
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();

                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                builder.connectTimeout(5, TimeUnit.MINUTES) // connect timeout
                        .writeTimeout(5, TimeUnit.MINUTES) // write timeout
                        .readTimeout(5, TimeUnit.MINUTES); // read timeout

                OkHttpClient okHttpClient = builder.build();
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

            new getDataKategori().execute();

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

                StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.restapi + "/api/admin_kategori_hapus?uid="+uid+"&key="+key, response -> {
                    Log.e("VOLLEY", response);
                    try {

                        final JSONObject req = new JSONObject(response);

                        if( req.getBoolean("success") ) {

                            new getDataKategori().execute();

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