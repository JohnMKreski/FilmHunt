package com.example.filmhunt;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class ImdbUtils {
    public interface DirectorFetchListener {
        void onDirectorsFetched(String directors);
    }

    public static void fetchDirectors(Context context, String movieId, DirectorFetchListener listener) {
        new Thread(() -> {
            try {
                String url = "https://m.imdb.com/title/" + movieId + "/";
                Document doc = Jsoup.connect(url).get();
                // Targets the 4 common classes in the <ul> of the IMDB website for Directors
                //<li> and <a> inside the <ul> is where to retrieve the directors
                Elements directorElements = doc.select("ul.ipc-inline-list.ipc-inline-list--show-dividers.ipc-inline-list--inline.ipc-metadata-list-item__list-content li a");

                final StringBuilder directors = new StringBuilder("Directors: ");
                int count = 0;
                for (Element director : directorElements) {
                    // Switched the count from 2 to 1 instead because for films with a single director
                    //the single directors name will appear twice. Perhaps use SET to remove dupes
                    if (count < 1) {
                        String directorName = director.text();
                        directors.append(directorName).append(", ");
                        count++;
                    } else {
                        break;
                    }
                }
                // Remove the trailing comma and space
                if (directors.length() > 11) {
                    directors.setLength(directors.length() - 2);
                }

                String directorString = directors.toString();
                ((Activity) context).runOnUiThread(() -> listener.onDirectorsFetched(directorString));
            } catch (IOException e) {
                Log.e("ImdbUtils", "Error fetching directors", e);
                ((Activity) context).runOnUiThread(() -> listener.onDirectorsFetched("Directors: N/A"));
            }
        }).start();
    }
}