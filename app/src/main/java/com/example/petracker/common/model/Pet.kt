package com.example.petracker.common.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Pet(
    val id: String,
    val name: String,
    val species: String,
    val breed: String?,
    val sex: String,
    val weight_kg: Double?,
    val birth_date: String?,
    val owner_id: String,
    val created_at: String,
    val updated_at: String
) : Parcelable