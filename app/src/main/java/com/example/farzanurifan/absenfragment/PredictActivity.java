package com.example.farzanurifan.absenfragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
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

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PredictActivity extends AppCompatActivity {
    private CameraView foto;
    private CameraKitEventListener cameraListener;
    private Button btnFoto;
    private TextView tipe_activity;
    private EditText current_nrp;
    private EditText password_predict;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predict);
        final Intent intent = getIntent();
//        Toast.makeText(PredictActivity.this, intent.getStringExtra("nrp") , Toast.LENGTH_LONG).show();
        if(intent.getStringExtra("tipe").equals("Predict")) {
            this.setTitle("Predict Foto");
        } else {
            this.setTitle("Sign In");
        }
        current_nrp = (EditText) findViewById(R.id.current_nrp);
        tipe_activity = (TextView) findViewById(R.id.tipe_activity);
        tipe_activity.setText(intent.getStringExtra("tipe"));
        password_predict = (EditText) findViewById(R.id.password_predict);

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
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                String myBase64Image = encodeToBase64(result, Bitmap.CompressFormat.JPEG, 100);
                final ApiInterface api = Server.getclient().create(ApiInterface.class);
                Log.d("test", "data:image/jpeg;base64," + myBase64Image);
//                Log.d("test", intent.getStringExtra("nrp"));
                JSONObject paramObject = new JSONObject();
                final String password = password_predict.getText().toString();

                final long StartTime = new Date().getTime();
                final String userId = current_nrp.getText().toString();

                String tipe = intent.getStringExtra("tipe");
                if (tipe.equals("Predict")) {
                    Call<ResponseApi> predict = api.predictFoto(userId, password, "data:image/jpeg;base64," + myBase64Image);
                    predict.enqueue(new Callback<ResponseApi>() {
                        @Override
                        public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {
                            System.out.println(response.toString());
                            String message = response.body().getMessage();
                            Toast.makeText(PredictActivity.this, message, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(PredictActivity.this, MainActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Call<ResponseApi> call, Throwable t) {
                            t.printStackTrace();
                            Toast.makeText(PredictActivity.this, "Failed to send", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Call<ResponseApi> signin = api.signin(userId, password, "data:image/jpeg;base64," + myBase64Image, intent.getStringExtra("lat"), intent.getStringExtra("lon"), intent.getStringExtra("agenda"));
                    signin.enqueue(new Callback<ResponseApi>() {
                        @Override
                        public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {
                            System.out.println(response.toString());
                            String message = response.body().getMessage();
                            Toast.makeText(PredictActivity.this, message, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(PredictActivity.this, MainActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Call<ResponseApi> call, Throwable t) {
                            t.printStackTrace();
                            Toast.makeText(PredictActivity.this, "Failed to send", Toast.LENGTH_LONG).show();
                        }
                    });
                }
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
