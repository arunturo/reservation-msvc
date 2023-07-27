package com.turo.domain

import java.time.OffsetDateTime
import java.util.UUID

interface DomainEvent {
    fun id(): UUID
    fun aggregateId(): UUID
    fun version(): Int
    fun occurredOn(): OffsetDateTime
}