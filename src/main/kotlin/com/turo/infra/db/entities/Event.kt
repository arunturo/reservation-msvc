package com.turo.infra.db.entities

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.OffsetDateTime
import java.util.*


@Entity
@Table(name = "reservation_events")
data class Event(
    @Id
    val id: UUID,
    val reservationId: UUID,
    val type: String,
    val timestamp: OffsetDateTime,
    val payload: String,
    var status: String
)
