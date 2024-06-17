package com.example.filmhunt;

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

    private List<Movie> movies;
    private OnItemClickListener listener;

    public MovieAdapter(List<Movie> movies, OnItemClickListener listener) {
        this.movies = movies;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Movie movie);
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
        holder.titleTextView.setText(movie.title);
        //change setText when API is ready (Using sample data to test)
        holder.yearTextView.setText(String.valueOf(movie.getYear()));
        holder.detailsTextView.setText(movie.getDetails());

        if (movie.getImage() != null && movie.getImage().getImageUrl() != null) {
            // Use Glide to load the movie image
            Glide.with(holder.itemView.getContext())
                    .load(movie.getImage().getImageUrl())
                    .into(holder.movieImageView);
        } else {
            // Handle the case where there is no image available
            holder.movieImageView.setImageResource(R.drawable.ic_photo); // Set a placeholder image
        }
        holder.itemView.setOnClickListener(v -> listener.onItemClick(movie));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {

        ImageView movieImageView;
        TextView titleTextView, yearTextView, detailsTextView;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            movieImageView = itemView.findViewById(R.id.movie_image);
            titleTextView = itemView.findViewById(R.id.movie_title);
            yearTextView = itemView.findViewById(R.id.movie_year);
            detailsTextView = itemView.findViewById(R.id.movie_details);
        }
    }
}


