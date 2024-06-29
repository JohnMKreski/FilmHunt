package com.example.filmhunt;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.filmhunt.Models.ImdbResponse.Movie;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private static final String TAG = "MovieAdapter";
    private List<Movie> movies;
    private OnItemClickListener listener;

    public MovieAdapter(List<Movie> movies, OnItemClickListener listener) {
        this.movies = movies;
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setMovies(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    public void updateMovie(int position, Movie movie) {
        movies.set(position, movie);
        notifyItemChanged(position);
    }

    public interface OnItemClickListener {
        void onItemClick(Movie movie, Dialog dialog);
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.titleTextView.setText(movie.getTitle());
        holder.yearTextView.setText(String.valueOf(movie.getYear()));
        holder.starsTextView.setText("Stars: " + movie.getStars());
        holder.typeTextView.setText("Type: " + movie.getType());

        // Initially set directors text to "Loading..." or similar
        holder.dirTextView.setText("Directors: Loading...");

        // Fetch/set directors using ImdbUtils
        Context context = holder.itemView.getContext();
        ImdbUtils.fetchDirectors(context, movie.getId(), new ImdbUtils.DirectorFetchListener() {
            @Override
            public void onDirectorsFetched(String directors) {
                holder.dirTextView.setText(directors);
            }
        });

        // Accessing plotData and plotText with null checks to get plainText (plot is here)
        String plotText = "No plot available";
        if (movie.getPlotData() != null && movie.getPlotData().getPlotText() != null) {
            plotText = movie.getPlotData().getPlotText().getPlainText();
        }
        holder.plotTextView.setText("Plot: " + plotText);

        if (movie.getImage() != null && movie.getImage().getImageUrl() != null) {
            // Use Glide to load the movie image
            Glide.with(holder.itemView.getContext())
                    .load(movie.getImage().getImageUrl())
                    .into(holder.movieImageView);
        } else {
            // If no image available, uses placeholder
            holder.movieImageView.setImageResource(R.drawable.ic_photo);
        }

        holder.itemView.setOnClickListener(v -> {
            Log.d(TAG, "Clicked movie ID: " + movie.getId());
            Dialog dialog = new Dialog(holder.itemView.getContext());
            dialog.setContentView(R.layout.dialog_movie_dashboard);
            listener.onItemClick(movie, dialog);
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {

        ImageView movieImageView;
        TextView titleTextView, yearTextView, plotTextView, typeTextView, starsTextView, dirTextView;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            movieImageView = itemView.findViewById(R.id.movie_image);
            titleTextView = itemView.findViewById(R.id.movie_title);
            yearTextView = itemView.findViewById(R.id.movie_year);
            typeTextView = itemView.findViewById(R.id.movie_type);
            starsTextView = itemView.findViewById(R.id.movie_stars);
            dirTextView = itemView.findViewById(R.id.movie_directors);
            plotTextView = itemView.findViewById(R.id.movie_plot);
        }
    }
}
