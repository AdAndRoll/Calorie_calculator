package ru.vasilev.data.network.rest.dto

import com.google.gson.annotations.SerializedName

data class StatusResponse(
    @SerializedName("status")
    val status: String, // "processing", "completed", "error"

    @SerializedName("result")
    val result: String? = null
)