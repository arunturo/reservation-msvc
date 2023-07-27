package com.turo.infra.db

import com.fasterxml.jackson.databind.ObjectMapper
import com.turo.domain.DomainEvent
import com.turo.domain.DomainEventRepository
import com.turo.infra.db.entities.Event
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class JPAEventRepository(
    val eventEntityRepository: EventEntityRepository,
    val jacksonObjectMapper: ObjectMapper
): DomainEventRepository {

    override fun save(event: DomainEvent) {
        val eventEntity = toEventEntity(event, jacksonObjectMapper)
        eventEntityRepository.save(eventEntity)
    }
}

@Repository
interface EventEntityRepository : JpaRepository<Event, UUID> {
    fun findByStatus(status: String): List<Event>
}

fun toEventEntity(event: DomainEvent, objectMapper: ObjectMapper): Event {
    val payload = objectMapper.writeValueAsString(event)
    return Event(
        event.id(),
        event.aggregateId(),
        event.javaClass.simpleName,
        event.occurredOn(),
        payload,
        "pending"
    )
}