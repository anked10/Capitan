package com.tec.bufeo.capitan.MVVM.Foro.publicaciones.Repository;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIServiceFeed {
    @FormUrlEncoded
    @POST("index.php?c=Foro&a=listar_publicaciones&key_mobile=123456asdfgh")
    Call<String> savePost(@Field("id_usuario") String id_usuario,
                          @Field("limite_sup") String limite_sup,
                          @Field("limite_inf") String limite_inf);
}
