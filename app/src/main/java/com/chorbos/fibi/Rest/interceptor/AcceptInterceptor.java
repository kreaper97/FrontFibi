package com.chorbos.fibi.Rest.interceptor;


import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AcceptInterceptor implements Interceptor {

    @NonNull
    private String API_VERSION = "application/vnd.apiintratime.v1+json";


    @Override
    public Response intercept(@NonNull Interceptor.Chain chain) throws IOException {
        Request original = chain.request();
        Request request = original.newBuilder()
                .header("Accept", API_VERSION)
                .method(original.method(), original.body())
                .build();

        return chain.proceed(request);
    }
}
