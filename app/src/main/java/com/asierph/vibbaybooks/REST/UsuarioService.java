package com.asierph.vibbaybooks.REST;

import com.asierph.vibbaybooks.POJOS.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UsuarioService {

    @GET("usuario/{email}/{pass}")
    Call<Usuario> getUsuarioPorEmailPass(@Path("email") String email, @Path("pass") String pass);
    @GET("usuario/")
    Call<List<Usuario>> getTodosUsuarios();
    @POST("usuario")
    Call<Usuario> postUsuario(@Body Usuario u);
    @PUT("usuario/{id}")
    Call<Usuario> putUsuario(@Path("id") int usuario, @Body Usuario u);
    @DELETE("usuario/{id}")
    Call<Usuario> deleteUsuario(@Path("id") int Usuario);
}
