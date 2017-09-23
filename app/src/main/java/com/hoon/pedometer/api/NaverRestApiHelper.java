package com.hoon.pedometer.api;

import android.support.annotation.NonNull;

import com.hoon.pedometer.BuildConfig;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 네이버 API + Retrofit 사용을 위한 헬퍼 클래스
 */
public class NaverRestApiHelper {

    private static final String BASE_URL = "https://openapi.naver.com/";
    private static final String NAVER_API_CLIENT_ID = "6l5ZkSUHVJfRjcH9JVSR";
    private static final String NAVER_API_CLIENT_SECRET = "L5YdnJ9vDo";

    private final Retrofit mRetrofit;

    private NaverRestApiHelper() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(BuildConfig.DEBUG ?
                HttpLoggingInterceptor.Level.BODY
                : HttpLoggingInterceptor.Level.NONE);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        OkHttpClient client = httpClient
                .addInterceptor(new HeaderInterceptor())
                .addInterceptor(loggingInterceptor)
                .build();

        mRetrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static Retrofit getRetrofit() {
        return SingletonHolder.INSTANCE.mRetrofit;
    }

    private static class HeaderInterceptor implements Interceptor {

        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder();
            requestBuilder.addHeader("X-Naver-Client-Id", NAVER_API_CLIENT_ID);
            requestBuilder.addHeader("X-Naver-Client-Secret", NAVER_API_CLIENT_SECRET);
            return chain.proceed(requestBuilder.build());
        }
    }

    private static class SingletonHolder {
        private static final NaverRestApiHelper INSTANCE = new NaverRestApiHelper();
    }

}