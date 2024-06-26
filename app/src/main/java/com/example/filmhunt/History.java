package com.example.filmhunt;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.filmhunt.Models.ImdbResponse;

import java.util.ArrayList;
import java.util.List;

public class History extends BaseActivity {

    public static List<ImdbResponse.Movie> history = new ArrayList<>();

    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.history), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setupNavigationDrawer(R.layout.activity_history, R.id.toolbar, R.id.nav_view, R.id.history, R.id.nav_head_userDetails);

        recyclerView = findViewById(R.id.histRecycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        movieAdapter = new MovieAdapter(history, this::showMovieDetailsDialog);
        recyclerView.setAdapter(movieAdapter);
    }

    private void showMovieDetailsDialog(ImdbResponse.Movie movie, Dialog dialog) {
        dialog.setContentView(R.layout.dialog_movie_details);

        ImageView movieImageView = dialog.findViewById(R.id.wat_dialog_movie_image);
        TextView movieTitleTextView = dialog.findViewById(R.id.wat_dialog_movie_title);
        TextView movieDetailsTextView = dialog.findViewById(R.id.wat_dialog_movie_details);
        TextView movieTypeTextView = dialog.findViewById(R.id.wat_dialog_movie_type);
        TextView movieStarsTextView = dialog.findViewById(R.id.wat_dialog_movie_stars);
        TextView movieDirectorsTextView = dialog.findViewById(R.id.wat_dialog_movie_directors);
        TextView moviePlotTextView = dialog.findViewById(R.id.wat_dialog_movie_plot);

        movieTitleTextView.setText(movie.getTitle());
        movieTypeTextView.setText("Type: " + movie.getType());
        movieStarsTextView.setText("Stars: " + movie.getStars());
        movieDirectorsTextView.setText("Directors: " + movie.getDetails());

        String movieDetails = movie.getYear() + " • " +
                (movie.getCertificateData() != null ? movie.getCertificateData().getRating() : "N/A") + " • " +
                (movie.getRuntimeData() != null ? movie.getRuntimeData().getFormattedRuntime() : "N/A");
        movieDetailsTextView.setText(movieDetails);

        if (movie.getPlotData() != null && movie.getPlotData().getPlotText() != null) {
            String plotText = movie.getPlotData().getPlotText().getPlainText();
            moviePlotTextView.setText("Plot: " + plotText);
        } else {
            moviePlotTextView.setText("Plot: N/A");
        }

        if (movie.getImage() != null && movie.getImage().getImageUrl() != null) {
            Glide.with(this)
                    .load(movie.getImage().getImageUrl())
                    .into(movieImageView);
        } else {
            movieImageView.setImageResource(R.drawable.ic_photo);
        }

        dialog.show();
    }
}
