package com.turo.domain

import java.time.OffsetDateTime
import java.util.*

class Vehicle private constructor(
    val id: String,
    val make: String,
    val model: String,
    val year: Int,
    val color: String,
    val mileage: Double,
    private val reservationsRepo: ReservationRepository
) {

    fun isAvailable(startDate: OffsetDateTime, endDate: OffsetDateTime): Boolean {
        reservationsRepo.findByVehicleId(id).forEach { reservation ->
            if (reservation.isOverlapping(startDate, endDate)) {
                return false
            }
        }
        // check maintenance schedule and other domain logic
        // ...
        return true
    }

    fun reserve(customerId: String, startDate: OffsetDateTime, endDate: OffsetDateTime): Reservation {
        if (!isAvailable(startDate, endDate)) {
            throw IllegalArgumentException("Vehicle is not available")
        }
        val reservation = Reservation(
            UUID.randomUUID(),customerId, this, startDate, endDate, Reservation.PendingReservation())
        return reservation
    }

    fun availableIntervals(): List<Pair<OffsetDateTime, OffsetDateTime>> {
        //TODO:  retrieve intervals where the vehicle is available for reservation
        return emptyList()
    }

    fun snapshot(): VehicleSnapShot {
        return VehicleSnapShot(id.toString(), make, model, year, color, mileage)
    }

    // Builder class
    class Builder(
        private val id: String,
        private val make: String,
        private val model: String,
        private val year: Int,
        private val reservationsRepo: ReservationRepository
    ) {
        private var color: String = ""
        private var mileage: Double = 0.0

        fun color(color: String): Builder {
            this.color = color
            return this
        }

        fun mileage(mileage: Double): Builder {
            this.mileage = mileage
            return this
        }

        fun build(): Vehicle {
            return Vehicle(id, make, model, year, color, mileage, reservationsRepo)
        }
    }
}

data class VehicleSnapShot(
    val id: String,
    val make: String,
    val model: String,
    val year: Int,
    val color: String,
    val mileage: Double
)