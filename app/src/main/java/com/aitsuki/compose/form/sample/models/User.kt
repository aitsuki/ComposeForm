package com.aitsuki.compose.form.sample.models

data class User(
    val email: String,
    val occupation: String = "",
    val annualIncome: String = "",
)