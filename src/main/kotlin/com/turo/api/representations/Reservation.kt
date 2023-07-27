package com.turo.api.representations

import com.turo.domain.ReservationSnapShot
import java.time.OffsetDateTime

data class Reservation(
    val id: String,
    val vehicleId: String,
    val customerId: String,
    val status: String,
    val startDate: OffsetDateTime,
    val endDate: OffsetDateTime
)


fun toRepresentation(reservationSnapshot: ReservationSnapShot): Reservation {
    return Reservation(
        reservationSnapshot.id,
        reservationSnapshot.vehicleId,
        reservationSnapshot.customerId,
        reservationSnapshot.status,
        reservationSnapshot.startDate,
        reservationSnapshot.endDate
    )
}