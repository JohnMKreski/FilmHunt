package com.example.filmhunt;

import android.app.Dialog;

import static com.example.filmhunt.History.history;
import android.os.Bundle;
import android.util.Log;
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
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Dashboard extends BaseActivity {
    private static final String TAG = "Dashboard";
    WatchlistHelperClass watchlistHelper;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    TextView hintTextView;
    ImageView hintImageView;
    SearchView searchBar;
    RecyclerView recyclerView;
    MovieAdapter movieAdapter;
    LinearLayout searchLayout;
    RequestManager apiService;
    Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupNavigationDrawer(R.layout.activity_dashboard, R.id.toolbar, R.id.nav_view, R.id.dashboard, R.id.nav_head_userDetails);

        hintTextView = findViewById(R.id.hintTextView);
        hintImageView = findViewById(R.id.hintImageView);
        searchBar = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recycler_view);
        searchLayout = findViewById(R.id.search_layout);

        // Set the query hint
        searchBar.setQueryHint("Search for movies");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        movieAdapter = new MovieAdapter(new ArrayList<>(), (movie, dialog) -> {
            showMovieActionsDialog(movie, dialog);
            fetchMovieDetails(movie, dialog);
            Toast.makeText(this, "Clicked: " + movie.getTitle(), Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(movieAdapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://imdb8.p.rapidapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder().addInterceptor(new LoggingInterceptor()).build())
                .build();

        apiService = retrofit.create(RequestManager.class);

        // Initialize Firebase Auth and User
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        if (user != null) {
            watchlistHelper = new WatchlistHelperClass(user.getUid());
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
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

    private void searchMovies(RequestManager apiService, String query) {
        Log.d(TAG, "Searching for movies with query: " + query);
        Call<ImdbResponse> call = apiService.getMovies(query);
        call.enqueue(new Callback<ImdbResponse>() {
            @Override
            public void onResponse(Call<ImdbResponse> call, Response<ImdbResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> movies = response.body().movies;
                    movieAdapter.setMovies(movies);
                    movieAdapter.notifyDataSetChanged();
                    Log.d(TAG, "Movies loaded successfully");
                } else {
                    hintTextView.setText("Error: " + response.message());
                    hintTextView.setVisibility(View.VISIBLE);
                    hintImageView.setVisibility(View.VISIBLE);
                    Log.e(TAG, "Error loading movies: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ImdbResponse> call, Throwable t) {
                hintTextView.setText("Failed to load movies. Please try again.");
                hintTextView.setVisibility(View.VISIBLE);
                hintImageView.setVisibility(View.VISIBLE);
                Log.e(TAG, "Failure loading movies: " + t.getMessage(), t);
            }
        });
    }

    private void fetchMovieDetails(Movie movie, Dialog dialog) {
        Log.d(TAG, "Fetching details for movie ID: " + movie.getId());
        Call<ResponseBody> call = apiService.getMovieSummary(movie.getId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "Response received: " + response.toString());

                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBody = response.body().string();
                        Log.d(TAG, "Response body: " + responseBody);

                        // Parsing response with gson
                        Gson gson = new Gson();
                        JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                        JsonObject dataObject = jsonObject.getAsJsonObject("data").getAsJsonObject("title");

                        // extracting the plot here
                        if (dataObject.has("plot")) {
                            JsonObject plotObject = dataObject.getAsJsonObject("plot");
                            if (plotObject.has("plotText")) {
                                JsonObject plotTextObject = plotObject.getAsJsonObject("plotText");
                                String plotPlainText = plotTextObject.get("plainText").getAsString();
                                Movie.PlotData plotData = new Movie.PlotData();
                                Movie.PlotData.PlotText plotText = new Movie.PlotData.PlotText();
                                plotText.setPlainText(plotPlainText);
                                plotData.setPlotText(plotText);
                                movie.setPlotData(plotData);
                                Log.d(TAG, "Plot fetched: " + plotPlainText);
                            } else {
                                Log.d(TAG, "Plot text is null");
                            }
                        } else {
                            Log.d(TAG, "Plot data is null");
                        }

                        // Extracting the rating here
                        if (dataObject.has("certificate")) {
                            JsonObject certificateObject = dataObject.getAsJsonObject("certificate");
                            String rating = certificateObject.get("rating").getAsString();
                            Movie.CertificateData certificateData = new Movie.CertificateData();
                            certificateData.setRating(rating);
                            movie.setCertificateData(certificateData);
                            Log.d(TAG, "Rating fetched: " + rating);
                        }

                        // Extracting the runtime here
                        if (dataObject.has("runtime")) {
                            JsonObject runtimeObject = dataObject.getAsJsonObject("runtime");
                            int seconds = runtimeObject.get("seconds").getAsInt();
                            Movie.RuntimeData runtimeData = new Movie.RuntimeData();
                            runtimeData.setSeconds(seconds);
                            movie.setRuntimeData(runtimeData);
                            Log.d(TAG, "Runtime fetched: " + runtimeData.getFormattedRuntime());
                        }

                        // Fetching directors
                        ImdbUtils.fetchDirectors(Dashboard.this, movie.getId(), new ImdbUtils.DirectorFetchListener() {
                            @Override
                            public void onDirectorsFetched(String directors) {
                                TextView movieDirectorsTextView = dialog.findViewById(R.id.dash_dialog_movie_directors);
                                movieDirectorsTextView.setText(directors);
                            }
                        });
                        // Updating the dialog with extracted movie details
                        updateDialogUI(dialog, movie);
                    } catch (IOException e) {
                        Log.e(TAG, "Error parsing response body: " + e.getMessage(), e);
                    }
                } else {
                    Log.e(TAG, "Error fetching plot details: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Failure fetching plot details: " + t.getMessage(), t);
            }
        });
    }

    private void updateDialogUI(Dialog dialog, Movie movie) {
        TextView moviePlotTextView = dialog.findViewById(R.id.dash_dialog_movie_plot);
        TextView movieDetailsTextView = dialog.findViewById(R.id.dash_dialog_movie_details);

        if (movie.getPlotData() != null && movie.getPlotData().getPlotText() != null) {
            String plotText = movie.getPlotData().getPlotText().getPlainText();
            moviePlotTextView.setText("Plot: " + plotText);
        } else {
            moviePlotTextView.setText("Plot: N/A");
        }

        String movieDetails = movie.getYear() + " • " +
                (movie.getCertificateData() != null ? movie.getCertificateData().getRating() : "N/A") + " • " +
                (movie.getRuntimeData() != null ? movie.getRuntimeData().getFormattedRuntime() : "N/A");
        movieDetailsTextView.setText(movieDetails);
    }

    private void showMovieActionsDialog(Movie movie, Dialog dialog) {
        ImageView movieImageView = dialog.findViewById(R.id.dash_dialog_movie_image);
        TextView movieTitleTextView = dialog.findViewById(R.id.dash_dialog_movie_title);
        TextView movieTypeTextView = dialog.findViewById(R.id.dash_dialog_movie_type);
        TextView movieStarsTextView = dialog.findViewById(R.id.dash_dialog_movie_stars);
        TextView movieDirectorsTextView = dialog.findViewById(R.id.dash_dialog_movie_directors);
        TextView moviePlotTextView = dialog.findViewById(R.id.dash_dialog_movie_plot);
        TextView movieDetailsTextView = dialog.findViewById(R.id.dash_dialog_movie_details);
        Button addToWatchlistButton = dialog.findViewById(R.id.add_to_watchlist_button);
        Button anotherFeatureButton = dialog.findViewById(R.id.another_feature_button);

        movieTitleTextView.setText(movie.getTitle());
        movieTypeTextView.setText("Type: " + movie.getType());
        movieStarsTextView.setText("Stars: " + movie.getStars());

        // Initially set directors text to "Loading..."
        movieDirectorsTextView.setText("Directors: Loading...");

        // Fetch and set directors using ImdbUtils
        ImdbUtils.fetchDirectors(this, movie.getId(), new ImdbUtils.DirectorFetchListener() {
            @Override
            public void onDirectorsFetched(String directors) {
                movieDirectorsTextView.setText(directors);
            }
        });

        // Initial setting
        moviePlotTextView.setText("Plot: N/A");

        String movieDetails = movie.getYear() + " • " +
                (movie.getCertificateData() != null ? movie.getCertificateData().getRating() : "N/A") + " • " +
                (movie.getRuntimeData() != null ? movie.getRuntimeData().getFormattedRuntime() : "N/A");
        movieDetailsTextView.setText(movieDetails);

        if (movie.getImage() != null && movie.getImage().getImageUrl() != null) {
            Glide.with(this)
                    .load(movie.getImage().getImageUrl())
                    .into(movieImageView);
        } else {
            movieImageView.setImageResource(R.drawable.ic_photo);
        }

        addToWatchlistButton.setOnClickListener(v -> {
            if (watchlistHelper != null) {
                watchlistHelper.addMovie(movie);
                Toast.makeText(Dashboard.this, "Added to watchlist", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Added to watchlist: " + movie.getTitle());
            } else {
                Toast.makeText(Dashboard.this, "User not authenticated", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "User not authenticated");
            }
            dialog.dismiss();
        });

        anotherFeatureButton.setOnClickListener(v -> {
            Toast.makeText(Dashboard.this, "Another feature clicked", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Another feature clicked for movie: " + movie.getTitle());
            dialog.dismiss();
        });

        history.add(movie);
        dialog.show();
    }
}
