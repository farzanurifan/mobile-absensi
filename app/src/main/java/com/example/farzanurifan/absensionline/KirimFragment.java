package com.example.farzanurifan.absensionline;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KirimFragment extends Fragment {
    private CameraView camera;
    private CameraKitEventListener cameradListener;
    private Button btnCapture, btnTrain;
    private String password, idUser;
    DatabaseHelper miniDb;
    TextView id_user;
    ProgressDialog progressDialog;
    MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_kirim, container, false);
        getActivity().setTitle("Kirim Foto");

        id_user = (TextView) rootView.findViewById(R.id.id_user);

        mainActivity = (MainActivity) getActivity();
        idUser = mainActivity.idUser;
        password = mainActivity.password;

        id_user.setText("ID User: " + idUser);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        cameradListener = new CameraKitEventListener() {
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
                result = Bitmap.createScaledBitmap(result, 512,512, true);
                String myBase64Image = encodeToBase64(result, Bitmap.CompressFormat.JPEG, 100);

                final ApiInterface api = Server.getclient().create(ApiInterface.class);
                final long StartTime = new Date().getTime();

                Call<ResponseApi> kirim =api.kirim(idUser, password,"data:image/jpeg;base64,"+myBase64Image);
                kirim.enqueue(new Callback<ResponseApi>() {
                    @Override
                    public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {
                        final long EndTime = new Date().getTime();
                        final long delta = EndTime - StartTime;
                        saveDB(idUser, String.valueOf(StartTime), String.valueOf(EndTime), String.valueOf(delta), String.valueOf(EndTime));
                        String hasil = response.body().getMessage();
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), hasil, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<ResponseApi> call, Throwable t) {
                        t.printStackTrace();
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "error", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {
            }
        };

        camera = (CameraView) rootView.findViewById(R.id.camera);
        camera.addCameraKitListener(cameradListener);

        btnCapture = (Button) rootView.findViewById(R.id.btn_foto);
        btnTrain = (Button) rootView.findViewById(R.id.btn_train);

        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setTitle("Mengirim Gambar");
                progressDialog.show();
                camera.captureImage();
            }
        });

        btnTrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setTitle("Training Gambar");
                progressDialog.show();
                final ApiInterface api = Server.getclient().create(ApiInterface.class);

                Call<ResponseApi> training = api.trainFoto(idUser, password);
                training.enqueue(new Callback<ResponseApi>() {
                    @Override
                    public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {
                        String hasil = response.body().getMessage();
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), hasil , Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<ResponseApi> call, Throwable t) {
                        t.printStackTrace();
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "error", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        return rootView;
    }

    public void saveDB (String nama, String start, String end, String delta , String time) {
        String _nama = nama;
        String _start = start;
        String _end = end;
        String _delta = delta;
        String _time = time;

        miniDb = new DatabaseHelper(getContext());
        miniDb.insertData(_nama,_start,_end,_delta,_time);
    }

    @Override
    public void onResume() {
        super.onResume();
        camera.start();
    }

    @Override
    public void onPause() {
        camera.stop();
        super.onPause();
    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }
}
