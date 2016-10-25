package com.sparkles.dietanalytics.rest;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by pavanibaradi on 10/12/16.
 */
public class RestAPIClient {

    private static RestAPIInterface REST_CLIENT;
    private static final String API_URL = "http://dietanalytics.2vyy2nnzmq.us-west-2.elasticbeanstalk.com/rest/";

    public RestAPIClient() {
    }

    public static RestAPIInterface get() {
        if(REST_CLIENT==null){
            setupRestClient();
        }
        return REST_CLIENT;
    }

    public static void setupRestClient() {
        Interceptor interceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder().addHeader("User-Agent", "Retrofit-Sample-App").build();
                return chain.proceed(newRequest);
            }
        };

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(interceptor);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        REST_CLIENT = retrofit.create(RestAPIInterface.class);
    }
}
