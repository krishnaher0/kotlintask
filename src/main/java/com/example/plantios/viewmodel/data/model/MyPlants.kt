package com.example.plantios.data.model

class MyPlants(
    val plantTitle: String? = null,
    val imageName: String? = null,
    val imageUrl: String? = null
) {
    constructor():this("", "", "")
}