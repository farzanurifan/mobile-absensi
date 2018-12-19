package com.example.farzanurifan.absensionline;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.io.ByteArrayOutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SigninActivity extends AppCompatActivity {
    private CameraView foto;
    private CameraKitEventListener cameraListener;
    private Button btnFoto;
    private TextView tipe_activity, current_nrp;
    ProgressDialog progressDialog;
    private String password, idUser, lat, lon, agenda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        final Intent intent = getIntent();
        setTitle("Sign In");

        current_nrp = (TextView) findViewById(R.id.current_nrp);
        tipe_activity = (TextView) findViewById(R.id.tipe_activity);
        tipe_activity.setText(intent.getStringExtra("tipe"));

        idUser = intent.getStringExtra("idUser");
        password = intent.getStringExtra("password");
        lat = intent.getStringExtra("lat");
        lon = intent.getStringExtra("lon");
        agenda = intent.getStringExtra("agenda");

        current_nrp.setText("ID User: " + idUser);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setTitle("Mengirim Gambar");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        cameraListener = new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                byte[] picture = cameraKitImage.getJpeg();
                Bitmap result = BitmapFactory.decodeByteArray(picture, 0, picture.length);
                result = Bitmap.createScaledBitmap(result, 512, 512, true);
                String myBase64Image = encodeToBase64(result, Bitmap.CompressFormat.JPEG, 100);

                final ApiInterface api = Server.getclient().create(ApiInterface.class);

                Call<ResponseApi> signin = api.signin(idUser, password, "data:image/jpeg;base64," + myBase64Image, lat, lon, agenda);
                signin.enqueue(new Callback<ResponseApi>() {
                    @Override
                    public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {
                        System.out.println(response.toString());
                        String message = response.body().getMessage();
                        progressDialog.dismiss();
                        if(message.contains("ACCEPTED")) {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(SigninActivity.this);
                            builder.setMessage(message + "\n\nWajah diterima, silakan lanjut ke tanda tangan\n\n")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(final DialogInterface dialog, final int id) {
                                            Intent intent = new Intent(SigninActivity.this, TtdActivity.class);
                                            intent.putExtra("idUser", idUser);
                                            intent.putExtra("password", password);
                                            intent.putExtra("lat", lat);
                                            intent.putExtra("lon", lon);
                                            intent.putExtra("agenda", agenda);
                                            startActivity(intent);
                                        }
                                    });
                            final AlertDialog alert = builder.create();
                            alert.show();
                        }
                        else {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(SigninActivity.this);
                            builder.setMessage(message + "\n\nWajah ditolak, silakan mengulangi\n\n")
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
                        Toast.makeText(SigninActivity.this, "Failed to send", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {
            }
        };

        foto = (CameraView) findViewById(R.id.foto);
        foto.addCameraKitListener(cameraListener);

        btnFoto = (Button) findViewById(R.id.btn_foto);

        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                foto.captureImage();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        foto.start();
    }

    @Override
    protected void onPause() {
        foto.stop();
        super.onPause();
    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }
}
