package ru.vasilev.data.network.rest

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import ru.vasilev.data.network.rest.dto.StatusResponse
import ru.vasilev.data.network.rest.dto.UploadResponse

interface RestApi {
    @Multipart
    @POST("/api/process")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): UploadResponse

    @GET("/api/status/{id}")
    suspend fun checkStatus(@Path("id") requestId: String): StatusResponse
}

