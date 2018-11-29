package com.example.farzanurifan.absenfragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AbsensiFragment extends Fragment {
    ListView list_absen;
    ArrayList<HashMap<String, String>> arraylist = new ArrayList<HashMap<String, String>>();
    private Button btn_predict;
    private CameraView camera;
    private CameraKitEventListener cameradListener;
    private EditText password, idUser;

//    int[] FOTO = {R.drawable.eunha, R.drawable.eunha, R.drawable.eunha, R.drawable.eunha, R.drawable.eunha};
//    String[] NO = {"Farza Nurifan", "Bambang Merah", "Budi Indomie", "Andi Kick", "Uzumaki Naruto"};
//    String[] NRP = {"5115100019", "5115100020", "5115100021", "5115100022", "5115100023"};
//    int[] STATUS = {R.drawable.checked, R.drawable.checked, R.drawable.error, R.drawable.checked, R.drawable.error};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_absensi, container, false);
        getActivity().setTitle("Predict");
        btn_predict = (Button) rootView.findViewById(R.id.btn_predict);
        idUser = (EditText) rootView.findViewById(R.id.id_user);
        password = (EditText) rootView.findViewById(R.id.password_user);

//        final Intent intent = getIntent();
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
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                String myBase64Image = encodeToBase64(result, Bitmap.CompressFormat.JPEG, 100);
                final ApiInterface api = Server.getclient().create(ApiInterface.class);
                Log.d("test", "onImage: "+myBase64Image);
                JSONObject paramObject = new JSONObject();

                final long StartTime = new Date().getTime();
                final String id_user = idUser.getText().toString();
                final String password_user = password.getText().toString();
                Call<ResponseApi> kirim =api.predictFoto(id_user, password_user,"data:image/jpeg;base64,"+myBase64Image);
                kirim.enqueue(new Callback<ResponseApi>() {
                    @Override
                    public void onResponse(Call<ResponseApi> call, Response<ResponseApi> response) {
                        final long EndTime = new Date().getTime();
                        final long delta = EndTime - StartTime;
                        String hasil = response.body().getMessage();
                        Toast.makeText(getActivity(), hasil, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<ResponseApi> call, Throwable t) {
                        t.printStackTrace();
                        Toast.makeText(getActivity(), "error", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {
            }
        };
//        list_absen = rootView.findViewById(R.id.list_absen);

//        final ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
//        for (int i = 0; i < NRP.length; i++) {
//            HashMap<String, String> hashMap = new HashMap<>();
//            hashMap.put("foto", FOTO[i] + "");
//            hashMap.put("no", NO[i]);
//            hashMap.put("name", NRP[i]);
//            hashMap.put("status", STATUS[i] + "");
//            arrayList.add(hashMap);
//        }
//        String[] from = {"foto", "no", "name", "status"};
//        int[] to = {R.id.foto, R.id.no_mhs, R.id.nrp, R.id.status_absen};
//        SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(), arrayList, R.layout.row_absensi, from, to);//Create object and set the parameters for simpleAdapter
//        list_absen.setAdapter(simpleAdapter);
//        list_absen.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                String nrp_mhs = parent.getItemAtPosition(position).toString();
//                String nrp_mhs = arrayList.get(position).get("name");
////                Toast.makeText(AbsensiActivity.this, nrp_mhs, Toast.LENGTH_LONG).show();
//
//                Intent intent = new Intent(getActivity(), PredictActivity.class);
//                intent.putExtra("nrp", nrp_mhs);
//                intent.putExtra("tipe", "Predict");
//                startActivity(intent);
//            }
//        });

        camera = (CameraView) rootView.findViewById(R.id.camera_predict);
        camera.addCameraKitListener(cameradListener);

        btn_predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.captureImage();
            }
        });
        return rootView;
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
