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

        @SerializedName("plot")
        public String plot;

        public String details;

        public Movie() {
            // Default constructor required for calls to DataSnapshot.getValue(Movie.class)
        }

//        // Constructor for sample data
//        public Movie(String id, String title, Integer year, String details) {
//            this.id = id;
//            this.title = title;
//            this.year = year;
//            this.details = details;
//        }

        // Getters and setters
        public Image getImage() {
            return image;
        }

        public void setImage(Image image) {
            this.image = image;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getQid() {
            return qid;
        }

        public void setQid(String qid) {
            this.qid = qid;
        }

        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }

        public String getStars() {
            return stars;
        }

        public void setStars(String stars) {
            this.stars = stars;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public String getPlot() {
            return plot;
        }

        public void setPlot(String plot) {
            this.plot = plot;
        }

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }

        public static class Image {
            @SerializedName("height")
            public int height;

            @SerializedName("imageUrl")
            public String imageUrl;

            @SerializedName("width")
            public int width;

            public Image() {}

            // Getters and setters
            public int getHeight() {
                return height;
            }

            public void setHeight(int height) {
                this.height = height;
            }

            public String getImageUrl() {
                return imageUrl;
            }

            public void setImageUrl(String imageUrl) {
                this.imageUrl = imageUrl;
            }

            public int getWidth() {
                return width;
            }

            public void setWidth(int width) {
                this.width = width;
            }
        }
    }
}
