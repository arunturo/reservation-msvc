package com.turo.infra.db.entities

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "reservations")
data class Reservation(
    @Id
    val id: UUID,
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id")
    val vehicle: Vehicle,
    val customerId: String,
    val status: String,
    val startDate: OffsetDateTime,
    val endDate: OffsetDateTime,
)