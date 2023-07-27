package com.turo.api.handlers

import com.turo.api.representations.Reservation
import com.turo.ApplicationService
import com.turo.api.representations.ModifyReservation
import com.turo.api.representations.toRepresentation
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
private class ReservationController(val applicationService: ApplicationService) {

    @PostMapping("/reservations")
    fun createReservation(@RequestBody reservation: Reservation): Reservation {
        val reservationSnapShot = applicationService.createReservation(reservation.customerId, reservation.vehicleId, reservation.startDate, reservation.endDate)
        return toRepresentation(reservationSnapShot)
    }

    @PatchMapping("/reservations/{id}/cancel")
    fun cancelReservation(@PathVariable id: String): Reservation {
        val cancelledReservation = applicationService.cancelReservation(id)
        return toRepresentation(cancelledReservation)
    }

    @PatchMapping("/reservations/{id}")
    fun updateReservation(@PathVariable id: String, @RequestBody reservation: ModifyReservation): Reservation {
        val updatedReservation = applicationService.modifyReservation(id, reservation.startDate, reservation.endDate)
        return toRepresentation(updatedReservation)
    }

}