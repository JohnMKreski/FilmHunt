package com.example.filmhunt;

import com.example.filmhunt.Models.ImdbResponse.Movie;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WatchlistHelperClass {
    private DatabaseReference watchlistReference;
    private FirebaseDatabase rootNode;

    public WatchlistHelperClass(String uid) {
        rootNode = FirebaseDatabase.getInstance();
        watchlistReference = rootNode.getReference("users").child(uid).child("watchlist");
    }

    public void addMovie(Movie movie) {
        watchlistReference.child(movie.id).setValue(movie);
        watchlistReference.child(movie.getId()).setValue(movie);
    }

    public void removeMovie(String movieId) {
        watchlistReference.child(movieId).removeValue();
    }

    public void getWatchlist(ValueEventListener listener) {
        watchlistReference.addListenerForSingleValueEvent(listener);
    }
}
