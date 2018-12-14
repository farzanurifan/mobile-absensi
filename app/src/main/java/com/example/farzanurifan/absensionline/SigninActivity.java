package com.example.farzanurifan.absensionline;

import android.app.ProgressDialog;
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
    private String password, idUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        final Intent intent = getIntent();
        if(intent.getStringExtra("tipe").equals("Predict")) {
            this.setTitle("Predict Foto");
        } else {
            this.setTitle("Sign In");
        }

        current_nrp = (TextView) findViewById(R.id.current_nrp);
        tipe_activity = (TextView) findViewById(R.id.tipe_activity);
        tipe_activity.setText(intent.getStringExtra("tipe"));

        idUser = intent.getStringExtra("idUser");
        password = intent.getStringExtra("password");

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

                Call<ResponseApi> signin = api.signin(idUser, password, "data:image/jpeg;base64," + myBase64Image, intent.getStringExtra("lat"), intent.getStringExtra("lon"), intent.getStringExtra("agenda"));
                signin.enqueue(new Callback<ResponseApi>() {
                    @Override
                    public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {
                        System.out.println(response.toString());
                        String message = response.body().getMessage();
                        Toast.makeText(SigninActivity.this, message, Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                        Intent intent = new Intent(SigninActivity.this, MainActivity.class);
                        intent.putExtra("idUser", idUser);
                        intent.putExtra("password", password);
                        startActivity(intent);
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
