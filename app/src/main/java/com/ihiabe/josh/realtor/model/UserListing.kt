package com.ihiabe.josh.realtor.model

data class UserListing (
    val pushId: String = "",
    val userId: String = "",
    val location: String = "",
    val description: String = "",
    val price: Long = -1,
    val images: MutableList<String> = mutableListOf()
)