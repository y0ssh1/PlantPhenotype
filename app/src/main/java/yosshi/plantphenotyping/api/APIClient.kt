package yosshi.plantphenotyping.api

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class APIClient {

    companion object {
        val instance: APIService = Retrofit.Builder()
            .baseUrl("") // Implement target api url
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(httpClient)
            .build()
            .create(APIService::class.java)

        private val gson: Gson
            get() = GsonBuilder()
            .setLenient()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()

        private val httpClient: OkHttpClient
            get() = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(20, TimeUnit.MINUTES)
                .readTimeout(20, TimeUnit.MINUTES)
                .writeTimeout(20, TimeUnit.MINUTES)
                .build()

        private val interceptor: Interceptor
            get() = Interceptor {
            val request = it.request()
                .newBuilder()
                .addHeader("x-api-key", "")
                .build()
            it.proceed(request)
            }
    }

}

