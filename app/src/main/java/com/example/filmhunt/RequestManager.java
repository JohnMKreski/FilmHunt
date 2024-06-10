package com.example.filmhunt;

import com.example.filmhunt.Models.ImdbResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface RequestManager {
    @Headers({
            "x-rapidapi-host: imdb8.p.rapidapi.com",
            "x-rapidapi-key: aa1fc7ea12mshd49ba34ef21f2fdp188742jsn014d913a39ff"
    })
    @GET("auto-complete")
    Call<ImdbResponse> getMovies(@Query("q") String query);
}
