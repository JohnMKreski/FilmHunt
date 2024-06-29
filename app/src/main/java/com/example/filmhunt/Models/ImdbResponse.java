package com.example.filmhunt.Models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ImdbResponse {

    @SerializedName("d")
    public List<Movie> movies;

    public static class Movie {
        private List<String> directors;

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
        public PlotData plotData;

        @SerializedName("certificate")
        public CertificateData certificateData;

        @SerializedName("runtime")
        public RuntimeData runtimeData;

//        public String details;

        public Movie() {
            // Default constructor required for calls to DataSnapshot.getValue(Movie.class)
        }

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

        public List<String> getDirectors() {
            return directors;
        }

        public void setDirectors(List<String> directors) {
            this.directors = directors;
        }

//        public String getDetails() {
//            return details;
//        }
//
//        public void setDetails(String details) {
//            this.details = details;
//        }

        public PlotData getPlotData() {
            return plotData;
        }

        public void setPlotData(PlotData plotData) {
            this.plotData = plotData;
        }

        public CertificateData getCertificateData() {
            return certificateData;
        }

        public void setCertificateData(CertificateData certificateData) {
            this.certificateData = certificateData;
        }

        public RuntimeData getRuntimeData() {
            return runtimeData;
        }

        public void setRuntimeData(RuntimeData runtimeData) {
            this.runtimeData = runtimeData;
        }

        @Override
        public String toString() {
            return "Movie{" +
                    "image=" + image +
                    ", id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", type='" + type + '\'' +
                    ", qid='" + qid + '\'' +
                    ", rank=" + rank +
                    ", stars='" + stars + '\'' +
                    ", year=" + year +
                    ", plotData=" + plotData +
                    ", directors='" + directors + '\'' +
                    ", certificateData='" + certificateData + '\'' +
                    ", runtimeData='" + runtimeData + '\'' +
                    '}';
        }

        public static class PlotData {
            @SerializedName("plotText")
            private PlotText plotText;

            public PlotText getPlotText() {
                return plotText;
            }

            public void setPlotText(PlotText plotText) {
                this.plotText = plotText;
            }

            @Override
            public String toString() {
                return "PlotData{" +
                        "plotText=" + plotText +
                        '}';
            }

            public static class PlotText {
                @SerializedName("plainText")
                private String plainText;

                public String getPlainText() {
                    return plainText;
                }

                public void setPlainText(String plainText) {
                    this.plainText = plainText;
                }

                @Override
                public String toString() {
                    return "PlotText{" +
                            "plainText='" + plainText + '\'' +
                            '}';
                }
            }
        }

        public static class CertificateData {
            @SerializedName("rating")
            public String rating;

            public String getRating() {
                return rating;
            }

            public void setRating(String rating) {
                this.rating = rating;
            }

            @Override
            public String toString() {
                return "CertificateData{" +
                        "rating='" + rating + '\'' +
                        '}';
            }
        }

        public static class RuntimeData {
            @SerializedName("seconds")
            public int seconds;

            public int getSeconds() {
                return seconds;
            }

            public void setSeconds(int seconds) {
                this.seconds = seconds;
            }

            public String getFormattedRuntime() {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                return String.format("%dh %dmin", hours, minutes);
            }

            @Override
            public String toString() {
                return "RuntimeData{" +
                        "seconds=" + seconds +
                        '}';
            }
        }

        public static class Image {
            @SerializedName("height")
            public int height;

            @SerializedName("imageUrl")
            public String imageUrl;

            @SerializedName("width")
            public int width;

            public Image() {}

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

            @Override
            public String toString() {
                return "Image{" +
                        "height=" + height +
                        ", imageUrl='" + imageUrl + '\'' +
                        ", width=" + width +
                        '}';
            }
        }
    }
}