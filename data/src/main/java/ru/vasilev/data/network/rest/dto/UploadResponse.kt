package ru.vasilev.data.network.rest.dto

import com.google.gson.annotations.SerializedName

data class UploadResponse(
    @SerializedName("id")
    val id: String
)