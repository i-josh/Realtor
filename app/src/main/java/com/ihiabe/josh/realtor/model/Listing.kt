package com.ihiabe.josh.realtor.model

data class Listing (
    val userId: String = "",
    val location: String = "",
    val description: String = "",
    val price: Long = -1,
    val images: List<String> = mutableListOf(),
    val isWished: Boolean = true
)