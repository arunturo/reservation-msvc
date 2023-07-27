package com.turo.domain

import com.turo.domain.events.ReservationCancelled
import com.turo.domain.events.ReservationCancelledWithPenalty
import java.time.OffsetDateTime
import java.util.UUID

class Reservation(
    val id: UUID,
    val customerId: String,
    val vehicle: Vehicle,
    private var startDate: OffsetDateTime,
    private var endDate: OffsetDateTime,
    private var currentStage: ReservationStage
) {

    private val _domainEvents: MutableList<DomainEvent> = mutableListOf()
    val domainEvents: List<DomainEvent>
        get() = _domainEvents.toList()

    val startDateTime: OffsetDateTime
        get() = startDate
    val endDateTime: OffsetDateTime
        get() = endDate

    fun approve() {
        this.currentStage = this.currentStage.approve()
    }

    fun decline() {
        this.currentStage = this.currentStage.decline()
    }

    fun cancel() {
        this.currentStage = this.currentStage.cancel(this)
        // Raise a external domain event here to notify the customer
        raiseEvent(ReservationCancelled(reservationId = this.id))
    }

    fun expire() {
        this.currentStage = this.currentStage.expired()
    }

    fun modify(startDate: OffsetDateTime, endDate: OffsetDateTime) {
        if (!vehicle.isAvailable(startDate, endDate)) {
            throw DomainException("Vehicle is not available")
        }
        this.startDate = startDate
        this.endDate = endDate
        this.currentStage = this.currentStage.modify(startDate, endDate)
    }

    fun isWithinCancellationWindow(): Boolean {
        // Check if the reservation is within the allowed cancellation window
        val currentTime = OffsetDateTime.now()
        val cancellationWindowEnd = startDateTime.minusHours(48) // 48-hour cancellation window
        return currentTime.isBefore(cancellationWindowEnd)
    }

    fun isOverlapping(startDate: OffsetDateTime, endDate: OffsetDateTime): Boolean {
        return  this.currentStage.isActive() && this.startDate.isBefore(endDate) && this.endDate.isAfter(startDate)
    }

    fun snapshot(): ReservationSnapShot {
        return ReservationSnapShot(id.toString(), customerId, vehicle.id, startDate, endDate, currentStage.status())
    }

    private  fun raiseEvent(event: DomainEvent) {
        _domainEvents.add(event)
    }


    // even though ReservationStage follows the State pattern,
    // the naming is intentionally kept as ReservationStage to
    // keep technical jargon out of the ubiquitous language in the
    // domain layer
    abstract class ReservationStage {

        abstract fun isActive(): Boolean

        open fun approve(): ReservationStage = throw InvalidOperationException("Cannot approve a reservation in this state")

        open fun decline(): ReservationStage = throw InvalidOperationException("Cannot decline a reservation in this state")

        open fun cancel(reservation: com.turo.domain.Reservation): ReservationStage = throw InvalidOperationException("Cannot cancel a reservation in this state")

        open fun expired(): ReservationStage = throw InvalidOperationException("Cannot expire a reservation in this state")

        open fun modify(
            startDate: OffsetDateTime,
            endDate: OffsetDateTime
        ): ReservationStage = throw InvalidOperationException("Cannot modify a reservation in this state")

        fun status(): String {
            return when (this) {
                is PendingReservation -> "PENDING"
                is ApprovedReservation -> "APPROVED"
                is DeclinedReservation -> "DECLINED"
                is ExpiredReservation -> "EXPIRED"
                is CancelledReservation -> "CANCELLED"
                else -> throw IllegalArgumentException("Invalid state")
            }
        }

        companion object {
            fun fromString(state: String): ReservationStage {
                return when (state) {
                    "PENDING" -> PendingReservation()
                    "APPROVED" -> ApprovedReservation()
                    "DECLINED" -> DeclinedReservation()
                    "EXPIRED" -> ExpiredReservation()
                    "CANCELLED" -> CancelledReservation()
                    else -> throw IllegalArgumentException("Invalid state")
                }
            }
        }
    }

    class PendingReservation : ReservationStage() {

        override fun isActive() = true

        override fun approve(): ReservationStage {
            return ApprovedReservation()
        }

        override fun decline(): ReservationStage {
            return DeclinedReservation()
        }

        override fun cancel(reservation: com.turo.domain.Reservation): ReservationStage {
            return CancelledReservation()
        }

        override fun expired(): ReservationStage {
            return ExpiredReservation()
        }

        override fun modify(startDate: OffsetDateTime, endDate: OffsetDateTime): ReservationStage {
            return this
        }
    }

    class ApprovedReservation : ReservationStage() {

        override fun isActive() = true

        override fun cancel(reservation: com.turo.domain.Reservation): ReservationStage {
            if(reservation.isWithinCancellationWindow()) {
                reservation.raiseEvent(ReservationCancelledWithPenalty(reservationId = reservation.id))
            }
            return CancelledReservation()
        }

        override fun expired(): ReservationStage {
            return ExpiredReservation()
        }


    }

    class DeclinedReservation : ReservationStage() {
        override fun isActive() = false
    }

    class ExpiredReservation : ReservationStage() {
        override fun isActive() = false
    }

    class CancelledReservation : ReservationStage() {
        override fun isActive() = false
    }

}


data class ReservationSnapShot(
    val id: String,
    val customerId: String,
    val vehicleId: String,
    val startDate: OffsetDateTime,
    val endDate: OffsetDateTime,
    val status: String,
)