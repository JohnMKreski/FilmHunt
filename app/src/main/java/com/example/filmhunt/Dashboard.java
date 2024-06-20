package com.example.filmhunt;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ViewTarget;
import com.example.filmhunt.Models.ImdbResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Dashboard extends BaseActivity {
    Button fireBtn;
    TextView apiDetails;
    SearchView searchBar;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupNavigationDrawer(R.layout.activity_dashboard, R.id.toolbar, R.id.nav_view, R.id.dashboard, R.id.nav_head_userDetails);

//            //firebase logout
//            fireBtn = findViewById(R.id.logout);
//            fireBtn.setOnClickListener(v -> {
//                FirebaseAuth.getInstance().signOut();
//                Intent intent = new Intent(getApplicationContext(), Login.class);
//                startActivity(intent);
//                finish();
//            });

        apiDetails = findViewById(R.id.apiDetails);
        searchBar = findViewById(R.id.searchView);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://imdb8.p.rapidapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestManager apiService = retrofit.create(RequestManager.class);

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchMovies(apiService, query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //handles text
                return false;
            }
        });
    }

    private void searchMovies(RequestManager apiService, String query) {
        Call<ImdbResponse> call = apiService.getMovies(query);
        call.enqueue(new Callback<ImdbResponse>() {

            private void setMovieDetails(String details, String imageUrl) {
                apiDetails.setText(details);

                ImageView movieImage = findViewById(R.id.movieImage);
                ViewTarget<ImageView, Drawable> into = Glide.with(Dashboard.this)
                        .load(imageUrl)
                        .into(movieImage);
            }
            @Override
            public void onResponse(Call<ImdbResponse> call, Response<ImdbResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StringBuilder result = new StringBuilder();

                    for (ImdbResponse.Movie movie : response.body().movies) {
                        result.append("Title: ").append(movie.title).append("\n")
                                .append("Year: ").append(movie.year).append("\n")
                                .append("Stars: ").append(movie.stars).append("\n\n");


                        setMovieDetails(result.toString(), movie.image.imageUrl);
                    }

                    apiDetails.setText(result.toString());

                } else {
                    apiDetails.setText("Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ImdbResponse> call, Throwable t) {
                apiDetails.setText("Failure: " + t.getMessage());
            }
        });
    }
}
