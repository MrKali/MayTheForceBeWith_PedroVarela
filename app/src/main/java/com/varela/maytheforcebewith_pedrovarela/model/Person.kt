package com.varela.maytheforcebewith_pedrovarela.model

data class Person (
    val birth_year: String,
    val eye_color: String,
    var films: ArrayList<String>,
    val gender: String,
    val hair_color: String,
    val height: String,
    val homeworld: String,
    val mass: String,
    val name: String,
    val skin_color: String,
    val created: String,
    val edited: String,
    var species: ArrayList<String>,
    var starships: ArrayList<String>,
    val url: String,
    var vehicles: ArrayList<String>,
    var favorite: Boolean = false
)