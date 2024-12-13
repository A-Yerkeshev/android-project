package com.example.androidproject.network

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

// API from where points of interest around current location are fetched
interface OverpassApi {
    @GET("interpreter")
    suspend fun getPointsOfInterest(
        @Query("data") dataQuery: String
    ): PoiResponse
}

data class PoiResponse(
    val elements: List<Element>
)

data class Element(
    val id: Long,
    val lat: Double,
    val lon: Double,
    val tags: Tags?
)

data class Tags(
    val tourism: String?,
    val historic: String?,
    val name: String?,
    @SerializedName("name:fi") val nameFi: String?,
    val description: String?,
    val wikipedia: String?,
    val website: String?
)
