package com.example.petracker.common.model

data class PetsPage(
    val items: List<Pet>,
    val total: Int,
    val limit: Int,
    val offset: Int
)