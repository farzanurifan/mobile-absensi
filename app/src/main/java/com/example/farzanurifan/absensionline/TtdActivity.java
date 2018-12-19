package com.example.farzanurifan.absensionline;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ajithvgiri.canvaslibrary.CanvasView;

import java.io.ByteArrayOutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TtdActivity extends AppCompatActivity {
    Button save, clear;
    CanvasView canvasView;
    RelativeLayout parentView;
    private String password, idUser, lat, lon, agenda;
    ProgressDialog progressDialog;
    TextView id_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ttd);

        final Intent intent = getIntent();
        setTitle("Sign In");

        save = (Button) findViewById(R.id.save);
        clear = (Button) findViewById(R.id.clear);
        id_user = (TextView) findViewById(R.id.id_user);

        parentView = findViewById(R.id.parentView);
        canvasView = new CanvasView(this);
        parentView.addView(canvasView);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setTitle("Mengirim Gambar");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        idUser = intent.getStringExtra("idUser");
        password = intent.getStringExtra("password");
        lat = intent.getStringExtra("lat");
        lon = intent.getStringExtra("lon");
        agenda = intent.getStringExtra("agenda");

        id_user.setText("ID User: " + idUser);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                parentView.setDrawingCacheEnabled(true);
                saveCanvas();
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentView.setDrawingCacheEnabled(false);
                canvasView.clearCanvas();
            }
        });
    }

    public void saveCanvas() {

        Bitmap result = parentView.getDrawingCache();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String myBase64Image = encodeToBase64(result, Bitmap.CompressFormat.JPEG, 100);
        final ApiInterface api = Server.getclient().create(ApiInterface.class);
        Log.d("test", "onImage: "+myBase64Image);

        Call<ResponseApi> kirim =api.signin_ttd(idUser, password,"data:image/jpeg;base64,"+myBase64Image, lat, lon, agenda);
        kirim.enqueue(new Callback<ResponseApi>() {
            @Override
            public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {
                String message = response.body().getMessage();
                progressDialog.dismiss();
                if(message.contains("ACCEPTED")) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(TtdActivity.this);
                    builder.setMessage(message + "\n\nWajah dan tanda tangan diterima, anda sudah berhasil sign in\n\n")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(final DialogInterface dialog, final int id) {
                                    Intent intent = new Intent(TtdActivity.this, MainActivity.class);
                                    intent.putExtra("idUser", idUser);
                                    intent.putExtra("password", password);
                                    startActivity(intent);
                                }
                            });
                    final AlertDialog alert = builder.create();
                    alert.show();
                }
                else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(TtdActivity.this);
                    builder.setMessage(message + "\n\nTanda tangan ditolak, silakan mengulangi\n\n")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(final DialogInterface dialog, final int id) {
                                }
                            });
                    final AlertDialog alert = builder.create();
                    alert.show();
                }
            }

            @Override
            public void onFailure(Call<ResponseApi> call, Throwable t) {
                t.printStackTrace();
                progressDialog.dismiss();
                Toast.makeText(TtdActivity.this, "error", Toast.LENGTH_LONG).show();
            }
        });
    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }
}
