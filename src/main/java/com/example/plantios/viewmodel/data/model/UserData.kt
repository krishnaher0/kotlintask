package com.example.plantios.data.model

data class UserData(
    val userId: String? = null,
    val fullName: String? = null,
    val emailAddress: String? = null,
    val phone: String? = null,
    var imageName: String? = null,
    var imageUrl: String? = null
) {
    constructor() : this("", "", "", "", "", "")
}
