package com.ihiabe.josh.realtor.model

data class Listing (
    val location: String = "",
    val description: String = "",
    val price: Long = -1,
    val imageUrl1: String = "",
    val imageUrl2: String = "",
    val imageUrl3: String = "",
    val imageUrl4: String = "",
    val imageUrl5: String = "",
    val isWished: Boolean = true
)