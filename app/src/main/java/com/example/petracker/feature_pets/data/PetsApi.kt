package com.example.petracker.feature_pets.data

import com.example.petracker.common.model.Pet
import com.example.petracker.common.model.PetsPage
import retrofit2.http.*

data class PetCreate(
    val name: String,
    val species: String,
    val breed: String? = null,
    val sex: String = "desconocido",
    val weight_kg: Double? = null,
    val birth_date: String? = null
)

interface PetsApi {
    @GET("pets")
    suspend fun list(@Query("limit") limit: Int = 50, @Query("offset") offset: Int = 0): PetsPage
    @POST("pets")
    suspend fun create(@Body body: PetCreate): Pet
}