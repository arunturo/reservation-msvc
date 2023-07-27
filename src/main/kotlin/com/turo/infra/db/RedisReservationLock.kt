package com.turo.infra.db

import com.turo.infra.ReservationLock
import org.springframework.stereotype.Component

@Component
class RedisReservationLock: ReservationLock {

    override fun acquireVehicleLock(vehicleId: String): Boolean {
        // Implement reentrant lock
        TODO()
    }

    override fun releaseVehicleLock(vehicleId: String): Boolean {
        TODO()
    }
}