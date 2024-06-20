package com.example.filmhunt;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.filmhunt.Models.ImdbResponse;
import com.example.filmhunt.Models.ImdbResponse.Movie;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Dashboard extends BaseActivity {
    WatchlistHelperClass watchlistHelper;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    Button fireBtn;
    TextView apiDetails, hintTextView;
    ImageView hintImageView;
    SearchView searchBar;
    RecyclerView recyclerView;
    MovieAdapter movieAdapter;
    LinearLayout searchLayout;
    List<Movie> movieList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.dashboard), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        setupNavigationDrawer(R.layout.activity_dashboard, R.id.toolbar, R.id.nav_view, R.id.dashboard, R.id.nav_head_userDetails);

//            //firebase logout
//            fireBtn = findViewById(R.id.logout);
//            fireBtn.setOnClickListener(v -> {
//                FirebaseAuth.getInstance().signOut();
//                Intent intent = new Intent(getApplicationContext(), Login.class);
//                startActivity(intent);
//                finish();
//            });

//        apiDetails = findViewById(R.id.apiDetails);
        hintTextView = findViewById(R.id.hintTextView);
        hintImageView = findViewById(R.id.hintImageView);
        searchBar = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recycler_view);
        searchLayout = findViewById(R.id.search_layout);

        // Set the query hint
        searchBar.setQueryHint("Search for movies");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        movieAdapter = new MovieAdapter(new ArrayList<>(), movie -> {
            showMovieActionsDialog(movie);
            // Handle movie item click
            Toast.makeText(this, "Clicked: " + movie.title, Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(movieAdapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://imdb8.p.rapidapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestManager apiService = retrofit.create(RequestManager.class);

        // Initialize Firebase Auth and User
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user != null) {
            watchlistHelper = new WatchlistHelperClass(user.getUid());
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            // Redirect to login or handle the error
            // Intent intent = new Intent(getApplicationContext(), Login.class);
            // startActivity(intent);
            // finish();
        }

        //Hides the textView and ImageView when the user searches for a movie
        searchBar.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                hintTextView.setVisibility(View.GONE);
                hintImageView.setVisibility(View.GONE);
            }
        });

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                hintTextView.setVisibility(View.GONE);
                hintImageView.setVisibility(View.GONE);

                searchMovies(apiService, query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //handles text change
                return false;
            }
        });
    }

    //    private void searchMovies(RequestManager apiService, String query) {
//        Call<ImdbResponse> call = apiService.getMovies(query);
//        call.enqueue(new Callback<ImdbResponse>() {
//
////            private void setMovieDetails(String details, String imageUrl) {
////                apiDetails.setText(details);
////
////                ImageView movieImage = findViewById(R.id.movieImage);
////                ViewTarget<ImageView, Drawable> into = Glide.with(Dashboard.this)
////                        .load(imageUrl)
////                        .into(movieImage);
////            }
////            @Override
////            public void onResponse(Call<ImdbResponse> call, Response<ImdbResponse> response) {
////                if (response.isSuccessful() && response.body() != null) {
////                    StringBuilder result = new StringBuilder();
////
////                    for (ImdbResponse.Movie movie : response.body().movies) {
////                        result.append("Title: ").append(movie.title).append("\n")
////                                .append("Year: ").append(movie.year).append("\n")
////                                .append("Stars: ").append(movie.stars).append("\n\n");
////
////
////                        setMovieDetails(result.toString(), movie.image.imageUrl);
////                    }
////
////                    apiDetails.setText(result.toString());
////
////                } else {
////                    apiDetails.setText("Error: " + response.message());
////                }
////            }
//
//            @Override
//            public void onFailure(Call<ImdbResponse> call, Throwable t) {
//                apiDetails.setText("Failure: " + t.getMessage());
//            }
//        });
//    }
    private void searchMovies(RequestManager apiService, String query) {
        Call<ImdbResponse> call = apiService.getMovies(query);
        call.enqueue(new Callback<ImdbResponse>() {
            @Override
            public void onResponse(Call<ImdbResponse> call, Response<ImdbResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movies = response.body().movies;
                    movieAdapter.setMovies(movies);
                    movieAdapter.notifyDataSetChanged();
                } else {
                    hintTextView.setText("Error: " + response.message());
                    hintTextView.setVisibility(View.VISIBLE);
                    hintImageView.setVisibility(View.VISIBLE);

                    Toast.makeText(Dashboard.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ImdbResponse> call, Throwable t) {
                hintTextView.setText("Failure: " + t.getMessage());
                hintTextView.setVisibility(View.VISIBLE);
                hintImageView.setVisibility(View.VISIBLE);

                Toast.makeText(Dashboard.this, "Failure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showMovieActionsDialog(Movie movie) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_movie_dashboard);

        ImageView movieImageView = dialog.findViewById(R.id.dialog_movie_image);
        TextView movieTitleTextView = dialog.findViewById(R.id.dialog_movie_title);
        TextView movieYearTextView = dialog.findViewById(R.id.dialog_movie_year);
        TextView movieTypeTextView = dialog.findViewById(R.id.dialog_movie_type);
        TextView movieStarsTextView = dialog.findViewById(R.id.dialog_movie_stars);
        TextView movieDirectorsTextView = dialog.findViewById(R.id.dialog_movie_directors);
        TextView movieDetailsTextView = dialog.findViewById(R.id.dialog_movie_details);
        Button addToWatchlistButton = dialog.findViewById(R.id.add_to_watchlist_button);
        Button anotherFeatureButton = dialog.findViewById(R.id.another_feature_button);

        movieTitleTextView.setText(movie.getTitle());
        movieYearTextView.setText(String.valueOf(movie.getYear()));
        movieTypeTextView.setText("Type:" + movie.getType());
        movieStarsTextView.setText("Stars: " + movie.getStars());
        //TODO getDirectors getDetails from IMBD
        movieDirectorsTextView.setText("Directors: " + movie.getDetails());
        movieDetailsTextView.setText("Details: " + movie.getDetails());

        if (movie.getImage() != null && movie.getImage().getImageUrl() != null) {
            Glide.with(this)
                    .load(movie.getImage().getImageUrl())
                    .into(movieImageView);
        } else {
            movieImageView.setImageResource(R.drawable.ic_photo);
        }

        addToWatchlistButton.setOnClickListener(v -> {
            // Add movie to watchlist
            if (watchlistHelper != null) {
                watchlistHelper.addMovie(movie);
                Toast.makeText(Dashboard.this, "Added to watchlist", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Dashboard.this, "User not authenticated", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        anotherFeatureButton.setOnClickListener(v -> {
            // Handle another feature
            Toast.makeText(Dashboard.this, "Another feature clicked", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

}
