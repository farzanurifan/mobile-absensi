package com.example.farzanurifan.absensionline;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ajithvgiri.canvaslibrary.CanvasView;

import java.io.ByteArrayOutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TtdKirimFragment extends Fragment {
    Button save, clear, btn_train_ttd;
    CanvasView canvasView;
    RelativeLayout parentView;
    private String password, idUser;
    ProgressDialog progressDialog;
    MainActivity mainActivity;
    TextView id_user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ttd_kirim, container, false);
        getActivity().setTitle("Kirim Tanda Tangan");
        save = (Button) rootView.findViewById(R.id.save);
        clear = (Button) rootView.findViewById(R.id.clear);
        btn_train_ttd = (Button) rootView.findViewById(R.id.btn_train_ttd);
        id_user = (TextView) rootView.findViewById(R.id.id_user);

        parentView = rootView.findViewById(R.id.parentView);
        canvasView = new CanvasView(getContext());
        parentView.addView(canvasView);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setTitle("Mengirim Gambar");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        mainActivity = (MainActivity) getActivity();
        idUser = mainActivity.idUser;
        password = mainActivity.password;

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

        btn_train_ttd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setTitle("Training Gambar");
                progressDialog.show();
                final ApiInterface api = Server.getclient().create(ApiInterface.class);

                Call<ResponseApi> training = api.trainFoto_ttd(idUser, password);
                training.enqueue(new Callback<ResponseApi>() {
                    @Override
                    public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {
                        String message = response.body().getMessage();
                        progressDialog.dismiss();
                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(message)
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog, final int id) {
                                    }
                                });
                        final AlertDialog alert = builder.create();
                        alert.show();
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

    public void saveCanvas() {

        Bitmap result = parentView.getDrawingCache();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String myBase64Image = encodeToBase64(result, Bitmap.CompressFormat.JPEG, 100);
        final ApiInterface api = Server.getclient().create(ApiInterface.class);
        Log.d("test", "onImage: "+myBase64Image);

        Call<ResponseApi> kirim =api.kirim_ttd(idUser, password,"data:image/jpeg;base64,"+myBase64Image);
        kirim.enqueue(new Callback<ResponseApi>() {
            @Override
            public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {
                String message = response.body().getMessage();
                progressDialog.dismiss();
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                            }
                        });
                final AlertDialog alert = builder.create();
                alert.show();
            }

            @Override
            public void onFailure(Call<ResponseApi> call, Throwable t) {
                t.printStackTrace();
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "error", Toast.LENGTH_LONG).show();
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
