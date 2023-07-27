package com.turo.domain

interface ReservationRepository {

    fun findById(id: String): Reservation?

    fun findByVehicleId(vehicleId: String): List<Reservation>

    fun save(reservation: Reservation): Reservation
}