package com.turo.domain

interface VehicleRepository {

    fun findById(id: String): Vehicle?

}