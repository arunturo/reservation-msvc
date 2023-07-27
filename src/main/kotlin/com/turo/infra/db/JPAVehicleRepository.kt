package com.turo.infra.db

import com.turo.domain.ReservationRepository
import com.turo.domain.ReservationSnapShot
import com.turo.domain.Vehicle
import com.turo.domain.VehicleRepository
import com.turo.domain.VehicleSnapShot
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
class JPAVehicleRepository(
    val vehicleEntityRepository: VehicleEntityRepository,
    val reservationRepository: ReservationRepository
): VehicleRepository {

    override fun findById(id: String): Vehicle? {

        val optionalVehicleEntity: Optional<com.turo.infra.db.entities.Vehicle> =
            vehicleEntityRepository.findById(id)

        return  optionalVehicleEntity.map { vehicle -> toVehicleDomain(vehicle, reservationRepository)
        }.orElse(null)
    }
}

@Repository
interface VehicleEntityRepository: JpaRepository<com.turo.infra.db.entities.Vehicle, String>

fun toVehicleDomain(vehicleEntity: com.turo.infra.db.entities.Vehicle, reservationRepository: ReservationRepository): Vehicle {
    return Vehicle.Builder(
        vehicleEntity.vin,
        vehicleEntity.make,
        vehicleEntity.model,
        vehicleEntity.year,
        reservationRepository
    )
        .color(vehicleEntity.color)
        .mileage(vehicleEntity.mileage)
        .build()
}

fun toVehicleEntity(vehicleSnapshot: VehicleSnapShot): com.turo.infra.db.entities.Vehicle {
    return com.turo.infra.db.entities.Vehicle(
        vehicleSnapshot.id,
        vehicleSnapshot.make,
        vehicleSnapshot.model,
        vehicleSnapshot.year,
        vehicleSnapshot.color,
        vehicleSnapshot.mileage
    )
}