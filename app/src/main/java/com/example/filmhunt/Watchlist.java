package com.example.filmhunt;

import static com.example.filmhunt.History.history;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.filmhunt.Models.ImdbResponse.Movie;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Watchlist extends BaseActivity {
    private WatchlistHelperClass watchlistHelper;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    public static List<Movie> movieList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.watchlist), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setupNavigationDrawer(R.layout.activity_watchlist, R.id.toolbar, R.id.nav_view, R.id.watchlist, R.id.nav_head_userDetails);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user != null) {
            watchlistHelper = new WatchlistHelperClass(user.getUid());
            recyclerView = findViewById(R.id.recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            movieAdapter = new MovieAdapter(movieList, this::showMovieDetailsDialog);
            recyclerView.setAdapter(movieAdapter);

            // Fetch and display the watchlist
            fetchWatchlist();

            //Adding sample data


            //addSampleData();

        } else {
            Toast.makeText(this, "Please sign in to make a watchlist", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, Dashboard.class);
            startActivity(intent);
            finish();
        }
    }

    private void fetchWatchlist() {
        watchlistHelper.getWatchlist(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                movieList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Movie movie = snapshot.getValue(Movie.class);
                    movieList.add(movie);

                }

                movieAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("WatchlistActivity", "Error fetching watchlist", databaseError.toException());
            }
        });
    }

    private void showMovieDetailsDialog(Movie movie) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_movie_details);

        ImageView movieImageView = dialog.findViewById(R.id.wat_dialog_movie_image);
        TextView movieTitleTextView = dialog.findViewById(R.id.wat_dialog_movie_title);
        TextView movieYearTextView = dialog.findViewById(R.id.wat_dialog_movie_year);
        TextView movieTypeTextView = dialog.findViewById(R.id.wat_dialog_movie_type);
        TextView movieStarsTextView = dialog.findViewById(R.id.wat_dialog_movie_stars);
        TextView movieDirectorsTextView = dialog.findViewById(R.id.wat_dialog_movie_directors);
        TextView moviePlotTextView = dialog.findViewById(R.id.wat_dialog_movie_plot);

        movieTitleTextView.setText(movie.getTitle());
        movieYearTextView.setText(String.valueOf(movie.getYear()));
        movieTypeTextView.setText("Type: " + movie.getType());
        movieStarsTextView.setText("Stars: " + movie.getStars());
        movieDirectorsTextView.setText("Directors: " + movie.getDetails());
        moviePlotTextView.setText("Plot: " + movie.getPlot());

        if (movie.getImage() != null && movie.getImage().getImageUrl() != null) {
            Glide.with(this)
                    .load(movie.getImage().getImageUrl())
                    .into(movieImageView);
        } else {
            movieImageView.setImageResource(R.drawable.ic_photo);
        }

        history.add(movie);

        dialog.show();
    }

//    private void addSampleData() {
//        watchlistHelper.addMovie(new Movie("movieId1", "Movie Title1", 2024, "Movie Details 1"));
//        watchlistHelper.addMovie(new Movie("movieId2", "Movie Title2", 2024, "Movie Details 2"));
//        watchlistHelper.addMovie(new Movie("movieId3", "Movie Title3", 2024, "Movie Details 3"));
//        watchlistHelper.addMovie(new Movie("movieId4", "Movie Title4", 2024, "Movie Details 4"));
//        watchlistHelper.addMovie(new Movie("movieId5", "Movie Title5", 2024, "Movie Details 5"));
//        watchlistHelper.addMovie(new Movie("movieId6", "Movie Title6", 2024, "Movie Details 6"));
//        watchlistHelper.addMovie(new Movie("movieId7", "Movie Title7", 2024, "Movie Details 7"));
//        watchlistHelper.addMovie(new Movie("movieId8", "Movie Title8", 2024, "Movie Details 8"));
//        watchlistHelper.addMovie(new Movie("movieId9", "Movie Title9", 2024, "Movie Details 9"));
//    }
}

