package com.turo.infra.db

import com.turo.domain.Reservation
import com.turo.domain.ReservationRepository
import com.turo.domain.ReservationSnapShot
import com.turo.infra.db.entities.Vehicle
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class JPAReservationRepository(val reservationEntities: ReservationEntityRepository): ReservationRepository {

    override fun findById(id: String): Reservation? {

        val optionalReservation = reservationEntities.findById(id)

        return optionalReservation.map { reservation ->
            toReservationDomain(reservation, this)
        }.orElse(null)
    }

    override fun findByVehicleId(vehicleId: String): List<Reservation> {
        TODO()
    }

    override fun save(reservation: Reservation): Reservation {
        val reservationSnapshot = reservation.snapshot()
        val vehicleEntity = toVehicleEntity(reservation.vehicle.snapshot())
        val reservationEntity = toReservationEntity(reservationSnapshot, vehicleEntity)
        reservationEntities.save(reservationEntity)
        return reservation
    }
}

@Repository
interface ReservationEntityRepository: JpaRepository<com.turo.infra.db.entities.Reservation, String>

fun toReservationDomain(reservationEntity: com.turo.infra.db.entities.Reservation, reservationRepository: ReservationRepository): Reservation {
    val vehicle = toVehicleDomain(reservationEntity.vehicle, reservationRepository)
    return Reservation(
        reservationEntity.id,
        reservationEntity.customerId,
        vehicle,
        reservationEntity.startDate,
        reservationEntity.endDate,
        Reservation.ReservationStage.fromString(reservationEntity.status),
    )
}

fun toReservationEntity(reservationSnapShot: ReservationSnapShot, vehicleEntity: Vehicle): com.turo.infra.db.entities.Reservation {
    return com.turo.infra.db.entities.Reservation(
        UUID.fromString(reservationSnapShot.id),
        vehicleEntity,
        reservationSnapShot.customerId,
        reservationSnapShot.status,
        reservationSnapShot.startDate,
        reservationSnapShot.endDate
    )
}