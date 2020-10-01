package com.sitare.imageproject;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class OkHttpCreator {
    public OkHttpClient create(final String token) {
        Interceptor interceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                return chain.proceed(chain
                        .request()
                        .newBuilder()
                        .header("Body", "multipart/form-data")
                        .header("X-Atlassian-Token", "no-check")
                        .header("Authorization", "Bearer " + token)
                        .build()
                );
            }
        };

        Interceptor loggingInterceptor = new HttpLoggingInterceptor()
                .setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

        return new OkHttpClient.Builder().addInterceptor(interceptor).addInterceptor(loggingInterceptor).build();
    }
}
