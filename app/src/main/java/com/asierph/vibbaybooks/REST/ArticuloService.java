package com.asierph.vibbaybooks.REST;

import com.asierph.vibbaybooks.POJOS.Articulo;
import com.asierph.vibbaybooks.POJOS.Imagen;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ArticuloService {

    @GET("articulo/{id}")
    Call<Articulo> getArticulo(@Path("id") int articulo);
    @GET("articulo/")
    Call<List<Articulo>> getTodosArticulos();
    @GET("articulo/usuario/{id}")
    Call<List<Articulo>> getTodosMisArticulos(@Path("id") int idusuario);
    @POST("articulo/")
    Call<String> postArticulo(@Body Articulo a);
    @POST("articulo/img")
    Call<Imagen> subirImagen(@Body Imagen i);
    @PUT("articulo/{id}")
    Call<String> putArticulo(@Path("id") int articulo, @Body Articulo a);
    @DELETE("articulo/{id}")
    Call<Articulo> deleteArticulo(@Path("id") int articulo);
}
