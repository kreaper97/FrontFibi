package com.chorbos.fibi.Rest.interceptor;



import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


public class AuthenticationInterceptor implements Interceptor {

    public static final String TAG = "AuthInterceptor";
    private String authToken;

    public AuthenticationInterceptor(String token) {
        this.authToken = token;
    }

    @Override
    public Response intercept(@NonNull Interceptor.Chain chain) throws IOException {
        Request original = chain.request();
        // set or override the `Authorization` header
        // keep the request body
        Request.Builder builder = original.newBuilder()
                .header("token", authToken)
                .method(original.method(), original.body());

        Request request = builder.build();
        return chain.proceed(request);
    }
}


