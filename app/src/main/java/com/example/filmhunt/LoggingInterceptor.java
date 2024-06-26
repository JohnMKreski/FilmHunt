package com.example.filmhunt;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

// Using this class to log requests
public class LoggingInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        long t1 = System.nanoTime();
        Log.d("Interceptor", String.format("Sending request %s on %s%n%s", request.url(), chain.connection(), request.headers()));

        Response response = chain.proceed(request);
        long t2 = System.nanoTime();
        String responseBodyString = response.body().string();

        Log.d("Interceptor", String.format("Received response for %s in %.1fms%n%s%nResponse body: %s",
                response.request().url(), (t2 - t1) / 1e6d, response.headers(), responseBodyString));

        return response.newBuilder()
                .body(ResponseBody.create(response.body().contentType(), responseBodyString))
                .build();
    }
}
