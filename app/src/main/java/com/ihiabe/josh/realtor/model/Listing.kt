package com.ihiabe.josh.realtor.model

data class Listing (
    val pushId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userPhoneNumber: String = "",
    val location: String = "",
    val description: String = "",
    val price: Long = -1,
    val images: MutableList<String> = mutableListOf(),
    val bookmarked: Map<String,Boolean> = mapOf()
)