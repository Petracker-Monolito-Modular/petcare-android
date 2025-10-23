package com.example.petracker.common.model

data class Pet(
    val id: String,
    val name: String,
    val species: String,     // "felino" | "canino" | "otro"
    val breed: String?,
    val sex: String,         // "macho" | "hembra" | "desconocido"
    val weight_kg: Double?,
    val birth_date: String?, // "YYYY-MM-DD"
    val owner_id: String,
    val created_at: String,
    val updated_at: String
)

