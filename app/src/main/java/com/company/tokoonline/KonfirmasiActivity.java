package com.company.tokoonline;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.FragmentManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.company.tokoonline.config.AppController;
import com.company.tokoonline.config.Config;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class KonfirmasiActivity extends AppCompatActivity {

    private int key;

    private Context context;
    private SharedPreferences sharedpreferences;

    ImageView foto;
    TextView tv_nota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konfirmasi);

        context = KonfirmasiActivity.this;

        Intent intent = getIntent();
        key = intent.getIntExtra("key",0);
        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);

        foto = findViewById(R.id.foto);
        tv_nota = findViewById(R.id.nota);

        findViewById(R.id.actionBack).setOnClickListener(v -> {

            onBackPressed();

        });

        findViewById(R.id.actionOk).setOnClickListener(v ->{

            Intent myIntent = new Intent(v.getContext(), PesananActivity.class);
            startActivity(myIntent);
            finish();

        });

        findViewById(R.id.actionFoto).setOnClickListener(v ->{
            showPictureDialog( v.getContext() );
        });


        new getDataPesananDetail().execute();
    }


    private void showPictureDialog(Context c){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(c);
        pictureDialog.setTitle("Ambil foto struk dari :");
        String[] pictureDialogItems = {
                "Dari Foto Gallery",
                "Dari Foto Camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
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

            bitmap = scaleImageBitmap(bitmap,650);

        } else if (requestCode == 11102 && data != null) {

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            bitmap = scaleImageBitmap(imageBitmap,650);

        }

        if(bitmap != null && ( requestCode == 11101 || requestCode == 11102 ) ) {

            Log.e("result1", String.valueOf(requestCode));

            BitmapDrawable result = new BitmapDrawable(bitmap);
            foto.setImageDrawable(result);
            foto.setScaleType(ImageView.ScaleType.FIT_CENTER);

            AsyncTaskRunnerUploadProfile profile = new AsyncTaskRunnerUploadProfile(bitmap);
            profile.execute();


        }
    }


    public class AsyncTaskRunnerUploadProfile extends AsyncTask<String, String, Boolean> {

        Bitmap bitmap;

        public AsyncTaskRunnerUploadProfile(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        @Override
        protected Boolean doInBackground(String... params) {

            try {

                okhttp3.MultipartBody.Builder multipartBody = new okhttp3.MultipartBody.Builder().setType(okhttp3.MultipartBody.FORM);

                //create a file to write bitmap data
                File f = new File( getCacheDir(), key+".png" );
                f.createNewFile();

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
                byte[] bitmapdata = bos.toByteArray();

                FileOutputStream fos = new FileOutputStream(f);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();

                final okhttp3.MediaType MEDIA_TYPE = f.getPath().endsWith("png") ? okhttp3.MediaType.parse("image/png") : okhttp3.MediaType.parse("image/jpeg");
                multipartBody.addFormDataPart("uploaded_file", f.getName(), okhttp3.RequestBody.create(MEDIA_TYPE, f));
                multipartBody.addFormDataPart("uploaded_key", String.valueOf(key));

                okhttp3.RequestBody requestBody = multipartBody.build();

                String url = Config.restapi +"/api/upload_strukbukti";
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

                    JSONObject jsonObject = j1.getJSONObject("response");

                    Log.e("foo",jsonObject.getString("foto"));


                    return true;

                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            Toast.makeText(context,"Struk bukti berhasil dikirimkan!", Toast.LENGTH_SHORT).show();

        }
        @Override
        protected void onPreExecute() {


        }
    }




    private Bitmap scaleImageBitmap(Bitmap bitmap, int boundBoxInDp) {

        // Get current dimensions
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // Determine how much to scale: the dimension requiring less scaling is
        // closer to the its side. This way the image always stays inside your
        // bounding box AND either x/y axis touches it.
        float xScale = ((float) boundBoxInDp) / width;
        float yScale = ((float) boundBoxInDp) / height;
        float scale = (xScale <= yScale) ? xScale : yScale;

        // Create a matrix for the scaling and add the scaling data
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Create a new bitmap and convert it to a format understood by the ImageView
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        BitmapDrawable result = new BitmapDrawable(scaledBitmap);
        width = scaledBitmap.getWidth();
        height = scaledBitmap.getHeight();

        return scaledBitmap;
    }










    private class getDataPesananDetail extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {

                RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

                String uid = sharedpreferences.getString("uid", "");

                StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.restapi + "/api/pesanan_detail?uid="+uid+"&key="+key, response -> {
                    Log.e("VOLLEY", response);
                    try {

                        final JSONObject req = new JSONObject(response);

                        if( req.getBoolean("success") ) {
                            JSONObject jsonObject = req.getJSONObject("response");
                            tv_nota.setText("#"+jsonObject.getString("transaksi_nota"));

                            Picasso.with( context ).load( jsonObject.getString("transaksi_strukbukti") ).into( foto );

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