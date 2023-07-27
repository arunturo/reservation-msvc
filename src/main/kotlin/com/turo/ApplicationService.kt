package com.turo

import com.turo.domain.DomainEventRepository
import com.turo.domain.ReservationRepository
import com.turo.domain.ReservationSnapShot
import com.turo.domain.VehicleRepository
import com.turo.infra.ReservationLock
import com.turo.infra.db.EventEntityRepository
import com.turo.infra.messaging.EventDispatcher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Service
class ApplicationService(
    val vehicleRepository: VehicleRepository,
    val reservationRepository: ReservationRepository,
    val domainEventRepository: DomainEventRepository,
    val eventRepository: EventEntityRepository,
    val eventDispatcher: EventDispatcher,
    val reservationLock: ReservationLock
) {

    private val EXCHANGE_NAME = "RESERVATION_EVENTS"
    private val ROUTING_KEY = "RESERVATIONS"

    @Transactional
    fun createReservation(customerId: String, vehicleId: String, startDate: OffsetDateTime, endDate: OffsetDateTime): ReservationSnapShot {
        val vehicle = vehicleRepository.findById(vehicleId) ?: throw IllegalArgumentException("Vehicle not found")
        try {
            reservationLock.acquireVehicleLock(vehicleId)
            val reservation = vehicle.reserve(customerId, startDate, endDate)
            reservationRepository.save(reservation)
            return reservation.snapshot()
        } finally {
            reservationLock.releaseVehicleLock(vehicleId)
        }
    }

    @Transactional
    fun modifyReservation(reservationId: String, startDate: OffsetDateTime?, endDate: OffsetDateTime?): ReservationSnapShot {
        val reservation = reservationRepository.findById(reservationId)
            ?: throw IllegalArgumentException("No reservation with id found")
        try {
            reservationLock.acquireVehicleLock(reservation.vehicle.id)
            reservation.modify(startDate ?: reservation.startDateTime, endDate ?: reservation.endDateTime)
            reservationRepository.save(reservation)
            return reservation.snapshot()
        } finally {
            reservationLock.releaseVehicleLock(reservation.vehicle.id)
        }
    }


    @Transactional
    fun cancelReservation(reservationId: String): ReservationSnapShot {
        val reservation = reservationRepository.findById(reservationId)
            ?: throw IllegalArgumentException("No reservation with id found")
        reservation.cancel()
        reservation.domainEvents.forEach(domainEventRepository::save)
        return reservation.snapshot()
    }


    @Transactional
    // Store and forward pattern
    fun publishDomainEvents() {
        eventRepository.findByStatus("pending").forEach {
            eventDispatcher.dispatchEvent(EXCHANGE_NAME, ROUTING_KEY, it.payload)
            it.status= "published"
            eventRepository.save(it)
        }
    }

}