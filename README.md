# FILMHUNT
Mobile APP for Regis University MSSE667: Web Mobile Frameworks

## ABOUT
FilmHunt is a mobile application designed for movie enthusiasts to explore, track, and manage their favorite movies. The app leverages the Android framework to provide a seamless and engaging user experience.

## KEY FEATURES
- **Search Movies**: Search for movies and get detailed information.
- **Watchlist**: Add movies to your watchlist and manage them easily.
- **Movie Details**: View detailed information about movies, including plot summaries, cast names, ratings and runtime.
- **History**: Keep track of movies you've viewed in the app.

## TECH STACK
- **Programming Language**: Java
- **Framework**: Android SDK
- **Networking**: Retrofit, OkHttp
- **Data Parsing**: Gson, Jsoup
- **Image Loading**: Glide
- **Authentication & Database**: Firebase Authentication, Firebase Realtime Database
- **UI Components**: AndroidX, Material Components

## Firebase Configuration
This project uses Firebase for Firebase Authentication.

To run the project, you need to add the `google-services.json` file to the `app` directory. Follow these steps:

1. Go to the [Firebase Console](https://console.firebase.google.com/).
2. Select your project.
3. Click on the "Android" icon to add an Android app to your project.
4. Register your app with the package name `com.example.filmhunt`.
5. Download the `google-services.json` file.
    5a. In the project page on the left slider navigation, click the 'cog' icon and click Project Settings.
    5b. In General, under the Your apps section, click and download the google-services.json file. 
6. Place the `google-services.json` file in the `app` directory of your project.

Make sure not to share the `google-services.json` file publicly, as it contains sensitive information about your Firebase project.
