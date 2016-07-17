package com.asierph.vibbaybooks.REST;

import com.asierph.vibbaybooks.POJOS.Articulo;
import com.asierph.vibbaybooks.POJOS.Puja;

import java.util.List;
import com.google.gson.JsonElement;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PujaService {

    @GET("puja/usuario/{id}")
    Call<List<Puja>> getTodasMisPujas(@Path("id") int idUsuario);
    @GET("puja/articulo/{id}")
    Call<List<Puja>> getTodasPujasPorArticulo(@Path("id") int idArticulo);
    @GET("puja/{idUsuario}/{idArticulo}")
    Call<JsonElement> puedePujar(@Path("idArticulo") int idarticulo, @Path("idUsuario") int idusuario);
    @POST("puja/")
    Call<String> postPuja(@Body Puja p);
    @PUT("puja/{id}")
    Call<Puja> putArticulo(@Path("id") int articulo, @Body Articulo a);
    @DELETE("puja/{id}")
    Call<Puja> deleteArticulo(@Path("id") int articulo);

}
