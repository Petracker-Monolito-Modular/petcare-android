package com.example.petracker.feature_pets.data

import com.example.petracker.common.model.Pet
import com.example.petracker.common.model.PetsPage

class PetsRepository(private val api: PetsApi) {
    suspend fun list(limit: Int = 50, offset: Int = 0): Result<PetsPage> = try {
        Result.success(api.list(limit, offset))
    } catch (e: Exception) { Result.failure(e) }

    suspend fun create(body: PetCreate): Result<Pet> = try {
        Result.success(api.create(body))
    } catch (e: Exception) { Result.failure(e) }

    suspend fun update(id: String, body: PetCreate) = runCatching { api.update(id, body) }
    suspend fun delete(id: String) = runCatching { api.delete(id) }
}