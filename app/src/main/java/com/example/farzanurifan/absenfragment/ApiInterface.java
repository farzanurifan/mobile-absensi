package com.example.farzanurifan.absenfragment;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("/sendImg/")
    Call<ResponseApi> kirim(@Field("idUser") String idUser, @Field("password") String password, @Field("image") String image);

    @FormUrlEncoded
    @POST("/doTrain/")
    Call<ResponseApi> trainFoto(@Field("idUser") String idUser, @Field("password") String password);

    @FormUrlEncoded
    @POST("/doPredict/")
    Call<ResponseApi> predictFoto(@Field("idUser") String idUser, @Field("password") String password, @Field("image") String image);

    @FormUrlEncoded
    @POST("/signin/")
    Call<ResponseApi> signin(@Field("idUser") String idUser, @Field("password") String password, @Field("image") String image, @Field("Lat") String Lat, @Field("Lon") String Lon, @Field("idAgenda") String idAgenda);
}
