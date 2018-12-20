package com.example.farzanurifan.absensionline;

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

    @FormUrlEncoded
    @POST("/sendSignature/")
    Call<ResponseApi> kirim_ttd(@Field("idUser") String idUser, @Field("password") String password, @Field("image") String image);

    @FormUrlEncoded
    @POST("/doTrain_TTD/")
    Call<ResponseApi> trainFoto_ttd(@Field("idUser") String idUser, @Field("password") String password);

    @FormUrlEncoded
    @POST("/doPredict_TTD/")
    Call<ResponseApi> predictFoto_ttd(@Field("idUser") String idUser, @Field("password") String password, @Field("image") String image);

    @FormUrlEncoded
    @POST("/signin_TTD/")
    Call<ResponseApi> signin_ttd(@Field("idUser") String idUser, @Field("password") String password, @Field("image") String image, @Field("Lat") String Lat, @Field("Lon") String Lon, @Field("idAgenda") String idAgenda);

}
