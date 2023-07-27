package com.turo.domain

interface DomainEventRepository {

    fun save(event: DomainEvent)

}