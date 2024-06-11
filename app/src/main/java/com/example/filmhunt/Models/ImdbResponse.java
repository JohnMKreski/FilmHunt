package com.example.filmhunt.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ImdbResponse {
    // This class can be edited to include more properties from the api
    @SerializedName("d")
    public List<Movie> movies;

    public static class Movie {
        @SerializedName("i")
        public Image image;

        @SerializedName("id")
        public String id;

        @SerializedName("l")
        public String title;

        @SerializedName("q")
        public String type;

        @SerializedName("qid")
        public String qid;

        @SerializedName("rank")
        public int rank;

        @SerializedName("s")
        public String stars;

        @SerializedName("y")
        public int year;

        public static class Image {
            @SerializedName("height")
            public int height;

            @SerializedName("imageUrl")
            public String imageUrl;

            @SerializedName("width")
            public int width;
        }
    }
}
