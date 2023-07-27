package com.turo.infra

interface ReservationLock {

    fun acquireVehicleLock(vehicleId: String): Boolean

    fun releaseVehicleLock(vehicleId: String): Boolean
}