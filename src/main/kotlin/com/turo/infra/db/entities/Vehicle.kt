package com.turo.infra.db.entities

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "vehicles")
data class Vehicle(
    @Id
    val vin: String,
    val make: String,
    val model: String,
    val year: Int,
    val color: String,
    val mileage: Double
)
