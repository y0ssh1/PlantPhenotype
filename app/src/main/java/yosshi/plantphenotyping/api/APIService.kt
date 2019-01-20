package yosshi.plantphenotyping.api

import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import yosshi.plantphenotyping.model.ImageRequestBody
import yosshi.plantphenotyping.model.ImageResponseBody


interface APIService {
    @GET("/")
    fun hello(): Single<String>

    @POST("/images")
    fun postImage(@Body body: ImageRequestBody): Single<Response<ImageResponseBody>>
}