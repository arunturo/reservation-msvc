package com.turo.domain.events

import com.turo.domain.DomainEvent
import java.time.OffsetDateTime
import java.util.UUID

data class ReservationCancelled(
    val eventId: UUID = UUID.randomUUID(),
    val reservationId: UUID,
    val occurredOn: OffsetDateTime = OffsetDateTime.now()
): DomainEvent {

    override fun id(): UUID {
        return eventId
    }

    override fun aggregateId(): UUID {
        return reservationId
    }

    override fun version(): Int {
        return 1
    }

    override fun occurredOn(): OffsetDateTime {
        return occurredOn
    }
}
