package com.example.stunting.database.with_api.entities.checks

data class MonthlyTransactionCount(
    val year: String,
    val month: String, // "01", "02", etc.
    val count: Int
)