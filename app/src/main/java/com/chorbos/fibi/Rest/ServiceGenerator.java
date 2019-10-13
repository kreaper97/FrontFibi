package com.chorbos.fibi.Rest;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.chorbos.fibi.BuildConfig;
import com.chorbos.fibi.Rest.interceptor.AcceptInterceptor;
import com.chorbos.fibi.Rest.interceptor.AuthenticationInterceptor;
import com.chorbos.fibi.Rest.util.StringConverterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Iterator;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    private static final String BASE_URL = "https://fibi-upc.herokuapp.com/api/";
    private static final String TAG = "ServiceGenerator";

    @NonNull
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    @NonNull
    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(StringConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson));

    @NonNull
    private static Retrofit retrofit = builder.build();

    @NonNull
    private static HttpLoggingInterceptor logging =
            new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

    @NonNull
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder().addInterceptor(new AcceptInterceptor());

    public static <S> S createService(@NonNull Class<S> serviceClass) {
        return createService(serviceClass, "");
    }

    public static <S> S createService(@NonNull Class<S> serviceClass, @NonNull String clientId, @NonNull String clientSecret) {
        if (!TextUtils.isEmpty(clientId) && !TextUtils.isEmpty(clientSecret)) {
            String authToken = Credentials.basic(clientId, clientSecret);
            return createService(serviceClass, authToken);
        }
        return createService(serviceClass);
    }

    public static <S> S createService(@NonNull Class<S> serviceClass, String authToken) {
        if (!TextUtils.isEmpty(authToken)) {
            AuthenticationInterceptor interceptor = new AuthenticationInterceptor(authToken);
            if (!httpClient.interceptors().contains(interceptor)) {
                // remove existing interceptors
                Iterator<Interceptor> interceptors = httpClient.interceptors().iterator();
                while (interceptors.hasNext()) {
                    if (interceptors.next() instanceof AuthenticationInterceptor) {
                        interceptors.remove();
                    }
                }
                // add new interceptor & update retrofit
                httpClient.addInterceptor(interceptor);

                builder.client(httpClient.build());
                retrofit = builder.build();
            }
        }
        return create(serviceClass);
    }

    private static <S> S create(@NonNull Class<S> serviceClass) {
        if (BuildConfig.DEBUG && !httpClient.interceptors().contains(logging)) {
            httpClient.addInterceptor(logging);
            builder.client(httpClient.build());
            retrofit = builder.build();
        }

        return retrofit.create(serviceClass);
    }
}
