package com.vinay.movielistsample.di

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.vinay.movielistsample.data.api.MoviesApi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class ApiModule {

    @Singleton
    @Provides
    fun provideRetrofit(
        gsonConverter: GsonConverterFactory,
        httpClient: OkHttpClient,
        baseUrl: String
    ): Retrofit {
        return Retrofit.Builder().baseUrl(baseUrl).client(httpClient)
            .addConverterFactory(gsonConverter)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build()
    }

    @Singleton
    @Provides
    fun provideGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create(
            GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()
        )
    }

    @Singleton
    @Provides
    fun provideMoviesApi(retrofit: Retrofit): MoviesApi {
        return buildApiClient(retrofit, MoviesApi.MODULE_PATH).create(MoviesApi::class.java)
    }

    @Singleton
    @Provides
    fun provideBaseUrl(): String {
        return BASE_URL
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        val httpClient = OkHttpClient.Builder().apply {
            readTimeout(HTTP_TIMEOUT, TimeUnit.SECONDS)
            connectTimeout(HTTP_TIMEOUT, TimeUnit.SECONDS)
        }
        httpClient.addInterceptor { chain ->
            val ongoing = chain.request().newBuilder()
            ongoing.addHeader("Content-Type", "application/json")
            ongoing.addHeader("Accept", "application/json")
            ongoing.addHeader("Authorization", "Bearer $API_TOKEN")
            val response = chain.proceed(ongoing.build())
            response
        }
        httpClient.addNetworkInterceptor(loggingInterceptor)
        return httpClient.build()
    }

    @Singleton
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    private fun buildApiClient(retrofit: Retrofit, path: String): Retrofit {
        return retrofit.newBuilder().baseUrl(retrofit.baseUrl().url().toString() + path)
            .validateEagerly(true).build()
    }

    companion object {
        private const val HTTP_TIMEOUT = 40L
        private const val BASE_URL = "https://api.themoviedb.org/4/"
        const val API_KEY = "fd955e6d0f621c2a9400ea5ace6650d3"
        private const val API_TOKEN =
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJmZDk1NWU2ZDBmNjIxYzJhOTQwMGVhNWFjZTY2NTBkMyIsInN1YiI6IjVjZTk0M2IwMGUwYTI2NTE4M2NhN2I2MiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.pNN5MdvS-RY19kMHLV25Ku0ixloO2Ikdh7Y55-iFlfM"
    }
}
